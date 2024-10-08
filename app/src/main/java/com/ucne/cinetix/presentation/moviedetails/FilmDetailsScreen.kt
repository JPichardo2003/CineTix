package com.ucne.cinetix.presentation.moviedetails

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import com.ucne.cinetix.R
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.presentation.auth.AuthState
import com.ucne.cinetix.presentation.components.BackButton
import com.ucne.cinetix.presentation.components.ExpandableText
import com.ucne.cinetix.presentation.components.MovieGenreLabel
import com.ucne.cinetix.ui.theme.AppOnPrimaryColor
import com.ucne.cinetix.ui.theme.AppPrimaryColor
import com.ucne.cinetix.ui.theme.ButtonColor
import com.ucne.cinetix.ui.theme.RatingStarColor
import com.ucne.cinetix.ui.theme.rememberGradientColors
import com.ucne.cinetix.util.Constants.BASE_BACKDROP_IMAGE_URL
import com.ucne.cinetix.util.Constants.BASE_POSTER_IMAGE_URL
import com.ucne.cinetix.util.FilmType
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun FilmDetailsScreen(
    viewModel: FilmDetailsViewModel = hiltViewModel(),
    filmId: Int,
    selectedFilm: Int,
    goToHomeScreen: () -> Unit,
    goToWatchListScreen: () -> Unit,
    refreshPage: (Int, Int) -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState = viewModel.authState.observeAsState()
    viewModel.usuarios.collectAsStateWithLifecycle()
    var film by remember { mutableStateOf<FilmEntity?>(null) }

    LaunchedEffect(key1 = filmId, key2 = selectedFilm, key3 = uiState.film) {
        film = uiState.film
        val filmType = if (selectedFilm == 1) FilmType.MOVIE else FilmType.TV
        viewModel.getFilmById(filmId = filmId, filmType = filmType)
        viewModel.getSimilars(filmType, filmId)
    }

    FilmDetailsBody(
        uiState = uiState,
        authState = authState,
        film = film,
        selectedFilm = selectedFilm,
        goToHomeScreen = goToHomeScreen,
        goToWatchListScreen = goToWatchListScreen,
        refreshPage = refreshPage,
        onWatchListChanged = viewModel::onWatchListChanged,
    )

}

@Composable
fun FilmDetailsBody(
    uiState: FilmDetailsUIState,
    film: FilmEntity?,
    authState: State<AuthState?>,
    selectedFilm: Int,
    goToHomeScreen: () -> Unit,
    goToWatchListScreen: () -> Unit,
    refreshPage: (Int, Int) -> Unit,
    onWatchListChanged: (Int?, Int?) -> Unit,
) {
    val backgroundColors = rememberGradientColors()
    val similarFilms = uiState.similarFilms.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    backgroundColors
                )
            )
    ) {
        film?.let {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.33F)
            ) {
                val (
                    backdropImage,
                    backButton,
                    movieTitleBox,
                    moviePosterImage,
                    translucentBr
                ) = createRefs()

                CoilImage(
                    imageModel = "$BASE_BACKDROP_IMAGE_URL${film.backdropPath}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .fillMaxHeight()
                        .constrainAs(backdropImage) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    failure = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.backdrop_not_available),
                                contentDescription = "no image"
                            )
                        }
                    },
                    shimmerParams = ShimmerParams(
                        baseColor = AppPrimaryColor,
                        highlightColor = ButtonColor,
                        durationMillis = 500,
                        dropOff = 0.65F,
                        tilt = 20F
                    ),
                    contentScale = Crop,
                    contentDescription = "Header backdrop image",
                )

                BackButton(
                    modifier = Modifier
                        .constrainAs(backButton) {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start, margin = 10.dp)
                        }
                ) { goToHomeScreen() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0XFF180E36).copy(alpha = 0.5F),
                                    Color(0XFF180E36)
                                ),
                                startY = 0.1F
                            )
                        )
                        .constrainAs(translucentBr) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(backdropImage.bottom)
                        }
                )

                Column(
                    modifier = Modifier.constrainAs(movieTitleBox) {
                        start.linkTo(moviePosterImage.end, margin = 12.dp)
                        end.linkTo(parent.end, margin = 12.dp)
                        bottom.linkTo(moviePosterImage.bottom, margin = 10.dp)
                    },
                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = when (selectedFilm) {
                                1 -> "Movie"
                                2 -> "Series"
                                else -> ""
                            },
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(size = 4.dp))
                                .background(Color.DarkGray.copy(alpha = 0.65F))
                                .padding(2.dp),
                            color = AppOnPrimaryColor.copy(alpha = 0.78F),
                            fontSize = 12.sp,
                        )
                    }

                    Text(
                        text = film.title ?: film.titleSeries ?: "TBA",
                        modifier = Modifier
                            .padding(top = 2.dp, start = 4.dp, bottom = 4.dp)
                            .fillMaxWidth(0.5F),
                        maxLines = 2,
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        color = Color.White.copy(alpha = 0.78F)
                    )

                    Text(
                        text = film.releaseDate ?: film.releaseDateSeries ?: "TBA",
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        fontSize = 15.sp,
                        fontWeight = Light,
                        color = Color.White.copy(alpha = 0.56F)
                    )

                    RatingBar(
                        value = (film.voteAverage / 2).toFloat(),
                        modifier = Modifier.padding(start = 6.dp, bottom = 4.dp, top = 4.dp),
                        config = RatingBarConfig()
                            .style(RatingBarStyle.Normal)
                            .isIndicator(true)
                            .activeColor(RatingStarColor)
                            .hideInactiveStars(false)
                            .inactiveColor(Color.LightGray.copy(alpha = 0.3F))
                            .stepSize(StepSize.HALF)
                            .numStars(5)
                            .size(16.dp)
                            .padding(4.dp),
                        onValueChange = {},
                        onRatingChanged = {}
                    )

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 4.dp, bottom = 8.dp)
                            .fillMaxWidth(0.42F),
                    ) {
                        if(authState.value is AuthState.Authenticated){
                            val context = LocalContext.current
                            IconButton(onClick = {
                                onWatchListChanged(film.id, uiState.userId)
                                if (uiState.addedToWatchList) {
                                    Toast.makeText(
                                        context, "Removed from watchlist", LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        context, "Added to watchlist", LENGTH_SHORT
                                    ).show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (!uiState.addedToWatchList) R.drawable.ic_add_to_list
                                        else R.drawable.ic_added_to_list
                                    ),
                                    tint = AppOnPrimaryColor,
                                    contentDescription = "add to watch list icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(
                                onClick = goToWatchListScreen
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Shop,
                                    tint = AppOnPrimaryColor,
                                    contentDescription = "watchlist icon"
                                )
                            }
                        }
                    }
                }

                CoilImage(
                    imageModel = "$BASE_POSTER_IMAGE_URL/${film.posterPath}",
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .width(115.dp)
                        .height(172.5.dp)
                        .constrainAs(moviePosterImage) {
                            top.linkTo(backdropImage.bottom)
                            bottom.linkTo(backdropImage.bottom)
                            start.linkTo(parent.start)
                        }, failure = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.image_not_available),
                                contentDescription = "no image"
                            )
                        }
                    },
                    shimmerParams = ShimmerParams(
                        baseColor = AppPrimaryColor,
                        highlightColor = ButtonColor,
                        durationMillis = 500,
                        dropOff = 0.65F,
                        tilt = 20F
                    ),
                    previewPlaceholder = R.drawable.hashiras,
                    contentScale = Crop,
                    circularReveal = CircularReveal(duration = 1000),
                    contentDescription = "movie poster"
                )
            }

            LazyRow(
                modifier = Modifier
                    .padding(top = (96).dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            ) {
                val genreList = uiState.filmGenres.filter { genres ->
                    genres.id in (film.genreIds ?: emptyList()) }
                genreList.forEach { genre ->
                    item {
                        MovieGenreLabel(
                            background = ButtonColor,
                            textColor = AppOnPrimaryColor,
                            genre = genre.name
                        )
                    }
                }
            }

            ExpandableText(
                text = film.overview,
                modifier = Modifier
                    .padding(top = 3.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )

            LazyColumn(
                horizontalAlignment = Alignment.Start
            ) {

                item {
                    if (similarFilms.itemCount != 0) {
                        Text(
                            text = "Similar",
                            fontWeight = Bold,
                            fontSize = 18.sp,
                            color = AppOnPrimaryColor,
                            modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
                        )
                    }
                }

                item {
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(similarFilms) { thisMovie ->
                            CoilImage(
                                imageModel = "${BASE_POSTER_IMAGE_URL}/${thisMovie!!.posterPath}",
                                shimmerParams = ShimmerParams(
                                    baseColor = AppPrimaryColor,
                                    highlightColor = ButtonColor,
                                    durationMillis = 500,
                                    dropOff = 0.65F,
                                    tilt = 20F
                                ),
                                failure = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.image_not_available),
                                            contentDescription = "no image"
                                        )
                                    }
                                },
                                previewPlaceholder = R.drawable.hashiras,
                                contentScale = Crop,
                                circularReveal = CircularReveal(duration = 1000),
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .size(130.dp, 195.dp)
                                    .clickable {
                                        refreshPage(thisMovie.id, selectedFilm)
                                    },
                                contentDescription = "Movie item"
                            )
                        }
                    }
                }
            }
        }
    }
}