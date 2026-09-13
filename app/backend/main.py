import argparse
import json
import os
import re
import sys
from typing import List, Optional

import numpy as np
import torch
import torch.nn as nn
from tokenizers import Tokenizer

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel


URL_RE = re.compile(r"https?://\S+|www\.\S+")
WS_RE = re.compile(r"\s+")

NEGATION_CUES_RE = re.compile(
    r"\b(not|never|no|cannot|hardly|barely|without|neither|nor)\b|\w+n['\u2019]t\b",
    re.IGNORECASE,
)


def clean_text(text):
    text = URL_RE.sub(" <URL> ", text)
    text = WS_RE.sub(" ", text).strip()
    return text


def compute_negation_scope_mask(text, offsets, scope_chars):
    scope_ranges = [(m.end(), m.end() + scope_chars) for m in NEGATION_CUES_RE.finditer(text)]
    mask = []
    for (s, _e) in offsets:
        in_scope = any(rs <= s < re_ for rs, re_ in scope_ranges)
        mask.append(1 if in_scope else 0)
    return mask

class Attention(nn.Module):
    def __init__(self, hidden_dim):
        super().__init__()
        self.attn = nn.Linear(hidden_dim, 1)

    def forward(self, lstm_out, pad_mask):
        scores = self.attn(lstm_out).squeeze(-1)
        scores = scores.masked_fill(pad_mask == 0, -1e9)
        weights = torch.softmax(scores, dim=1)
        context = torch.bmm(weights.unsqueeze(1), lstm_out).squeeze(1)
        return context, weights


class ContextAwareBiLSTMClassifier(nn.Module):
    def __init__(self, vocab_size, embed_dim, hidden_dim, num_labels,
                 neg_embed_dim=8, dropout=0.3, pad_idx=0):
        super().__init__()
        self.pad_idx = pad_idx
        self.embedding = nn.Embedding(vocab_size, embed_dim, padding_idx=pad_idx)
        self.neg_scope_embedding = nn.Embedding(2, neg_embed_dim)
        self.lstm = nn.LSTM(embed_dim + neg_embed_dim, hidden_dim, batch_first=True, bidirectional=True)
        self.attention = Attention(hidden_dim * 2)
        self.dropout = nn.Dropout(dropout)
        self.fc = nn.Linear(hidden_dim * 2, num_labels)

    def _encode_sequence(self, x, neg_scope):
        pad_mask = (x != self.pad_idx).float()
        word_embed = self.embedding(x)
        neg_embed = self.neg_scope_embedding(neg_scope)
        combined = torch.cat([word_embed, neg_embed], dim=-1)
        lstm_out, _ = self.lstm(combined)
        context, attn_weights = self.attention(lstm_out, pad_mask)
        return context, attn_weights

    def forward(self, x, neg_scope, return_attention=False):
        context, attn_weights = self._encode_sequence(x, neg_scope)
        context = self.dropout(context)
        logits = self.fc(context)
        if return_attention:
            return logits, attn_weights
        return logits

    def encode(self, x, neg_scope):
        context, _ = self._encode_sequence(x, neg_scope)
        return context

def get_toxicity_level(toxicity_score):
    if toxicity_score < 0.60:
        return {"level": "safe", "blur": 0, "message": "No blur required."}
    elif toxicity_score < 0.85:
        return {"level": "moderate", "blur": 1, "message": "Content temporarily blurred due to potentially harmful language."}
    else:
        return {"level": "high", "blur": 2, "message": "Content heavily blurred due to highly toxic or harmful language."}


def get_toxicity_warning(toxicity_score):
    if toxicity_score < 0.60:
        return {"show_warning": False, "title": "Content appears safe",
                "reason": "The detected toxicity level is below the warning threshold."}
    elif toxicity_score < 0.85:
        return {"show_warning": True, "title": "Potentially harmful content",
                "reason": "This content may contain offensive, abusive, or inappropriate language. It has been temporarily blurred."}
    else:
        return {"show_warning": True, "title": "Highly toxic content",
                "reason": "This content has a high probability of containing severely offensive, abusive, threatening, or harmful language. It has been heavily blurred for safety."}


def get_age_rating(toxicity_score):
    if toxicity_score < 0.60:
        return {"age_rating": "13-15", "level": "Low"}
    elif toxicity_score < 0.70:
        return {"age_rating": "15-18", "level": "Moderate"}
    elif toxicity_score < 0.85:
        return {"age_rating": "18-21", "level": "High"}
    else:
        return {"age_rating": "21+", "level": "Very High"}


def build_rating_payload(toxicity_score):
    tox_level = get_toxicity_level(toxicity_score)
    warning = get_toxicity_warning(toxicity_score)
    age = get_age_rating(toxicity_score)
    return {
        "ageRating": age["age_rating"],
        "is_sensitive": warning["show_warning"],
        "toxicity_rating": tox_level["level"],
        "message": tox_level["message"],
        "blur_level": tox_level["blur"],
        "warning_title": warning["title"],
        "warning_reason": warning["reason"],
        "age_rating_level": age["level"],
    }


SEVERITY_ORDER = ["hate", "harassment", "abuse", "toxicity"]

GUIDELINE_NOTE = {
    "toxicity":   "Community Guidelines: keep feedback focused on ideas, not people.",
    "hate":       "Community Guidelines: content targeting people based on identity is not allowed.",
    "harassment": "Community Guidelines: repeated targeting or threats toward a person are not allowed.",
    "abuse":      "Community Guidelines: personal insults and degrading language are not allowed.",
}


def cosine_sim_matrix(query, bank_matrix):
    q = query / (np.linalg.norm(query) + 1e-9)
    b = bank_matrix / (np.linalg.norm(bank_matrix, axis=1, keepdims=True) + 1e-9)
    return b @ q

class ToxicityModerator:
    def __init__(self, artifacts_dir="artifacts", device=None):
        self.device = device or torch.device("cuda" if torch.cuda.is_available() else "cpu")

        with open(os.path.join(artifacts_dir, "model_config.json")) as f:
            self.config = json.load(f)

        with open(os.path.join(artifacts_dir, "thresholds.json")) as f:
            self.thresholds = json.load(f)

        with open(os.path.join(artifacts_dir, "suggestion_bank.json")) as f:
            self.suggestion_bank = json.load(f)

        self.labels = self.config["labels"]
        self.max_len = self.config["max_len"]
        self.scope_chars = self.config["scope_chars"]
        self.pad_id = self.config["pad_id"]

        self.tokenizer = Tokenizer.from_file(os.path.join(artifacts_dir, "bpe_tokenizer.json"))

        self.model = ContextAwareBiLSTMClassifier(
            vocab_size=self.config["vocab_size"],
            embed_dim=self.config["embed_dim"],
            hidden_dim=self.config["hidden_dim"],
            num_labels=self.config["num_labels"],
            neg_embed_dim=self.config["neg_embed_dim"],
            dropout=self.config["dropout"],
            pad_idx=self.pad_id,
        ).to(self.device)
        state_dict = torch.load(
            os.path.join(artifacts_dir, "toxicity_model_v4.pt"), map_location=self.device
        )
        self.model.load_state_dict(state_dict)
        self.model.eval()

        self.suggestion_bank_embeddings = np.stack(
            [self._embed_text(e["trigger_context"]) for e in self.suggestion_bank]
        )

    def _encode_with_features(self, text):
        enc = self.tokenizer.encode(text)
        ids = enc.ids[: self.max_len]
        offsets = enc.offsets[: self.max_len]
        neg_scope = compute_negation_scope_mask(text, offsets, self.scope_chars)
        pad_len = self.max_len - len(ids)
        ids = ids + [self.pad_id] * pad_len
        neg_scope = neg_scope + [0] * pad_len
        return ids, neg_scope

    def _to_tensors(self, text):
        ids, neg_scope = self._encode_with_features(text)
        x = torch.tensor([ids], dtype=torch.long, device=self.device)
        neg = torch.tensor([neg_scope], dtype=torch.long, device=self.device)
        return x, neg

    def _embed_text(self, text):
        cleaned = clean_text(text)
        x, neg = self._to_tensors(cleaned)
        with torch.no_grad():
            vec = self.model.encode(x, neg)[0].cpu().numpy()
        return vec

    def predict(self, text, top_n=5):
        cleaned = clean_text(text)
        x, neg = self._to_tensors(cleaned)

        with torch.no_grad():
            logits, attn = self.model(x, neg, return_attention=True)
            probs = torch.sigmoid(logits)[0].cpu().tolist()

        per_label = {
            label: {"prob": round(p, 4), "flagged": bool(p >= self.thresholds[label])}
            for label, p in zip(self.labels, probs)
        }
        overall = 1 - float(np.prod([1 - p for p in probs]))

        tokens = self.tokenizer.encode(cleaned).tokens[: self.max_len]
        weights = attn[0][: len(tokens)].cpu().tolist()
        top_tokens = sorted(zip(tokens, weights), key=lambda t: -t[1])[:top_n]

        return {
            "per_label": per_label,
            "overall_toxicity": round(overall, 4),
            "any_flagged": any(v["flagged"] for v in per_label.values()),
            "top_attended_tokens": [{"token": t, "weight": round(w, 4)} for t, w in top_tokens],
        }

    def suggest_alternative(self, text, per_label):
        flagged_categories = [c for c in SEVERITY_ORDER if per_label.get(c, {}).get("flagged")]
        if not flagged_categories:
            return None
        primary_category = flagged_categories[0]

        candidate_idxs = [
            i for i, e in enumerate(self.suggestion_bank) if e["category"] == primary_category
        ]
        if not candidate_idxs:
            candidate_idxs = list(range(len(self.suggestion_bank)))

        query_vec = self._embed_text(text)
        sims = cosine_sim_matrix(query_vec, self.suggestion_bank_embeddings[candidate_idxs])
        best_local = int(np.argmax(sims))
        best_idx = candidate_idxs[best_local]
        best_entry = self.suggestion_bank[best_idx]

        return {
            "matched_category": primary_category,
            "similarity": round(float(sims[best_local]), 4),
            "suggested_rewrite": best_entry["suggested_rewrite"],
            "guideline_note": GUIDELINE_NOTE[primary_category],
        }

    def moderate(self, text, top_n=5):
        result = self.predict(text, top_n=top_n)
        rating = build_rating_payload(result["overall_toxicity"])
        suggestion = (
            self.suggest_alternative(text, result["per_label"]) if result["any_flagged"] else None
        )
        return {
            "input_text": text,
            **result,
            "rating": rating,
            "suggestion": suggestion,
        }


# --------------------------------------------------------------------------
# FastAPI serving layer
#
# This block adds an HTTP API on top of ToxicityModerator, mirroring the
# /health, /classify, /classify-batch shape from the v2 server so the
# Android app's Retrofit client doesn't need to change its contract.
#
# Run it with:
#   uvicorn main:app --host 0.0.0.0 --port 8000
#
# The model loads once, lazily, on the first request (or immediately on
# startup - see the startup event below), NOT every request.
# --------------------------------------------------------------------------

ARTIFACTS_DIR = os.environ.get("ARTIFACTS_DIR", "artifacts")

app = FastAPI(title="Toxicity Moderation API", version="4.0")

_moderator: Optional[ToxicityModerator] = None


def get_moderator() -> ToxicityModerator:
    global _moderator
    if _moderator is None:
        _moderator = ToxicityModerator(artifacts_dir=ARTIFACTS_DIR)
    return _moderator


@app.on_event("startup")
def _load_model_on_startup():
    # Load the model once when the server boots, so the first real request
    # isn't the one paying the (multi-second) model + tokenizer load cost.
    get_moderator()


class ClassifyRequest(BaseModel):
    text: str
    top_n: Optional[int] = 5


class ClassifyBatchRequest(BaseModel):
    texts: List[str]
    top_n: Optional[int] = 5


@app.get("/health")
def health():
    m = get_moderator()
    return {"status": "ok", "labels": m.labels, "device": str(m.device)}


@app.post("/classify")
def classify(req: ClassifyRequest):
    if not req.text or not req.text.strip():
        raise HTTPException(status_code=400, detail="text must not be empty")
    m = get_moderator()
    return m.moderate(req.text, top_n=req.top_n or 5)


@app.post("/classify-batch")
def classify_batch(req: ClassifyBatchRequest):
    if not req.texts:
        raise HTTPException(status_code=400, detail="texts must not be empty")
    m = get_moderator()
    return {"results": [m.moderate(t, top_n=req.top_n or 5) for t in req.texts]}


def main():
    parser = argparse.ArgumentParser(description="BiLSTM toxicity moderation (no transformers).")
    parser.add_argument("text", nargs="?", help="Text to moderate. If omitted, reads lines from stdin.")
    parser.add_argument("--artifacts-dir", default="artifacts", help="Directory with trained artifacts.")
    parser.add_argument("--serve", action="store_true", help="Run the FastAPI server instead of the CLI.")
    parser.add_argument("--host", default="0.0.0.0", help="Host to bind when --serve is used.")
    parser.add_argument("--port", type=int, default=8000, help="Port to bind when --serve is used.")
    args = parser.parse_args()

    if args.serve:
        import uvicorn
        os.environ["ARTIFACTS_DIR"] = args.artifacts_dir
        uvicorn.run("main:app", host=args.host, port=args.port)
        return

    moderator = ToxicityModerator(artifacts_dir=args.artifacts_dir)

    if args.text is not None:
        print(json.dumps(moderator.moderate(args.text), indent=2))
        return

    for line in sys.stdin:
        line = line.rstrip("\n")
        if not line.strip():
            continue
        print(json.dumps(moderator.moderate(line), indent=2))


if __name__ == "__main__":
    main()
