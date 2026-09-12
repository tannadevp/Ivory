"""
Constants and enumerations
"""

# Interaction types
INTERACTION_TYPES = {
    "view": 1,
    "like": 2,
    "share": 3,
    "comment": 4,
}

# Mood states
MOOD_STATES = {
    "good": (0.7, 1.0),
    "neutral": (0.4, 0.7),
    "low": (0.0, 0.4),
}

# Model confidence thresholds
TOXICITY_THRESHOLD = 0.5
NSFW_THRESHOLD = 0.5
