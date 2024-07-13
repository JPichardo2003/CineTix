package com.ucne.cinetix.data.remote.response

import com.google.gson.annotations.SerializedName
import com.ucne.cinetix.data.remote.dto.FilmDto

data class FilmResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<FilmDto>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)