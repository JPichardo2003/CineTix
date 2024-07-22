package com.ucne.cinetix.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WatchList")
data class WatchListEntity(
    @PrimaryKey
    val watchListId: Int,
    val mediaType: String? = "",
    val imagePath: String? = "",
    val title: String? = "",
    val releaseDate: String? = "TBA",
    val rating: Double? = 0.0,
    val addedOn: String,
)
