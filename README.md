# Ivory

IVORY is an AI/ML-powered platform designed to identify, analyze, and flag harmful content in online communication. The system uses Natural Language Processing (NLP) and machine learning to assess text for toxicity and other potentially harmful patterns, while exposing the trained model through an API that can be integrated into the application.

---

## Overview

Online platforms receive enormous amounts of user-generated content every day. Manually identifying toxic, abusive, or harmful content is difficult, slow, and inconsistent.

**IVORY** aims to automate this process.

A user-provided piece of text is processed by the system and passed through an ML pipeline that:

1. Receives the text.
2. Preprocesses and tokenizes it.
3. Passes it through the trained NLP model.
4. Produces a toxicity/harmfulness prediction.
5. Returns the prediction and relevant confidence information to the application.

The project is designed with a modular architecture so that the ML model can be improved or replaced without rebuilding the entire application.

---

## Objectives

* Detect toxic and harmful text automatically.
* Apply NLP techniques to real-world user-generated content.
* Build and serve an ML model through a FAST API.
* Create a modular AI/ML backend that can be integrated with the frontend.
* Provide interpretable prediction information wherever possible.
* Develop an architecture that can be extended to additional forms of harmful content in the future.

---

## AI/ML Component

The core of IVORY is its Natural Language Processing pipeline.

### Technologies

* **Python**
* **PyTorch**
* **NLP**
* **Tokenization**
* **Neural Networks**
* **FastAPI**
* **Pydantic**
* **NumPy**

---

## Backend

The IVORY backend is implemented using **FastAPI**.

FastAPI provides REST endpoints through which the frontend can send text to the ML model.

### Example request

```json
{
  "text": "Example user-generated text"
}
```

### Example response

```json
{
  "toxicity_score": 0.12,
  "is_toxic": false
}
```

The exact response structure may change as additional prediction categories and analysis features are introduced.

---

## Dataset

The model requires a labelled text dataset containing examples of harmful and non-harmful content.

The dataset is processed before training to handle:

* Missing or invalid entries
* Duplicate samples
* Text normalization
* Labels
* Class imbalance
* Train/validation/test splitting

The project is designed so that the dataset can be replaced or expanded without changing the API architecture.

---

## Completed

* [x] GitHub repository setup
* [x] Initial project structure
* [x] ML backend setup
* [x] PyTorch model loading
* [x] Tokenization/inference pipeline
* [x] FastAPI backend
* [x] Basic prediction endpoint

---

## Future Architecture

IVORY can eventually evolve from a simple toxicity classifier into a broader content-safety system:

```text
                     ┌──────────────────┐
                     │   User Content   │
                     └────────┬─────────┘
                              │
                              ▼
                     ┌──────────────────┐
                     │  NLP Processing  │
                     └────────┬─────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌───────────┐   ┌───────────┐   ┌───────────┐
        │ Toxicity  │   │  Abuse /  │   │  Other    │
        │ Detection │   │ Harassment│   │ Categories│
        └─────┬─────┘   └─────┬─────┘   └─────┬─────┘
              │               │               │
              └───────────────┼───────────────┘
                              ▼
                     ┌──────────────────┐
                     │ Safety Decision  │
                     └────────┬─────────┘
                              │
                              ▼
                     ┌──────────────────┐
                     │ Application/API  │
                     └──────────────────┘
```

This modular design allows individual models to be improved independently.

---

## Responsible AI

IVORY is intended to assist with content moderation and safety—not to make irreversible decisions about users automatically.

Machine-learning predictions can contain:

* False positives
* False negatives
* Bias
* Contextual misunderstandings
* Language-specific errors

The system should therefore be evaluated carefully before being used in high-impact moderation decisions.

---

## Learning Goals

This project provides practical exposure to:

* Python
* Git & GitHub
* Machine Learning
* Deep Learning
* Natural Language Processing
* PyTorch
* Model training and evaluation
* REST APIs
* FastAPI
* Backend integration
* Collaborative software development

---
