package com.ucne.cinetix.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Search : Screen()

    @Serializable
    data object WatchList : Screen()

    @Serializable
    data class MovieDetails(val filmId: Int, val selectedFilmType: Int) : Screen()
}