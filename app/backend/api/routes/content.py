"""Content analysis routes"""

from fastapi import APIRouter
from schemas.content import ContentRequest, ContentAnalysisResponse

router = APIRouter(prefix="/api/v1/content", tags=["content"])

@router.post("/analyze", response_model=ContentAnalysisResponse)
async def analyze_content(request: ContentRequest):
    """Placeholder for content analysis endpoint"""
    return ContentAnalysisResponse(
        post_id=request.post_id,
        status="Content analysis endpoint - to be implemented"
    )
