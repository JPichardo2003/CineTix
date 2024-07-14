package com.ucne.cinetix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ucne.cinetix.presentation.home.HomeScreen

@Composable
fun CineTixNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                goToProfileScreen = {
                    navHostController.navigate(Screen.Profile)
                }
            )
        }
        composable<Screen.Profile> {
            /*TODO*/
        }
    }
}