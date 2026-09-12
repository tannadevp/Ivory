"""Mood and wellbeing routes"""

from fastapi import APIRouter

router = APIRouter(prefix="/api/v1/mood", tags=["mood"])

@router.get("/current")
async def get_current_mood():
    """Placeholder for mood retrieval endpoint"""
    return {"message": "Mood endpoint - to be implemented"}
