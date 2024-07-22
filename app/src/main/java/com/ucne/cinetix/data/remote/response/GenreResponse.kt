package com.ucne.cinetix.data.remote.response

import com.squareup.moshi.Json
import com.ucne.cinetix.data.remote.dto.GenreDto

data class GenreResponse(
    @Json(name="genres")
    val genres: List<GenreDto>
)