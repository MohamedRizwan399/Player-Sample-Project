package com.example.vplayed_test.postApiDataclass

data class BrowseData(
    val album_data: AlbumData,
    val languages: List<Language>
)