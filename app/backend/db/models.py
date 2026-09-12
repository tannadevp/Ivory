"""
SQLAlchemy database models
- Session tracking (session_id, timestamps)
- Mood history (mood scores over time)
- Content metadata (for filtering)
- User interactions (for engagement metrics)
"""

from datetime import datetime
from sqlalchemy import Column, String, Float, DateTime, Integer
from db.database import Base

class SessionLog(Base):
    """Tracks user sessions with simple session_id (no auth)"""
    __tablename__ = "session_logs"
    
    id = Column(Integer, primary_key=True)
    session_id = Column(String, unique=True, index=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    last_activity = Column(DateTime, default=datetime.utcnow)
    # TODO: Add fields for session metadata

class MoodHistory(Base):
    """Tracks mood scores over time per session"""
    __tablename__ = "mood_history"
    
    id = Column(Integer, primary_key=True)
    session_id = Column(String, index=True)
    mood_score = Column(Float)  # 0.0 to 1.0
    toxicity_avg = Column(Float)  # Average toxicity of content consumed
    engagement_score = Column(Float)  # Engagement metric (time, interactions)
    timestamp = Column(DateTime, default=datetime.utcnow)
    # TODO: Add fields for detailed mood breakdown

class ContentMetadata(Base):
    """Metadata for feed content"""
    __tablename__ = "content_metadata"
    
    id = Column(Integer, primary_key=True)
    content_id = Column(String, unique=True, index=True)
    toxicity_score = Column(Float, nullable=True)
    nsfw_score = Column(Float, nullable=True)
    # TODO: Add fields for semantic triggers, content type, etc.

class UserInteraction(Base):
    """Track user interactions (for engagement metrics)"""
    __tablename__ = "user_interactions"
    
    id = Column(Integer, primary_key=True)
    session_id = Column(String, index=True)
    content_id = Column(String)
    interaction_type = Column(String)  # 'view', 'like', 'share', etc.
    time_spent = Column(Integer)  # seconds
    timestamp = Column(DateTime, default=datetime.utcnow)
    # TODO: Add fields for interaction details
