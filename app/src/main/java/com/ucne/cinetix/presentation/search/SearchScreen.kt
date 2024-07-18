package com.ucne.cinetix.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ucne.cinetix.R
import com.ucne.cinetix.presentation.components.BackButton
import com.ucne.cinetix.presentation.home.HomeViewModel
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.rememberGradientColors
import com.ucne.cinetix.util.Constants.BASE_POSTER_IMAGE_URL

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    goToHomeScreen: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val searchResult = searchUiState.multiSearch.collectAsLazyPagingItems()
    val gradientsColors = rememberGradientColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientsColors)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 16.dp)
                .fillMaxWidth(fraction = 0.60F)
        ) {
            BackButton {
                goToHomeScreen()
            }

            Text(
                text = "Search",
                modifier = Modifier.padding(start = 50.dp),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = AppOnPrimaryColor
            )
        }

        SearchBar(
            onSearch = searchViewModel::searchRemoteMovie,
            onSearchParamChanged = { searchViewModel.onSearchParamChanged(it) },
            searchParam = searchUiState.searchParam
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            when (searchResult.loadState.refresh) {
                is LoadState.Loading -> item {
                    if (searchUiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.NotLoading -> {
                    items(searchResult) { films ->
                        films?.let {
                            SearchResultItem(
                                title = films.title,
                                mediaType = films.mediaType,
                                posterImage = "$BASE_POSTER_IMAGE_URL/${films.posterPath}",
                                genres = uiState.filmGenres.filter { genre ->
                                    return@filter if (films.genreIds.isNullOrEmpty()) false
                                    else films.genreIds.contains(genre.id)
                                },
                                rating = (films.voteAverage ?: 0).toDouble(),
                                releaseYear = films.releaseDate,
                                onClick = {}
                            )
                        }

                    }
                    if (searchResult.itemCount == 0) {
                        item {
                            NoResultsFound()
                        }
                    }
                }

                else -> item {
                    NoResultsFound()
                }
            }
        }
    }
}

@Composable
private fun NoResultsFound() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.match_not_found),
            colorFilter = ColorFilter.tint(White),
            contentDescription = "Film not found"
        )
    }
}