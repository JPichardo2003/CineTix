package com.ucne.cinetix.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import com.ucne.cinetix.R
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.presentation.components.LoopReverseLottieLoader
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.AppPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor
import com.ucne.cinetix.ui.theme.ErrorMessage
import com.ucne.cinetix.ui.theme.rememberGradientColors
import com.ucne.cinetix.util.Constants.BASE_BACKDROP_IMAGE_URL
import com.ucne.cinetix.util.Constants.BASE_POSTER_IMAGE_URL
import com.ucne.cinetix.util.FilmType
import retrofit2.HttpException
import java.io.IOException

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    goToProfileScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    goToFilmDetails: (Int, Int) -> Unit
) {
    val gradientColors = rememberGradientColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors),
            )
    ) {
        TopAppBarCineTix(
            goToProfileScreen = goToProfileScreen,
            goToSearchScreen = goToSearchScreen
        )
        HomeBody(
            homeViewModel = homeViewModel,
            goToFilmDetails = goToFilmDetails
        )
    }
}


@Composable
fun HomeBody(
    homeViewModel: HomeViewModel = hiltViewModel(),
    goToFilmDetails: (Int, Int) -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val trendingFilms = uiState.trendingMoviesState.collectAsLazyPagingItems()
    val popularFilms = uiState.popularFilmsState.collectAsLazyPagingItems()
    val topRatedFilms = uiState.topRatedFilmState.collectAsLazyPagingItems()
    val nowPlayingFilms = uiState.nowPlayingMoviesState.collectAsLazyPagingItems()
    val upcomingMovies = uiState.upcomingMoviesState.collectAsLazyPagingItems()
    val backInTheDays = uiState.backInTheDaysMoviesState.collectAsLazyPagingItems()

    val listState: LazyListState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .fillMaxSize()
    ) {
        item {
            Header("Genres")
            GenreList(homeViewModel)
        }

        item {
            Header("Trending")
            ScrollableMovieItems(
                landscape = true,
                pagingItems = trendingFilms,
                onErrorClick = { homeViewModel.refreshAll() },
                goToFilmDetails = goToFilmDetails,
                selectedFilmType = uiState.selectedFilmType
            )
        }

        item {
            Header("Popular")
            ScrollableMovieItems(
                pagingItems = popularFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                },
                goToFilmDetails = goToFilmDetails,
                selectedFilmType = uiState.selectedFilmType

            )
        }

        item {
            Header("Top Rated")
            ScrollableMovieItems(
                pagingItems = topRatedFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                },
                goToFilmDetails = goToFilmDetails,
                selectedFilmType = uiState.selectedFilmType
            )
        }

        item {
            Header("Now Playing")
            ScrollableMovieItems(
                pagingItems = nowPlayingFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                },
                goToFilmDetails = goToFilmDetails,
                selectedFilmType = uiState.selectedFilmType
            )
        }

        if (uiState.selectedFilmType == FilmType.MOVIE) {
            item {
                Header("Upcoming")
                ScrollableMovieItems(
                    pagingItems = upcomingMovies,
                    onErrorClick = {
                        homeViewModel.refreshAll()
                    },
                    goToFilmDetails = goToFilmDetails,
                    selectedFilmType = uiState.selectedFilmType
                )
            }
        }

        if (backInTheDays.itemCount != 0) {
            item {
                ShowAboutCategory(
                    name = "Back in the Days",
                    description = "Films released between 1940 and 1980"
                )
            }
            item {
                ScrollableMovieItems(
                    pagingItems = backInTheDays,
                    onErrorClick = {
                        homeViewModel.refreshAll()
                    },
                    goToFilmDetails = goToFilmDetails,
                    selectedFilmType = uiState.selectedFilmType
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GenreList(homeViewModel: HomeViewModel= hiltViewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val genres = uiState.filmGenres
    LazyRow(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        items(genres.size) {
            SelectableGenre(
                genre = genres[it].name,
                selected = genres[it].name == uiState.selectedGenre.name,
                onclick = {
                    if (uiState.selectedGenre.name != genres[it].name) {
                        homeViewModel.onGenreChanged(genres[it])
                        homeViewModel.filterBySetSelectedGenre(genres[it])
                    }
                }
            )
        }
    }
}

@Composable
private fun Header(text: String) {
    Text(
        text = text,
        fontSize = 24.sp,
        color = AppOnPrimaryColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(6.dp)
    )
}

@Composable
fun MovieItem(
    imageUrl: String,
    title: String,
    modifier: Modifier,
    landscape: Boolean,
    goToFilmDetails: () -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(all = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                goToFilmDetails()
            },
        horizontalAlignment = Alignment.Start
    ) {
        CoilImage(
            imageModel = imageUrl,
            shimmerParams = ShimmerParams(
                baseColor = AppPrimaryColor,
                highlightColor = ButtonColor,
                durationMillis = 100,
                dropOff = 0.65F,
                tilt = 20F
            ),
            failure = {
                Box(
                    contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_not_available),
                        contentDescription = "no image"
                    )
                }
            },
            previewPlaceholder = R.drawable.hashiras,
            contentScale = Crop,
            circularReveal = CircularReveal(duration = 500),
            modifier = modifier.clip(RoundedCornerShape(8.dp)),
            contentDescription = "Movie item"
        )

        AnimatedVisibility(visible = landscape) {
            Text(
                text = if (title.length > 26) "${title.take(26)}..." else title,
                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp)
                    .fillMaxWidth(),
                maxLines = 1,
                color = AppOnPrimaryColor,
                overflow = TextOverflow.Ellipsis,
                fontWeight = Normal,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun ScrollableMovieItems(
    landscape: Boolean = false,
    pagingItems: LazyPagingItems<FilmEntity>,
    onErrorClick: () -> Unit,
    goToFilmDetails: (Int, Int) -> Unit,
    selectedFilmType: FilmType
) {
    Box(
        contentAlignment = Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (landscape) 195.dp else 215.dp)
    ) {
        when (pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                LoopReverseLottieLoader(lottieFile = R.raw.loader)
            }
            is LoadState.NotLoading -> {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(pagingItems) { film ->
                        film?.let{
                            val imageUrl =
                                if (landscape) "$BASE_BACKDROP_IMAGE_URL/${film.backdropPath}"
                                else "$BASE_POSTER_IMAGE_URL/${film.posterPath}"

                            MovieItem(
                                landscape = landscape,
                                imageUrl = imageUrl,
                                title = film.title ?: film.titleSeries ?: "No title",
                                modifier = Modifier
                                    .width(if (landscape) 215.dp else 130.dp)
                                    .height(if (landscape) 161.25.dp else 195.dp)
                            ) {
                                if (selectedFilmType == FilmType.MOVIE) {
                                    goToFilmDetails(film.id, 1)
                                } else {
                                    goToFilmDetails(film.id, 2)
                                }

                            }
                        }
                    }
                }
            }
            is LoadState.Error -> {
                val error = pagingItems.loadState.refresh as LoadState.Error
                val errorMessage = when (error.error) {
                    is HttpException -> "Sorry, Something went wrong!\nTap to retry"
                    is IOException -> "Connection failed. Tap to retry!"
                    else -> "Failed! Tap to retry!"
                }
                Box(contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(161.25.dp) // mantiene el espacio vertical entre categorias
                        .clickable { onErrorClick() }
                ) {
                    Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = Light,
                        color = ErrorMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableGenre(
    genre: String,
    selected: Boolean,
    onclick: () -> Unit
) {

    val animateChipBackgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFA0A1C2) else ButtonColor.copy(alpha = 0.5F),
        animationSpec = tween(
            durationMillis = if (selected) 100 else 50,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .clip(CircleShape)
            .background(
                color = animateChipBackgroundColor
            )
            .height(32.dp)
            .widthIn(min = 80.dp)
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onclick()
            }
    ) {
        Text(
            text = genre,
            fontWeight = if (selected) Normal else Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Center),
            color = if (selected) AppPrimaryColor else Color.White.copy(alpha = 0.80F)
        )
    }
}

@Composable
fun ShowAboutCategory(name: String, description: String) {
    var showAboutThisCategory by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            fontSize = 24.sp,
            color = AppOnPrimaryColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 4.dp, top = 14.dp,
                end = 8.dp, bottom = 4.dp
            )
        )
        IconButton(
            modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
            onClick = { showAboutThisCategory = showAboutThisCategory.not() }) {
            Icon(
                imageVector = if (showAboutThisCategory) Icons.Filled.KeyboardArrowUp else Icons.Filled.Info,
                tint = AppOnPrimaryColor,
                contentDescription = "Info Icon"
            )
        }
    }

    AnimatedVisibility(visible = showAboutThisCategory) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp, color = ButtonColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(ButtonColor.copy(alpha = 0.25F))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = description,
                    color = AppOnPrimaryColor
                )
            }
        }
    }
}