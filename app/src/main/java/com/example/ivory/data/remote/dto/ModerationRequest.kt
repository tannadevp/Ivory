package com.example.ivory.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ModerationDto(
    val text: String,
    val top_n: Int = 5
)

data class ModerationResponseDto(

    @SerializedName("input_text")
    val inputText: String,

    @SerializedName("overall_toxicity")
    val overallToxicity: Double,

    @SerializedName("any_flagged")
    val anyFlagged: Boolean,

    val rating: RatingDto,

    val suggestion: SuggestionDto?
)

data class RatingDto(

    @SerializedName("ageRating")
    val ageRating: String,

    @SerializedName("is_sensitive")
    val isSensitive: Boolean,

    @SerializedName("toxicity_rating")
    val toxicityRating: String,

    val message: String,

    @SerializedName("blur_level")
    val blurLevel: Int,

    @SerializedName("warning_title")
    val warningTitle: String,

    @SerializedName("warning_reason")
    val warningReason: String,

    @SerializedName("age_rating_level")
    val ageRatingLevel: String
)

data class SuggestionDto(

    @SerializedName("matched_category")
    val matchedCategory: String,

    val similarity: Double,

    @SerializedName("suggested_rewrite")
    val suggestedRewrite: String,

    @SerializedName("guideline_note")
    val guidelineNote: String
)