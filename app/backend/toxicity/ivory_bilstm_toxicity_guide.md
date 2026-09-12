# IVORY — Toxicity Classifier (BiLSTM Plan): Full Build Guide

One thing to flag before the plan itself: this is your **second pivot** on the model architecture (TF-IDF baseline → transformer, now → BiLSTM-from-scratch-tokenizer). That's not a criticism of the choice — BiLSTM is a genuinely reasonable middle ground for a 12-hour budget, more defensible as "built by us" than a fine-tuned pretrained transformer, and lighter to run than DistilBERT. But you need to *pick this and stop switching*, because every switch resets the "do I actually understand this" clock back to zero, and that clock is what your mentor is going to test.

---

## PART 0 — Topics you need before writing code

This build uses none of the TF-IDF/transformer machinery from before. Different foundations:

| Topic | Why it matters here |
|---|---|
| **RNNs → LSTM → BiLSTM, conceptually** | Plain RNNs forget long-range context (vanishing gradient). LSTM adds gates (forget/input/output) to control what's remembered. Bidirectional = one LSTM reads left→right, another right→left, outputs concatenated — so "you're not an idiot" gets context from both directions before deciding. |
| **Why embeddings, not TF-IDF, feed an LSTM** | LSTM needs a sequence of vectors *in order* — TF-IDF collapses order into a bag of counts, which throws away exactly what a sequence model needs. Embedding layer maps each token id → dense vector, learned during training (or initialized from pretrained GloVe if you have time). |
| **Vocabulary building from scratch** | You are not using a pretrained tokenizer this time. You build a word→integer mapping from your training data only. This means you must handle unseen words at inference time yourself (see next row) — a pretrained tokenizer's subword handling would have solved this for you; without it, it's your problem. |
| **OOV (out-of-vocabulary) handling** | Any word not seen during training maps to an `<UNK>` token. This directly affects performance on slang, typos, and — relevant to your Hindi/Hinglish test case — romanized non-English words the vocab never saw. Know this is a real limitation, not a bug you can "just fix." |
| **Padding & sequence length** | Sentences vary in length; batches need equal-length tensors. Pick a `MAX_LEN`, pad short sequences with a `<PAD>` token, truncate long ones. Padding tokens must not influence the LSTM's hidden state — that's what `padding_idx` in the embedding layer and (optionally) packed sequences are for. |
| **Embedding layer as a trainable lookup table** | `nn.Embedding(vocab_size, embed_dim, padding_idx=0)` — this is a matrix of shape `(vocab_size, embed_dim)` that gets updated by backprop like any other weight, unless you freeze it. |
| **Dropout, and where it goes** | Regularization to prevent overfitting on a modest dataset. Goes after the LSTM output and/or between LSTM layers, not on the raw embeddings usually. |
| **Loss function for single-score vs multi-label output** | If output is one `toxicity_score` → `BCEWithLogitsLoss` (binary). If output is multiple category scores (insult/harassment/hate/threat) → still `BCEWithLogitsLoss` but applied per-label independently, same reasoning as before: these are not mutually exclusive classes, so no softmax/cross-entropy. |
| **Training loop mechanics in PyTorch** | `DataLoader` batching, `optimizer.zero_grad()` → `loss.backward()` → `optimizer.step()`, tracking train vs. validation loss per epoch, and what it looks like when they diverge (overfitting). |
| **Early stopping** | Stop training when validation loss stops improving, even if training loss keeps dropping — otherwise you're memorizing the training set. |
| **Confusion matrix + false negatives specifically** | For a safety product, a missed toxic post (false negative) is worse than a false positive. You need to look at recall on the toxic class specifically, not just overall accuracy. |
| **Class imbalance in a from-scratch setup** | `pos_weight` argument in `BCEWithLogitsLoss` (upweights the rare positive class) or oversampling the minority class in your `Dataset`. You don't get sklearn's `class_weight="balanced"` for free here — you compute the weight yourself from label frequencies. |
| **Saving a stateful pipeline, not just weights** | Unlike a HuggingFace model, your vocabulary is *not* baked into the model file. You must save the vocab (word→id dict) and the config (`MAX_LEN`, embedding dim, hidden size) alongside the model weights, or inference will silently break with a fresh, empty vocabulary. This is the single most common "works in notebook, dies in API" bug for this architecture. |
| **Decision layer producing multiple derived fields** | Your target output includes `toxicity_score`, `why_is_it_toxic`, `ageRating`, `is_sensitive`. Only `toxicity_score` (and category scores, if multi-label) come from the model. Everything else — the human-readable reason, the age rating, the sensitivity flag — is business logic you write in the service layer from those scores. Never let the model try to output a string reason directly. |

---

## PART 1 — Tech stack

```text
Language:         Python 3.10+
Data handling:     pandas, numpy
Deep learning:     PyTorch (nn.Embedding, nn.LSTM with bidirectional=True, nn.Linear)
Tokenization:      custom — no HuggingFace tokenizer this time
Evaluation:        scikit-learn.metrics (precision_recall_fscore_support, confusion_matrix)
Serialization:     torch.save (model state_dict) + a saved vocab.json + config.json
Backend:           FastAPI + Pydantic (kept consistent with your existing app/backend/ structure)
Environment:       venv + requirements.txt
Notebook env:      Jupyter, for Phases 1–10 of your plan; production code lives in app/backend + ml/toxicity/src
```

No GPU strictly required — a BiLSTM on a modest vocab/dataset trains reasonably on CPU within a 12-hour budget, though a free Colab GPU will make iteration faster if available.

---

## PART 2 — Dataset (same as before — don't change this too)

Keep using the Jigsaw Toxic Comment dataset, same label mapping discussed previously (`toxic`/`severe_toxic` → toxicity, `identity_hate` → hate, `insult`/`threat` → harassment, `obscene`/`severe_toxic` → abuse), unless you've specifically found a Hindi/Hinglish-inclusive dataset. If Hindi/Hinglish support actually matters for your demo (you mentioned wanting to test it), be upfront in your presentation that a vocabulary trained purely on English Jigsaw data will perform poorly on Hinglish — that's a direct, predictable consequence of Part 0's OOV row, not a mysterious bug. If you have hours to spare, look for a Hinglish-labeled hate/offensive speech dataset (e.g. HASOC Hindi-English code-mixed tasks) and mix a small amount in — but don't attempt this unless the baseline pipeline is already solid.

---

## PART 3 — Workflow (matches your mentor's 13 phases, mapped to concrete files)

```text
1.  Load + inspect dataset                       → notebooks/01_explore.ipynb
2.  Clean text                                    → src/data_utils.py: clean_text()
3.  Train/val/test split (70/15/15)               → src/data_utils.py: split_dataset()
4.  Build vocabulary + tokenizer from training set only → src/vocab.py
5.  Build BiLSTM model                            → src/model.py
6.  Build PyTorch Dataset/DataLoader               → src/dataset.py
7.  Train with early stopping                     → src/train.py
8.  Evaluate (precision/recall/F1, confusion matrix, false negatives) → src/evaluate.py
9.  Improve only if numbers are bad (imbalance/seq-len/hidden-size/dropout/epochs, in that order) → back to train.py
10. Save model weights + vocab.json + config.json → models/
11. Build inference wrapper                       → app/backend/models/toxicity/toxicity_model.py
12. Build decision layer (score → why/ageRating/is_sensitive) → app/backend/services/toxicity_service.py
13. Wire into FastAPI endpoint                    → app/backend/api/routes/content.py
14. Test with the exact example set from Phase 12 of your plan (safe / toxic / contextual negation / slang / Hinglish) → pytest + manual /docs calls
```

---

## PART 4 — Full code

### 4.1 Directory structure

```text
cyhi_project/
├── app/backend/                # unchanged production structure
├── ml/
│   └── toxicity/
│       ├── data/
│       ├── notebooks/
│       │   └── 01_bilstm.ipynb
│       ├── src/
│       │   ├── config.py
│       │   ├── data_utils.py
│       │   ├── vocab.py
│       │   ├── dataset.py
│       │   ├── model.py
│       │   ├── train.py
│       │   └── evaluate.py
│       ├── models/
│       │   ├── bilstm_toxicity.pt
│       │   ├── vocab.json
│       │   └── model_config.json
│       └── README.md
```

### 4.2 `requirements.txt` additions

```text
torch==2.3.1
pandas==2.2.2
numpy==1.26.4
scikit-learn==1.5.1
```

### 4.3 `ml/toxicity/src/config.py`

```python
LABELS = ["toxicity", "hate", "harassment", "abuse"]

RAW_DATA_PATH = "ml/toxicity/data/train.csv"
TRAIN_SPLIT, VAL_SPLIT, TEST_SPLIT = 0.70, 0.15, 0.15
RANDOM_SEED = 42

MAX_LEN = 100          # tokens per sequence — tune after checking length distribution
MIN_WORD_FREQ = 2       # words rarer than this in training data become <UNK>
EMBED_DIM = 128
HIDDEN_DIM = 128
NUM_LSTM_LAYERS = 1
DROPOUT = 0.3
BATCH_SIZE = 64
LEARNING_RATE = 1e-3
MAX_EPOCHS = 15
EARLY_STOPPING_PATIENCE = 3

MODEL_DIR = "ml/toxicity/models"
MODEL_PATH = f"{MODEL_DIR}/bilstm_toxicity.pt"
VOCAB_PATH = f"{MODEL_DIR}/vocab.json"
CONFIG_PATH = f"{MODEL_DIR}/model_config.json"
```

### 4.4 `ml/toxicity/src/data_utils.py`

```python
import re
import pandas as pd
from sklearn.model_selection import train_test_split
from config import LABELS, RAW_DATA_PATH, TRAIN_SPLIT, VAL_SPLIT, RANDOM_SEED

URL_RE = re.compile(r"https?://\S+|www\.\S+")
WHITESPACE_RE = re.compile(r"\s+")


def load_and_map_labels() -> pd.DataFrame:
    df = pd.read_csv(RAW_DATA_PATH)
    df["toxicity"] = ((df["toxic"] == 1) | (df["severe_toxic"] == 1)).astype(int)
    df["hate"] = df["identity_hate"].astype(int)
    df["harassment"] = ((df["insult"] == 1) | (df["threat"] == 1)).astype(int)
    df["abuse"] = ((df["obscene"] == 1) | (df["severe_toxic"] == 1)).astype(int)
    df = df[["comment_text"] + LABELS].dropna()
    df = df.drop_duplicates(subset="comment_text")
    return df


def clean_text(text: str) -> str:
    text = text.lower()
    text = URL_RE.sub(" <URL> ", text)
    text = WHITESPACE_RE.sub(" ", text).strip()
    # Deliberately NOT stripping punctuation entirely or removing short words like
    # "not" — negation and punctuation (e.g. repeated "!") carry toxicity signal.
    return text


def simple_tokenize(text: str) -> list:
    return text.split()


def split_dataset(df: pd.DataFrame):
    train_df, temp_df = train_test_split(df, train_size=TRAIN_SPLIT, random_state=RANDOM_SEED)
    rel_val = VAL_SPLIT / (1 - TRAIN_SPLIT)
    val_df, test_df = train_test_split(temp_df, train_size=rel_val, random_state=RANDOM_SEED)
    return train_df, val_df, test_df


def label_distribution(df: pd.DataFrame):
    return {label: float(df[label].mean()) for label in LABELS}
```

### 4.5 `ml/toxicity/src/vocab.py` (the part with no pretrained shortcut)

```python
import json
from collections import Counter
from config import MIN_WORD_FREQ

PAD_TOKEN, UNK_TOKEN = "<PAD>", "<UNK>"


def build_vocab(tokenized_texts: list, min_freq: int = MIN_WORD_FREQ) -> dict:
    """Build word->id from TRAINING data only. Never rebuild from val/test —
    that would leak information and also isn't how inference will see new text."""
    counter = Counter()
    for tokens in tokenized_texts:
        counter.update(tokens)

    vocab = {PAD_TOKEN: 0, UNK_TOKEN: 1}
    for word, freq in counter.items():
        if freq >= min_freq:
            vocab[word] = len(vocab)
    return vocab


def encode(tokens: list, vocab: dict, max_len: int) -> list:
    ids = [vocab.get(tok, vocab[UNK_TOKEN]) for tok in tokens]
    ids = ids[:max_len]
    ids += [vocab[PAD_TOKEN]] * (max_len - len(ids))
    return ids


def save_vocab(vocab: dict, path: str):
    with open(path, "w") as f:
        json.dump(vocab, f)


def load_vocab(path: str) -> dict:
    with open(path) as f:
        return json.load(f)
```

### 4.6 `ml/toxicity/src/dataset.py`

```python
import torch
from torch.utils.data import Dataset
from data_utils import clean_text, simple_tokenize
from vocab import encode
from config import LABELS, MAX_LEN


class ToxicityDataset(Dataset):
    def __init__(self, df, vocab):
        self.texts = df["comment_text"].apply(clean_text).apply(simple_tokenize).tolist()
        self.labels = df[LABELS].values.astype("float32")
        self.vocab = vocab

    def __len__(self):
        return len(self.labels)

    def __getitem__(self, idx):
        ids = encode(self.texts[idx], self.vocab, MAX_LEN)
        return torch.tensor(ids, dtype=torch.long), torch.tensor(self.labels[idx])
```

### 4.7 `ml/toxicity/src/model.py`

```python
import torch
import torch.nn as nn


class BiLSTMToxicityClassifier(nn.Module):
    def __init__(self, vocab_size, embed_dim, hidden_dim, num_labels,
                 num_layers=1, dropout=0.3, pad_idx=0):
        super().__init__()
        self.embedding = nn.Embedding(vocab_size, embed_dim, padding_idx=pad_idx)
        self.lstm = nn.LSTM(
            embed_dim, hidden_dim, num_layers=num_layers,
            batch_first=True, bidirectional=True,
            dropout=dropout if num_layers > 1 else 0.0,
        )
        self.dropout = nn.Dropout(dropout)
        # *2 because bidirectional concatenates forward + backward hidden states
        self.fc = nn.Linear(hidden_dim * 2, num_labels)

    def forward(self, x):
        embedded = self.embedding(x)                    # (batch, seq_len, embed_dim)
        lstm_out, (h_n, _) = self.lstm(embedded)         # h_n: (num_layers*2, batch, hidden_dim)
        # Concatenate final forward and backward hidden states
        forward_hidden = h_n[-2, :, :]
        backward_hidden = h_n[-1, :, :]
        combined = torch.cat((forward_hidden, backward_hidden), dim=1)
        combined = self.dropout(combined)
        logits = self.fc(combined)                       # (batch, num_labels) — raw logits
        return logits                                     # apply sigmoid outside, at inference time
```

### 4.8 `ml/toxicity/src/train.py`

```python
import json
import torch
from torch.utils.data import DataLoader
from sklearn.metrics import f1_score

from config import (
    LABELS, MAX_LEN, MIN_WORD_FREQ, EMBED_DIM, HIDDEN_DIM, NUM_LSTM_LAYERS,
    DROPOUT, BATCH_SIZE, LEARNING_RATE, MAX_EPOCHS, EARLY_STOPPING_PATIENCE,
    MODEL_PATH, VOCAB_PATH, CONFIG_PATH,
)
from data_utils import load_and_map_labels, clean_text, simple_tokenize, split_dataset
from vocab import build_vocab, save_vocab
from dataset import ToxicityDataset
from model import BiLSTMToxicityClassifier

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


def compute_pos_weight(train_df):
    """One weight per label = (negatives / positives). Upweights rare positive
    labels (e.g. 'hate') in the loss so the model doesn't just learn to predict 0."""
    weights = []
    for label in LABELS:
        pos = train_df[label].sum()
        neg = len(train_df) - pos
        weights.append(neg / max(pos, 1))
    return torch.tensor(weights, dtype=torch.float32)


def main():
    df = load_and_map_labels()
    train_df, val_df, test_df = split_dataset(df)

    train_tokens = train_df["comment_text"].apply(clean_text).apply(simple_tokenize).tolist()
    vocab = build_vocab(train_tokens, MIN_WORD_FREQ)
    save_vocab(vocab, VOCAB_PATH)

    train_ds = ToxicityDataset(train_df, vocab)
    val_ds = ToxicityDataset(val_df, vocab)
    train_loader = DataLoader(train_ds, batch_size=BATCH_SIZE, shuffle=True)
    val_loader = DataLoader(val_ds, batch_size=BATCH_SIZE)

    model = BiLSTMToxicityClassifier(
        vocab_size=len(vocab), embed_dim=EMBED_DIM, hidden_dim=HIDDEN_DIM,
        num_labels=len(LABELS), num_layers=NUM_LSTM_LAYERS, dropout=DROPOUT,
    ).to(device)

    pos_weight = compute_pos_weight(train_df).to(device)
    criterion = torch.nn.BCEWithLogitsLoss(pos_weight=pos_weight)
    optimizer = torch.optim.Adam(model.parameters(), lr=LEARNING_RATE)

    best_val_loss = float("inf")
    patience_counter = 0

    for epoch in range(MAX_EPOCHS):
        model.train()
        train_loss = 0.0
        for x, y in train_loader:
            x, y = x.to(device), y.to(device)
            optimizer.zero_grad()
            logits = model(x)
            loss = criterion(logits, y)
            loss.backward()
            optimizer.step()
            train_loss += loss.item() * x.size(0)
        train_loss /= len(train_ds)

        model.eval()
        val_loss = 0.0
        all_preds, all_true = [], []
        with torch.no_grad():
            for x, y in val_loader:
                x, y = x.to(device), y.to(device)
                logits = model(x)
                loss = criterion(logits, y)
                val_loss += loss.item() * x.size(0)
                preds = (torch.sigmoid(logits) >= 0.5).float()
                all_preds.append(preds.cpu())
                all_true.append(y.cpu())
        val_loss /= len(val_ds)

        val_f1 = f1_score(
            torch.cat(all_true).numpy(), torch.cat(all_preds).numpy(),
            average="macro", zero_division=0,
        )
        print(f"Epoch {epoch+1}: train_loss={train_loss:.4f} val_loss={val_loss:.4f} val_macro_f1={val_f1:.4f}")

        if val_loss < best_val_loss:
            best_val_loss = val_loss
            patience_counter = 0
            torch.save(model.state_dict(), MODEL_PATH)
        else:
            patience_counter += 1
            if patience_counter >= EARLY_STOPPING_PATIENCE:
                print("Early stopping triggered.")
                break

    with open(CONFIG_PATH, "w") as f:
        json.dump({
            "vocab_size": len(vocab), "embed_dim": EMBED_DIM, "hidden_dim": HIDDEN_DIM,
            "num_labels": len(LABELS), "num_layers": NUM_LSTM_LAYERS,
            "dropout": DROPOUT, "max_len": MAX_LEN, "labels": LABELS,
        }, f)

    print("Best model saved to", MODEL_PATH)


if __name__ == "__main__":
    main()
```

### 4.9 `ml/toxicity/src/evaluate.py`

```python
import torch
from torch.utils.data import DataLoader
from sklearn.metrics import precision_recall_fscore_support, f1_score, confusion_matrix
from config import LABELS


def evaluate(model, dataset, device, threshold=0.5, batch_size=64):
    loader = DataLoader(dataset, batch_size=batch_size)
    model.eval()
    all_preds, all_true = [], []
    with torch.no_grad():
        for x, y in loader:
            x = x.to(device)
            probs = torch.sigmoid(model(x)).cpu()
            preds = (probs >= threshold).float()
            all_preds.append(preds)
            all_true.append(y)

    y_pred = torch.cat(all_preds).numpy()
    y_true = torch.cat(all_true).numpy()

    per_label = {}
    for i, label in enumerate(LABELS):
        p, r, f, _ = precision_recall_fscore_support(
            y_true[:, i], y_pred[:, i], average="binary", zero_division=0
        )
        cm = confusion_matrix(y_true[:, i], y_pred[:, i])
        # cm[1][0] = false negatives = toxic content incorrectly let through — the
        # number that matters most for a teen-safety product, check it explicitly
        per_label[label] = {"precision": p, "recall": r, "f1": f, "false_negatives": int(cm[1][0]) if cm.shape == (2, 2) else None}

    macro_f1 = f1_score(y_true, y_pred, average="macro", zero_division=0)
    return per_label, macro_f1
```

### 4.10 Production inference wrapper — `app/backend/models/toxicity/toxicity_model.py`

```python
import json
import torch
from ml.toxicity.src.vocab import load_vocab, encode
from ml.toxicity.src.data_utils import clean_text, simple_tokenize
from ml.toxicity.src.model import BiLSTMToxicityClassifier


class BiLSTMToxicityModel:
    def __init__(self, model_path: str, vocab_path: str, config_path: str):
        with open(config_path) as f:
            self.cfg = json.load(f)
        self.vocab = load_vocab(vocab_path)
        self.labels = self.cfg["labels"]
        self.max_len = self.cfg["max_len"]

        self.model = BiLSTMToxicityClassifier(
            vocab_size=self.cfg["vocab_size"], embed_dim=self.cfg["embed_dim"],
            hidden_dim=self.cfg["hidden_dim"], num_labels=self.cfg["num_labels"],
            num_layers=self.cfg["num_layers"], dropout=self.cfg["dropout"],
        )
        self.model.load_state_dict(torch.load(model_path, map_location="cpu"))
        self.model.eval()

    def predict_proba(self, text: str) -> dict:
        tokens = simple_tokenize(clean_text(text))
        ids = encode(tokens, self.vocab, self.max_len)
        x = torch.tensor([ids], dtype=torch.long)
        with torch.no_grad():
            probs = torch.sigmoid(self.model(x))[0].tolist()
        return dict(zip(self.labels, probs))
```

### 4.11 `app/backend/services/toxicity_service.py` (decision layer — where the extra fields come from)

```python
from app.backend.models.toxicity.toxicity_model import BiLSTMToxicityModel
from app.backend.config import TOXICITY_MODEL_PATH, TOXICITY_VOCAB_PATH, TOXICITY_CONFIG_PATH, TOXICITY_THRESHOLDS

_model = BiLSTMToxicityModel(TOXICITY_MODEL_PATH, TOXICITY_VOCAB_PATH, TOXICITY_CONFIG_PATH)

REASON_MAP = {
    "toxicity": "General toxic language",
    "hate": "Identity-based hate speech",
    "harassment": "Insulting or threatening language",
    "abuse": "Abusive or obscene language",
}


class ToxicityService:
    def __init__(self):
        self.model = _model
        self.thresholds = TOXICITY_THRESHOLDS

    def analyze(self, text: str) -> dict:
        scores = self.model.predict_proba(text)
        flags = {label: scores[label] >= self.thresholds[label] for label in scores}
        triggered = [label for label, flagged in flags.items() if flagged]

        max_score = max(scores.values())
        is_sensitive = len(triggered) > 0
        age_rating = "16+" if is_sensitive and max_score > 0.7 else ("13+" if is_sensitive else "All ages")
        why = "; ".join(REASON_MAP[label] for label in triggered) if triggered else "No concerns detected"

        return {
            "toxicity_score": max_score,
            "scores": scores,
            "flags": flags,
            "why_is_it_toxic": why,
            "is_sensitive": is_sensitive,
            "ageRating": age_rating,
            "action": "blur" if is_sensitive else "show",
        }


toxicity_service = ToxicityService()
```

Note what's happening here, because it's the exact thing your mentor is likely to probe: **the model produces four numbers (`scores`). Everything else — `why_is_it_toxic`, `ageRating`, `is_sensitive`, `action` — is deterministic Python logic you wrote, not model output.** Be able to say that sentence out loud.

### 4.12 `app/backend/schemas/content.py`

```python
from pydantic import BaseModel
from typing import Dict, List


class ContentAnalyzeRequest(BaseModel):
    text: str


class ContentAnalyzeResponse(BaseModel):
    toxicity_score: float
    scores: Dict[str, float]
    flags: Dict[str, bool]
    why_is_it_toxic: str
    is_sensitive: bool
    ageRating: str
    action: str
```

### 4.13 `app/backend/api/routes/content.py`

```python
from fastapi import APIRouter
from app.backend.schemas.content import ContentAnalyzeRequest, ContentAnalyzeResponse
from app.backend.services.toxicity_service import toxicity_service

router = APIRouter(prefix="/api/v1/content", tags=["content"])


@router.post("/analyze", response_model=ContentAnalyzeResponse)
def analyze_content(payload: ContentAnalyzeRequest):
    return toxicity_service.analyze(payload.text)
```

### 4.14 `app/backend/config.py` additions

```python
TOXICITY_MODEL_PATH = "ml/toxicity/models/bilstm_toxicity.pt"
TOXICITY_VOCAB_PATH = "ml/toxicity/models/vocab.json"
TOXICITY_CONFIG_PATH = "ml/toxicity/models/model_config.json"

# Set these from your validation-set PR curves, not guessed — 0.5 for every
# label is the lazy default and will hurt recall on rare labels like "hate"
TOXICITY_THRESHOLDS = {
    "toxicity": 0.45,
    "hate": 0.35,
    "harassment": 0.40,
    "abuse": 0.40,
}
```

---

## PART 5 — Testing, exactly per Phase 12 of your plan

Run these through the API (`/docs` or a `pytest` file), and actually read the outputs — don't just check for a 200 status:

```text
"I really enjoyed this movie."              → expect: is_sensitive=False, action="show"
"You are a disgusting idiot."               → expect: is_sensitive=True, harassment/abuse high
"I don't think you're an idiot."            → the hard case — a bag-of-words-adjacent model
                                                (and even BiLSTM with a small vocab) may
                                                still flag this due to "idiot" being present;
                                                report this honestly as a known limitation,
                                                don't hide a failure here
"xoxo ur so dum lol"                        → tests slang/OOV handling; expect degraded
                                                but not necessarily wrong behavior
Hindi/Hinglish example                       → will very likely underperform or misfire,
                                                per Part 0's OOV discussion — say so upfront
```

The "contextual negation" case is genuinely difficult for this architecture and dataset size — BiLSTM helps with word order versus TF-IDF, but it isn't a guarantee against surface-word false positives. If it fails, that is a legitimate, explainable limitation ("our vocab/data size doesn't give the model enough negation examples to learn this reliably") — a much stronger answer in front of a mentor than pretending it's not an issue.

---

## PART 6 — Immediate next action

1. Run Phases 1–3 in the notebook (load, inspect, split) and actually read the label distribution and text-length distribution before hardcoding `MAX_LEN=100` — adjust it to your data.
2. Build the vocab, check its size (`len(vocab)`), and spot-check that common toxic words are actually in it (not filtered out by `MIN_WORD_FREQ`).
3. Run `train.py` for a couple epochs first as a smoke test — confirm loss decreases before committing to the full `MAX_EPOCHS` run.
4. Only after `evaluate.py` gives you numbers you can explain (including the false-negative count) do you touch the FastAPI wiring in 4.10–4.13.
5. Save 10 minutes before your mentor arrives to run through Part 5's test sentences live and know, in advance, which ones will fail and why.
