package com.ucne.cinetix.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GenreDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String
)