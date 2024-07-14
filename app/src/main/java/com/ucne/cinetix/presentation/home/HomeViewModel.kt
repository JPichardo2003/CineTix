package com.ucne.cinetix.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.data.remote.dto.GenreDto
import com.ucne.cinetix.data.repository.FilmRepository
import com.ucne.cinetix.data.repository.GenreRepository
import com.ucne.cinetix.util.FilmType
import com.ucne.cinetix.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val genreRepository: GenreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshAll()
        }
    }

    fun refreshAll(
        genreId: Int? = uiState.value.selectedGenre.id,
        filmType: FilmType = uiState.value.selectedFilmType
    ) {
        if (uiState.value.filmGenres.size == 1) {
            getFilmGenre(uiState.value.selectedFilmType)
        }
        if (genreId == null) {
            _uiState.update { it.copy(selectedGenre = GenreDto(null, "All")) }
        }

        getTrendingFilms(genreId, filmType)
        getPopularFilms(genreId, filmType)
        getTopRatedFilm(genreId, filmType)
        getNowPlayingFilms(genreId, filmType)
        getUpcomingFilms(genreId)
        getBackInTheDaysFilms(genreId, filmType)
        uiState.value.randomMovieId?.let { id -> getRecommendedFilms(id, genreId, filmType) }
    }

    fun filterBySetSelectedGenre(genreDto: GenreDto) {
        _uiState.update { it.copy(selectedGenre = genreDto) }
        refreshAll(genreDto.id)
    }

    fun getFilmGenre(filmType: FilmType = uiState.value.selectedFilmType) {
        viewModelScope.launch {
            val defaultGenre = GenreDto(null, "All")
            when (val results = genreRepository.getMoviesGenre(filmType)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            filmGenres = listOf(defaultGenre) + (results.data?.genres ?: emptyList()),
                        )
                    }
                }
                is Resource.Error -> {
                    Timber.e("Error loading Genres")
                }
                else -> {}
            }
        }
    }

    private fun getTrendingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(trendingMoviesState = if (genreId != null) {
                    filmRepository.getTrendingFilms(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getTrendingFilms(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getPopularFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(popularFilmsState = if (genreId != null) {
                    filmRepository.getPopularFilms(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getPopularFilms(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getTopRatedFilm(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(topRatedFilmState = if (genreId != null) {
                    filmRepository.getTopRatedFilm(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getTopRatedFilm(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getNowPlayingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(nowPlayingMoviesState = if (genreId != null) {
                    filmRepository.getNowPlayingFilms(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getNowPlayingFilms(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    fun getRecommendedFilms(movieId: Int, genreId: Int? = null, filmType: FilmType = uiState.value.selectedFilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(recommendedFilmsState = if (genreId != null) {
                    filmRepository.getRecommendedFilms(movieId, filmType).map { result ->
                        result.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getRecommendedFilms(movieId, filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getUpcomingFilms(genreId: Int?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(upcomingMoviesState = if (genreId != null) {
                    filmRepository.getUpcomingTvShows().map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getUpcomingTvShows().cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getBackInTheDaysFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(backInTheDaysMoviesState = if (genreId != null) {
                    filmRepository.getBackInTheDaysFilms(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getBackInTheDaysFilms(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    fun onGenreChanged(genre: GenreDto) {
        _uiState.update { it.copy(selectedGenre = genre) }
    }

    fun onFilmTypeChanged(filmType: FilmType) {
        _uiState.update { it.copy(selectedFilmType = filmType) }
    }
}

data class HomeUIState(
    val filmGenres: List<GenreDto> = listOf(GenreDto(null, "All")),
    var selectedGenre: GenreDto = GenreDto(null, "All"),
    var selectedFilmType: FilmType = FilmType.MOVIE,
    val trendingMoviesState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val popularFilmsState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val topRatedFilmState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val nowPlayingMoviesState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val upcomingMoviesState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val backInTheDaysMoviesState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val recommendedFilmsState: Flow<PagingData<FilmDto>> = emptyFlow(),
    val randomMovieId: Int? = null
)


