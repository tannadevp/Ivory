"""
IVORY Backend - AI-powered Social Media Wellbeing Layer
"""

from fastapi import FastAPI
from api.routes.content import router as content_router
from api.routes.mood import router as mood_router

app = FastAPI(title="IVORY API")

@app.get("/")
async def root():
    return {"message": "IVORY Backend is running"}

@app.get("/health")
async def health():
    return {"status": "ok"}

# Include routers
app.include_router(content_router)
app.include_router(mood_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
