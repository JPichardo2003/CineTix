package com.ucne.cinetix.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

//CineTix Exclusive Colors
val AppPrimaryColor = Color(0xFF180E36)
val AppOnPrimaryColor = Color.White.copy(alpha = 0.78F)
val ButtonColor = Color(0XFF423460)
val SeeMore = Color(0xFF7E7E7E)
val ErrorMessage = Color(0xFFE28B8B)
val RatingStarColor = Color(0XFFC9F964)

//HBOMAX InspirationColors
val Black = Color(0xFF000000)
val MidnightPurple = Color(0xFF28003c)
val RipePlum = Color(0xFF3c0050)
val DeepViolet = Color(0xFF3c0064)
val CrowBerry = Color(0xFF280050)

@Composable
fun rememberGradientColors(): List<Color> {
    return remember {
        listOf(
            Color(0xFF000000),
            Color(0xFF28003c),
            Color(0xFF3c0050),
            Color(0xFF3c0064),
            Color(0xFF3c0064),
            Color(0xFF280050),
            Color(0xFF000000)
        )
    }
}