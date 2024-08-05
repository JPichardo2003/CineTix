package com.ucne.cinetix.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WatchList")
data class WatchListEntity(
    @PrimaryKey
    val watchListId: Int? = null,
    val userId: Int,
    val filmId: Int,
    val addedOn: String,
)
