
package com.example.ivory.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ModerationDto(
    val text: String
)

data class ModerationResponseDto(
    @SerializedName("overall_toxicity")
    val overallToxicity: Float,

    @SerializedName("any_flagged")
    val anyFlagged: Boolean,

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

    @SerializedName("ageRating")
    val ageRating: String,

    @SerializedName("age_rating_level")
    val ageRatingLevel: String
)

