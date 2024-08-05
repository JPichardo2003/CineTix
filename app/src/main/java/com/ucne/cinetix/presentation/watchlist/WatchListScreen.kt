package com.ucne.cinetix.presentation.watchlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.cinetix.R
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.presentation.components.BackButton
import com.ucne.cinetix.presentation.search.SearchResultItem
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor
import com.ucne.cinetix.ui.theme.rememberGradientColors
import com.ucne.cinetix.util.Constants
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun WatchListScreen(
    viewModel: WatchListViewModel = hiltViewModel(),
    goToHomeScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToFilmDetails: (Int, Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val watchList by viewModel.watchList.collectAsStateWithLifecycle()
    viewModel.usuarios.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getUserIdByEmail()
    }

    // Obtener la lista de observación solo si userId está disponible
    LaunchedEffect(uiState.userId) {
        uiState.userId?.let { userId ->
            viewModel.getFilmsByUser(userId)
        }
    }

    LaunchedEffect(watchList) {
        val filmIds = watchList.map { it.filmId }
        viewModel.getFilmsById(filmIds)
    }

    WatchListBody(
        uiState = uiState,
        goToHomeScreen = goToHomeScreen,
        goToProfileScreen = goToProfileScreen,
        goToFilmDetails = goToFilmDetails,
        removeFromWatchList = viewModel::removeFromWatchList,
        getFilmsById = viewModel::getFilmsById,
        deleteWatchList = viewModel::deleteWatchList,
    )
}

@Composable
fun WatchListBody(
    uiState: WatchListUIState,
    removeFromWatchList: (Int, Int) -> Unit,
    getFilmsById: (List<Int>) -> Unit,
    deleteWatchList: (Int) -> Unit,
    goToHomeScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToFilmDetails: (Int, Int) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }
    val showNumberIndicator by remember { mutableStateOf(true) }
    var currentList by remember { mutableStateOf<List<WatchListEntity>>(emptyList()) }
    LaunchedEffect(uiState.watchList) {
        currentList = uiState.watchList?.firstOrNull() ?: emptyList()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(rememberGradientColors())
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            val focusManager = LocalFocusManager.current
            BackButton {
                focusManager.clearFocus()
                goToProfileScreen()
            }

            Text(
                text = "My WatchList",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = AppOnPrimaryColor
            )

            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    goToHomeScreen()
                }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_home),
                    tint = AppOnPrimaryColor,
                    contentDescription = "home icon"
                )
            }
        }

        fun countItems(items: Int): String {
            return when (items) {
                0 -> "There's nothing here!"
                else -> "Films: $items"
            }
        }

        AnimatedVisibility(visible = showNumberIndicator) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = countItems(currentList.size),
                    color = AppOnPrimaryColor
                )
                IconButton(
                    onClick = { openDialog = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clean),
                        tint = AppOnPrimaryColor,
                        contentDescription = "Clean button"
                    )
                }
            }
        }

        Divider()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(currentList, key = { it.watchListId ?: 0 }) { film ->
                val filmDetails = uiState.film.find { it.id == film.filmId }
                filmDetails?.let { details ->
                    SwipeToDismissItem(
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                        onDismiss = {
                            removeFromWatchList(film.filmId, uiState.userId ?: 0)//TODO
                            currentList = currentList.filter { it.watchListId != film.watchListId }
                        }
                    ) {
                        SearchResultItem(
                            title = details.title ?: details.titleSeries ?: "",
                            mediaType = details.mediaType,
                            posterImage = "${Constants.BASE_POSTER_IMAGE_URL}/${details.posterPath}",
                            genres = emptyList(),
                            rating = details.voteAverage,
                            releaseYear = details.releaseDate ?: details.releaseDateSeries ?: "TBA",
                            onClick = {
                                if (details.mediaType == "movie") {
                                    goToFilmDetails(details.id, 1)
                                } else {
                                    goToFilmDetails(details.id, 2)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (openDialog && currentList.isNotEmpty()) {
            AlertDialog(
                title = { Text(text = "Delete All", color = AppOnPrimaryColor) },
                text = { Text(text = "Would you like to clear your watch list?") },
                shape = RoundedCornerShape(8.dp),
                confirmButton = {
                    TextButton(onClick = {
                        deleteWatchList(uiState.userId ?: 0)
                        currentList = emptyList()
                        openDialog = false
                    }) {
                        Text(text = "YES", color = AppOnPrimaryColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text(text = "NO", color = AppOnPrimaryColor)
                    }
                },
                containerColor = ButtonColor,
                textContentColor = AppOnPrimaryColor,
                onDismissRequest = {
                    openDialog = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissItem(
    modifier: Modifier,
    onDismiss: () -> Unit,
    swippable: @Composable () -> Unit,
) {
    val dismissState = rememberDismissState(initialValue = DismissValue.Default,
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDismiss()
            }
            true
        })

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, ButtonColor.copy(alpha = 0.25F))
                            )
                        )
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFFF6F6F),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        },
        dismissContent = {
            swippable()
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}