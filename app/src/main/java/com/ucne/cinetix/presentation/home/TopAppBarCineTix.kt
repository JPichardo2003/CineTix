package com.ucne.cinetix.presentation.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.cinetix.R
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.util.FilmType

@Composable
fun TopAppBarCineTix(
    homeViewModel: HomeViewModel = hiltViewModel(),
    goToProfileScreen: () -> Unit,
    goToSearchScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = goToProfileScreen
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_profile),
                    tint = AppOnPrimaryColor,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "profile picture"
                )
            }
        }

        FilmTypeSelection(homeViewModel)

        Box(
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = goToSearchScreen
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "search icon",
                    tint = AppOnPrimaryColor
                )
            }
        }

    }
}

@Composable
private fun FilmTypeSelection(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val filmTypes = listOf(FilmType.MOVIE, FilmType.SERIES)
        val selectedFilmType = uiState.selectedFilmType

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            filmTypes.forEachIndexed { index, filmType ->
                Text(
                    text = if (filmType == FilmType.MOVIE) "Movies" else "Series",
                    fontWeight = if (selectedFilmType == filmTypes[index]) FontWeight.Bold else FontWeight.Light,
                    fontSize = if (selectedFilmType == filmTypes[index]) 24.sp else 16.sp,
                    color = if (selectedFilmType == filmTypes[index])
                        AppOnPrimaryColor else Color.LightGray.copy(alpha = 0.78F),
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp, top = 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (uiState.selectedFilmType != filmTypes[index]) {
                                homeViewModel.onFilmTypeChanged(filmTypes[index])
                                homeViewModel.getFilmGenre()
                                homeViewModel.refreshAll(null)
                            }
                        }
                )
            }
        }

        val animOffset = animateDpAsState(
            targetValue = when (filmTypes.indexOf(selectedFilmType)) {
                0 -> (-35).dp
                else -> 30.dp
            },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            ), label = ""
        )

        Box(
            modifier = Modifier
                .width(46.dp)
                .height(2.dp)
                .offset(x = animOffset.value)
                .clip(RoundedCornerShape(4.dp))
                .background(AppOnPrimaryColor)
        )
    }
}