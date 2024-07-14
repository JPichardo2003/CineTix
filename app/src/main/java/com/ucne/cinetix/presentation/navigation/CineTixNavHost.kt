package com.ucne.cinetix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ucne.cinetix.presentation.home.HomeScreen
import com.ucne.cinetix.presentation.profile.ProfileScreen

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
            ProfileScreen(
                goToHomeScreen = {
                    navHostController.navigate(Screen.Home)
                }
            )
        }
    }
}