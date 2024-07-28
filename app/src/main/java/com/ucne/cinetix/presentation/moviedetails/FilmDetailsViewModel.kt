package com.ucne.cinetix.presentation.moviedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.data.remote.dto.GenreDto
import com.ucne.cinetix.data.repository.FilmRepository
import com.ucne.cinetix.data.repository.GenreRepository
import com.ucne.cinetix.data.repository.WatchListRepository
import com.ucne.cinetix.util.FilmType
import com.ucne.cinetix.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailsViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val genreRepository: GenreRepository,
    private val watchListRepository: WatchListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilmDetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun getSimilarFilms(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            filmRepository.getSimilarFilms(filmId, filmType).also {
                uiState.value.similarFilms = it
            }.cachedIn(viewModelScope)
        }
    }

    // Movie Details
    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val movieDetails = filmRepository.getMovieDetails(movieId)
                _uiState.update { it.copy(movieDetails = movieDetails) }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching movie details")
            }
        }
    }

    // TV Show Details
    fun getTvShowDetails(tvShowId: Int) {
        viewModelScope.launch {
            try {
                val tvShowDetails = filmRepository.getTvShowDetails(tvShowId)
                _uiState.update { it.copy(tvShowDetails = tvShowDetails) }
            } catch (e: Exception) {
                Log.e("Error", "Error fetching tv show details")
            }
        }
    }

    // Film Genres
    fun getFilmGenre(filmType: FilmType = _uiState.value.selectedFilmType) {
        viewModelScope.launch {
            val defaultGenre = GenreDto(null, "All")
            when (val results = genreRepository.getMoviesGenre(filmType)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            filmGenres = listOf(defaultGenre) + (results.data?.genres ?: emptyList())
                        )
                    }
                }
                is Resource.Error -> {
                    Log.e("Error", "Error loading Genres")
                }
                else -> {}
            }
        }
    }

    // Watchlist
    fun addToWatchList(movie: WatchListEntity) {
        viewModelScope.launch {
            watchListRepository.addToWatchList(movie)
        }.invokeOnCompletion {
            exists(movie.watchListId)
        }
    }

    fun exists(mediaId: Int) {
        viewModelScope.launch {
            val exists = watchListRepository.exists(mediaId)
            _uiState.update { it.copy(addedToWatchList = exists) }
        }
    }

    fun removeFromWatchList(mediaId: Int) {
        viewModelScope.launch {
            watchListRepository.removeFromWatchList(mediaId)
        }.invokeOnCompletion {
            exists(mediaId)
        }
    }
}

data class FilmDetailsUIState(
    var similarFilms: Flow<PagingData<FilmDto>> = emptyFlow(),
    val movieDetails: FilmDto? = null,
    val tvShowDetails: FilmDto? = null,
    val filmGenres: List<GenreDto> = listOf(GenreDto(null, "All")),
    var selectedFilmType: FilmType = FilmType.MOVIE,
    val addedToWatchList: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)