package com.ucne.cinetix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ucne.cinetix.presentation.home.HomeScreen
import com.ucne.cinetix.presentation.moviedetails.FilmDetailsScreen
import com.ucne.cinetix.presentation.profile.ProfileScreen
import com.ucne.cinetix.presentation.search.SearchScreen

@Composable
fun CineTixNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home,
    ) {
        composable<Screen.Home> {
            HomeScreen(
                goToProfileScreen = {
                    navHostController.navigate(Screen.Profile)
                },
                goToSearchScreen = {
                    navHostController.navigate(Screen.Search)
                },
                goToFilmDetails = { id, filmType ->
                    navHostController.navigate(Screen.MovieDetails(id, filmType))
                }
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                }
            )
        }
        composable<Screen.Search> {
            SearchScreen(
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                }
            )
        }
        composable<Screen.MovieDetails> {
            val args = it.toRoute<Screen.MovieDetails>()
            FilmDetailsScreen(
                filmId = args.filmId,
                selectedFilm = args.selectedFilmType,
                goToHomeScreen = { navHostController.navigate(Screen.Home) }
            )
        }
    }
}