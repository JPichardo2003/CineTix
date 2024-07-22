package com.ucne.cinetix.presentation.watchlist

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.ucne.cinetix.presentation.search.SearchBar
import com.ucne.cinetix.presentation.search.SearchResultItem
import com.ucne.cinetix.presentation.search.SearchViewModel
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor
import com.ucne.cinetix.ui.theme.rememberGradientColors
import com.ucne.cinetix.util.Constants
import kotlinx.coroutines.flow.first

@Composable
fun WatchListScreen(
    watchListViewModel: WatchListViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    goToHomeScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
) {
    val watchListState by watchListViewModel.uiState.collectAsStateWithLifecycle()
    val searchState by searchViewModel.uiState.collectAsStateWithLifecycle()
    var currentList by remember { mutableStateOf<List<WatchListEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        currentList = watchListState.watchList.first()
    }
    LaunchedEffect(searchState.searchParam) {
        currentList = if (searchState.searchParam.isEmpty()) {
            watchListState.watchList.first()
        } else {
            watchListState.watchList.first().filter { movie ->
                movie.title?.contains(other = searchState.searchParam, ignoreCase = true) ?: false
            }
        }
        Log.e("Filtered by", "${searchState.searchParam}: ${currentList.size}")
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
                text = "My Watch list",
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

        SearchBar(
            searchParam = searchState.searchParam,
            onSearch = {
                searchViewModel.searchRemoteMovie()
            },
            onSearchParamChanged = {
                searchViewModel.onSearchParamChanged(it)
            }
        )

        fun countItems(items: Int): String {
            return when (items) {
                1 -> "Found 1 item"
                0 -> "There's nothing here!"
                else -> "Found $items items"
            }
        }

        var openDialog by remember { mutableStateOf(false) }
        val showNumberIndicator by remember { mutableStateOf(true) }
        AnimatedVisibility(visible = showNumberIndicator) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .border(
                        width = 1.dp, color = ButtonColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(ButtonColor.copy(alpha = 0.25F))
            ) {
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
                            painter = painterResource(id = R.drawable.ic_cancel),
                            tint = AppOnPrimaryColor,
                            contentDescription = "Cancel button"
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(currentList, key = { it.watchListId }) { film ->
                SwipeToDismissItem(
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    onDismiss = {
                        watchListViewModel.removeFromWatchList(film.watchListId)
                        currentList = currentList.filter { it.watchListId != film.watchListId }
                    }) {
                    SearchResultItem(
                        title = film.title,
                        mediaType = film.mediaType,
                        posterImage = "${Constants.BASE_POSTER_IMAGE_URL}/${film.imagePath}",
                        genres = emptyList(),
                        rating = film.rating ?: 0.0,
                        releaseYear = film.releaseDate
                    ) { }
                }
            }
        }

        if (openDialog && currentList.size > 1) {
            AlertDialog(
                title = { Text(text = "Delete All", color = AppOnPrimaryColor) },
                text = { Text(text = "Would you like to clear your watch list?") },
                shape = RoundedCornerShape(8.dp),
                confirmButton = {
                    TextButton(onClick = {
                        watchListViewModel.deleteWatchList()
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
                })
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
