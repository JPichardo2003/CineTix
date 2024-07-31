package com.ucne.cinetix.presentation.moviedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.data.remote.dto.GenreDto
import com.ucne.cinetix.data.remote.dto.toEntity
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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


    fun getFilmById(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            val film = filmRepository.getFilmById(filmId, filmType)
            _uiState.update { it.copy(film = film) }
        }
    }

    fun getFilmGenre(filmType: FilmType = uiState.value.selectedFilmType) {
        viewModelScope.launch {
            val defaultGenre = GenreEntity(null, "All")
            genreRepository.getGenresFromDB(filmType).onEach { genresFromDB ->
                _uiState.update {
                    it.copy(
                        filmGenres = listOf(defaultGenre) + genresFromDB
                    )
                }
            }.launchIn(viewModelScope)
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
    var similarFilms: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val film: FilmEntity? = null,
    val movieDetails: FilmDto? = null,
    val tvShowDetails: FilmDto? = null,
    val filmGenres: List<GenreEntity> = listOf(GenreEntity(null, "All")),
    var selectedFilmType: FilmType = FilmType.MOVIE,
    val addedToWatchList: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)