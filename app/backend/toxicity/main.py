import json
import os
import re
from typing import List, Optional

import numpy as np
import torch
import torch.nn as nn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

MODEL_PATH = os.environ.get("MODEL_PATH", "toxicity_model_v2.pt")
TOKENIZER_PATH = os.environ.get("TOKENIZER_PATH", "bpe_tokenizer.json")
THRESHOLDS_PATH = os.environ.get("THRESHOLDS_PATH", "thresholds.json")
CONFIG_PATH = os.environ.get("CONFIG_PATH", "model_config_v2.json")

DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

URL_RE = re.compile(r"https?://\S+|www\.\S+")
WS_RE = re.compile(r"\s+")
DEVANAGARI_RE = re.compile(r"[\u0900-\u097F]")
LATIN_RE = re.compile(r"[a-zA-Z]")

def clean_text(text: str) -> str:
    text = URL_RE.sub(" <URL> ", text)
    text = WS_RE.sub(" ", text).strip()
    return text

def script_features(text: str) -> List[float]:
    n = max(len(text), 1)
    n_deva = len(DEVANAGARI_RE.findall(text))
    n_latin = len(LATIN_RE.findall(text))
    deva_frac = n_deva / n
    latin_frac = n_latin / n
    is_code_mixed = float(n_deva > 0 and n_latin > 0)
    return [deva_frac, latin_frac, is_code_mixed]

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

class BiLSTMAttnToxicityClassifier(nn.Module):
    def __init__(self, vocab_size, embed_dim, hidden_dim, num_labels,
                 n_script_feats=3, dropout=0.3, pad_idx=0):
        super().__init__()
        self.pad_idx = pad_idx
        self.embedding = nn.Embedding(vocab_size, embed_dim, padding_idx=pad_idx)
        self.lstm = nn.LSTM(embed_dim, hidden_dim, batch_first=True, bidirectional=True)
        self.attention = Attention(hidden_dim * 2)
        self.dropout = nn.Dropout(dropout)
        self.fc = nn.Linear(hidden_dim * 2 + n_script_feats, num_labels)

    def forward(self, x, script_feats, return_attention=False):
        pad_mask = (x != self.pad_idx).float()
        embedded = self.embedding(x)
        lstm_out, _ = self.lstm(embedded)
        context, attn_weights = self.attention(lstm_out, pad_mask)
        context = self.dropout(context)
        fused = torch.cat([context, script_feats], dim=1)
        logits = self.fc(fused)
        if return_attention:
            return logits, attn_weights
        return logits

with open(CONFIG_PATH, "r") as f:
    CONFIG = json.load(f)

with open(THRESHOLDS_PATH, "r") as f:
    THRESHOLDS = json.load(f)

LABELS = CONFIG["labels"]
MAX_LEN = CONFIG["max_len"]

from tokenizers import Tokenizer
_raw_tokenizer = Tokenizer.from_file(TOKENIZER_PATH)

PAD_ID = _raw_tokenizer.token_to_id("<PAD>")
if PAD_ID is None:
    PAD_ID = 0


def encode(text: str, max_len: int = MAX_LEN) -> List[int]:
    ids = _raw_tokenizer.encode(text).ids[:max_len]
    ids += [PAD_ID] * (max_len - len(ids))
    return ids


def tokenize_for_display(text: str, max_len: int = MAX_LEN) -> List[str]:
    return _raw_tokenizer.encode(text).tokens[:max_len]


model = BiLSTMAttnToxicityClassifier(
    vocab_size=CONFIG["vocab_size"],
    embed_dim=CONFIG["embed_dim"],
    hidden_dim=CONFIG["hidden_dim"],
    num_labels=CONFIG["num_labels"],
    dropout=CONFIG["dropout"],
    pad_idx=PAD_ID,
).to(DEVICE)

model.load_state_dict(torch.load(MODEL_PATH, map_location=DEVICE))
model.eval()

def get_toxicity_level(toxicity_score):

    if toxicity_score < 0.60:
        return {
            "level": "safe",
            "blur": 0,
            "message": "No blur required."
        }

    elif toxicity_score < 0.85:
        return {
            "level": "moderate",
            "blur": 1,
            "message": "Content temporarily blurred due to potentially harmful language."
        }

    else:
        return {
            "level": "high",
            "blur": 2,
            "message": "Content heavily blurred due to highly toxic or harmful language."
        }


def get_toxicity_warning(toxicity_score):

    if toxicity_score < 0.60:

        return {
            "show_warning": False,
            "title": "Content appears safe",
            "reason": "The detected toxicity level is below the warning threshold."
        }

    elif toxicity_score < 0.85:

        return {
            "show_warning": True,
            "title": "Potentially harmful content",
            "reason": (
                "This content may contain offensive, abusive, "
                "or inappropriate language. It has been temporarily blurred."
            )
        }

    else:

        return {
            "show_warning": True,
            "title": "Highly toxic content",
            "reason": (
                "This content has a high probability of containing "
                "severely offensive, abusive, threatening, or harmful language. "
                "It has been heavily blurred for safety."
            )
        }


def get_age_rating(toxicity_score):

    if toxicity_score < 0.60:
        return {
            "age_rating": "13-15",
            "level": "Low"
        }

    elif toxicity_score < 0.70:
        return {
            "age_rating": "15-18",
            "level": "Moderate"
        }

    elif toxicity_score < 0.85:
        return {
            "age_rating": "18-21",
            "level": "High"
        }

    else:
        return {
            "age_rating": "21+",
            "level": "Very High"
        }


def build_rating_payload(toxicity_score: float) -> dict:
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

def predict(text: str, top_n: int = 5):
    cleaned = clean_text(text)
    ids = encode(cleaned)
    sf = script_features(cleaned)

    x = torch.tensor([ids], dtype=torch.long).to(DEVICE)
    sf_t = torch.tensor([sf], dtype=torch.float32).to(DEVICE)

    with torch.no_grad():
        logits, attn = model(x, sf_t, return_attention=True)
        probs = torch.sigmoid(logits)[0].cpu().tolist()

    per_label = {}
    for label, p in zip(LABELS, probs):
        per_label[label] = {"prob": round(p, 4), "flagged": bool(p >= THRESHOLDS[label])}

    overall = 1 - float(np.prod([1 - p for p in probs]))

    tokens = tokenize_for_display(cleaned)
    weights = attn[0][: len(tokens)].cpu().tolist()
    top_tokens = sorted(zip(tokens, weights), key=lambda t: -t[1])[:top_n]

    result = {
        "per_label": per_label,
        "overall_toxicity": round(overall, 4),
        "any_flagged": any(v["flagged"] for v in per_label.values()),
        "top_attended_tokens": [{"token": t, "weight": round(w, 4)} for t, w in top_tokens],
    }
    result.update(build_rating_payload(overall))
    return result

app = FastAPI(title="Toxicity Classifier API", version="1.0")


class ClassifyRequest(BaseModel):
    text: str
    top_n: Optional[int] = 5


class ClassifyBatchRequest(BaseModel):
    texts: List[str]
    top_n: Optional[int] = 5


@app.get("/health")
def health():
    return {"status": "ok", "labels": LABELS, "device": str(DEVICE)}


@app.post("/classify")
def classify(req: ClassifyRequest):
    if not req.text or not req.text.strip():
        raise HTTPException(status_code=400, detail="text must not be empty")
    return predict(req.text, top_n=req.top_n or 5)


@app.post("/classify-batch")
def classify_batch(req: ClassifyBatchRequest):
    if not req.texts:
        raise HTTPException(status_code=400, detail="texts must not be empty")
    return {"results": [predict(t, top_n=req.top_n or 5) for t in req.texts]}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)