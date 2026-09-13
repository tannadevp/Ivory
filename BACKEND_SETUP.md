# IVORY Backend Environment Setup

## Backend Structure Ready

```
app/backend/
├── main.py                      # FastAPI entry point
├── config.py                    # Configuration & model paths
├── requirements.txt             # Python dependencies
├── db/
│   ├── database.py              # SQLAlchemy setup
│   └── models.py                # Database models
├── schemas/
│   ├── content.py               # Pydantic schemas for content
│   └── mood.py                  # Pydantic schemas for mood
├── models/                      # ML models (toxicity, NSFW, triggers, mood)
├── services/                    # Business logic services
├── api/                         # API route handlers
└── utils/
    ├── preprocessing.py         # Text/data preprocessing
    └── constants.py             # App constants & thresholds
```

## Database Models Ready

- `SessionLog` - Track sessions with session_id
- `MoodHistory` - Store mood scores + toxicity/engagement metrics over time
- `ContentMetadata` - Cache content classification scores
- `UserInteraction` - Track views, likes, time spent

## Next Steps for Development

1. Implement ML model loaders in `models/`
2. Implement classification services (toxicity, NSFW, triggers)
3. Implement mood calculation & ranking logic in `services/`
4. Create API endpoints in `api/`
5. Add database initialization & migrations
