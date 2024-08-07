package com.ucne.cinetix.data.remote.dto

import com.ucne.cinetix.data.local.entities.WatchListEntity

data class WatchListDto(
    val watchListId: Int? = null,
    val userId: Int,
    val filmId: Int,
    val addedOn: String,
)

fun WatchListDto.toEntity(): WatchListEntity {
    return WatchListEntity(
        watchListId = this.watchListId,
        userId = this.userId,
        filmId = this.filmId,
        addedOn = this.addedOn
    )
}
