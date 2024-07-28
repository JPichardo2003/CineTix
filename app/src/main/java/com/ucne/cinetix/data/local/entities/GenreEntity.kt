package com.ucne.cinetix.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Genres")
data class GenreEntity (
    @PrimaryKey
    val id: Int?,
    val name: String
)