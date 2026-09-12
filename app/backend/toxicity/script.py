import pandas as pd
from pathlib import Path

# ---------- 1. Load HASOC 2019 Hindi files (tab-separated) ----------
BASE_DIR = Path(__file__).resolve().parent
hasoc_train = pd.read_csv(BASE_DIR / "hindi_dataset.tsv",sep="\t")
hasoc_test = pd.read_csv(BASE_DIR / "hasoc2019_hi_test_gold_2919.tsv",sep="\t")

hasoc_train = hasoc_train[["text", "task_1"]].rename(columns={"text": "text"})
hasoc_test  = hasoc_test[["text", "task_1"]].rename(columns={"text": "text"})

hasoc = pd.concat([hasoc_train, hasoc_test], ignore_index=True)
hasoc["label"] = hasoc["task_1"].map({"HOF": 1, "NOT": 0})
hasoc = hasoc[["text", "label"]]

# ---------- 2. Load Jigsaw train.csv ----------
jigsaw = pd.read_csv(BASE_DIR / "train.csv")
jigsaw = jigsaw.rename(columns={"comment_text": "text"})

toxic_cols = ["toxic", "severe_toxic", "obscene", "threat", "insult", "identity_hate"]
jigsaw["label"] = (jigsaw[toxic_cols].sum(axis=1) > 0).astype(int)

jigsaw = jigsaw[["text", "label"]]

# ---------- 3. Merge everything ----------
merged = pd.concat([hasoc, jigsaw], ignore_index=True)

# Basic cleanup: drop empty/NaN text, drop exact duplicates
merged["text"] = merged["text"].astype(str).str.strip()
merged = merged[merged["text"].str.len() > 0]
merged = merged.drop_duplicates(subset="text").reset_index(drop=True)

# Shuffle (important before train/test split, since the data is currently
# stacked language-by-language / source-by-source)
merged = merged.sample(frac=1, random_state=42).reset_index(drop=True)

print(merged["label"].value_counts())
print(merged.shape)

merged.to_csv("merged_toxicity_dataset.csv", index=False)