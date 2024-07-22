package com.ucne.cinetix.data.remote.response

import com.squareup.moshi.Json
import com.ucne.cinetix.data.remote.dto.FilmDto

data class FilmResponse(
    @Json(name="page")
    val page: Int,
    @Json(name="results")
    val results: List<FilmDto>,
    @Json(name="total_pages")
    val totalPages: Int,
    @Json(name="total_results")
    val totalResults: Int
)