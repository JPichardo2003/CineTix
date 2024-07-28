package com.ucne.cinetix.data.remote.dto

import com.squareup.moshi.Json
import com.ucne.cinetix.data.local.entities.GenreEntity

data class GenreDto(
    @Json(name="id")
    val id: Int?,
    @Json(name="name")
    val name: String
)

fun GenreDto.toEntity(): GenreEntity {
    return GenreEntity(
        id = this.id,
        name = this.name
    )
}

data class GenreResponse(
    @Json(name="genres")
    val genres: List<GenreDto>
)