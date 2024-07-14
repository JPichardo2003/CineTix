package com.ucne.cinetix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ucne.cinetix.presentation.navigation.CineTixNavHost
import com.ucne.cinetix.ui.theme.CineTixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            CineTixTheme {
                val navHostController = rememberNavController()
                CineTixNavHost(navHostController)
            }
        }
    }
}