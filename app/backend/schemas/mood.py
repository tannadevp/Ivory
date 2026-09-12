"""Pydantic schemas for Mood tracking"""

from pydantic import BaseModel
from datetime import datetime
from typing import Optional

class MoodSnapshot(BaseModel):
    """Current mood state"""
    session_id: str
    mood_score: float  # 0.0 to 1.0
    toxicity_avg: float
    engagement_score: float
    timestamp: datetime
    is_low_mood: bool  # True if below threshold

class MoodHistoryResponse(BaseModel):
    """Mood history for a session"""
    session_id: str
    mood_snapshots: list[MoodSnapshot]
    current_mood_state: str  # 'good', 'low', 'recovering'
