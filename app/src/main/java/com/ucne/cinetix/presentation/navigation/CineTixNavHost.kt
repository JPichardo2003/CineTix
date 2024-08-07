package com.ucne.cinetix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ucne.cinetix.presentation.auth.LoginScreen
import com.ucne.cinetix.presentation.auth.SignUpScreen
import com.ucne.cinetix.presentation.home.HomeScreen
import com.ucne.cinetix.presentation.moviedetails.FilmDetailsScreen
import com.ucne.cinetix.presentation.profile.ProfileScreen
import com.ucne.cinetix.presentation.search.SearchScreen
import com.ucne.cinetix.presentation.watchlist.WatchListScreen

@Composable
fun CineTixNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login,
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
                },
                goToWatchListScreen = {
                    navHostController.navigate(Screen.WatchList)
                },
                goToLoginScreen = {
                    navHostController.navigate(Screen.Login)
                }
            )
        }
        composable<Screen.Search> {
            SearchScreen(
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                },
                goToFilmDetails = { id, filmType ->
                    navHostController.navigate(Screen.MovieDetails(id, filmType))
                }
            )
        }
        composable<Screen.MovieDetails> {
            val args = it.toRoute<Screen.MovieDetails>()
            FilmDetailsScreen(
                filmId = args.filmId,
                selectedFilm = args.selectedFilmType,
                goToHomeScreen = { navHostController.navigate(Screen.Home) },
                goToWatchListScreen = { navHostController.navigate(Screen.WatchList) },
                refreshPage = { id, filmType ->
                    navHostController.navigate(Screen.MovieDetails(id, filmType))
                }
            )
        }
        composable<Screen.WatchList> {
            WatchListScreen(
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                },
                goToProfileScreen = {
                    navHostController.navigate(Screen.Profile)
                },
                goToFilmDetails = { id, filmType ->
                    navHostController.navigate(Screen.MovieDetails(id, filmType))
                }
            )
        }
        composable<Screen.Login> {
            LoginScreen(
                goToSignUpScreen = {
                    navHostController.navigate(Screen.Signup)
                },
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                }
            )
        }
        composable<Screen.Signup> {
            SignUpScreen (
                goToLoginScreen = {
                    navHostController.navigate(Screen.Login)
                },
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                }
            )
        }
    }
}