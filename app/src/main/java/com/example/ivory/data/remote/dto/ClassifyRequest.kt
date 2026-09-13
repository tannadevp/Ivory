package com.example.ivory.data.remote.dto

data class ClassifyRequest(
    val text: String,
    val top_n: Int = 5
)