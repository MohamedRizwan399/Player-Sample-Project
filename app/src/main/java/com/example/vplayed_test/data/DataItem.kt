package com.example.vplayed_test.data

data class DataItem(
    val id: Int,
    val img_url: String,
    val inhabitants: List<String>,
    val name: String,
    val notable_residents: List<String>,
    val type: String
)