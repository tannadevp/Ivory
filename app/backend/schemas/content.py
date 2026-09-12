"""Pydantic schemas for IVORY content API"""

from pydantic import BaseModel


class ContentRequest(BaseModel):
    """Incoming social media post to be analyzed"""
    post_id: str
    text: str


class ContentAnalysisResponse(BaseModel):
    """Response from content analysis endpoint"""
    post_id: str
    status: str
