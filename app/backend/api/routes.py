"""
Main API routes for IVORY backend
"""

from fastapi import APIRouter

router = APIRouter()

@router.get("/")
async def root():
    """Root endpoint"""
    return {"message": "IVORY Backend API", "version": "0.1.0"}

@router.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "ok"}
