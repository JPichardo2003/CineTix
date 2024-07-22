package com.ucne.cinetix.data.remote.response

import com.squareup.moshi.Json
import com.ucne.cinetix.data.remote.dto.SearchDto

class MultiSearchResponse(
    @Json(name="page")
    val page: Int,
    @Json(name="results")
    val results: List<SearchDto>,
    @Json(name="total_pages")
    val totalPages: Int,
    @Json(name="total_results")
    val totalResults: Int
)