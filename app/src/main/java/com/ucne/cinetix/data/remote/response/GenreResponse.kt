package com.ucne.cinetix.data.remote.response

import com.google.gson.annotations.SerializedName
import com.ucne.cinetix.data.remote.dto.GenreDto

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<GenreDto>
)