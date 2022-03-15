package com.example.vplayed_test.postApiDataclass

data class Movies(
    val error: Boolean,
    val message: String,
    val response: Response,
    val status: String,
    val statusCode: Int
)