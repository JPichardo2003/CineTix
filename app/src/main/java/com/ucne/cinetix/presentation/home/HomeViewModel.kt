package com.ucne.cinetix.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val genreRepository: GenreRepository
) : ViewModel() {
    private var _filmGenres = mutableStateListOf(GenreDto(null, "All"))
    val filmGenres: SnapshotStateList<GenreDto> = _filmGenres

    var selectedGenre by mutableStateOf(GenreDto(null, "All"))
    var selectedFilmType by mutableStateOf(FilmType.MOVIE)

    private var _trendingMovies = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    val trendingMoviesState: State<Flow<PagingData<FilmDto>>> = _trendingMovies

    private var _popularFilms = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    val popularFilmsState: State<Flow<PagingData<FilmDto>>> = _popularFilms

    private var _topRatedFilm = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    val topRatedFilmState: State<Flow<PagingData<FilmDto>>> = _topRatedFilm

    private var _nowPlayingFilm = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    val nowPlayingMoviesState: State<Flow<PagingData<FilmDto>>> = _nowPlayingFilm

    private var _upcomingFilms = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    val upcomingMoviesState: State<Flow<PagingData<FilmDto>>> = _upcomingFilms

    private var _backInTheDaysFilms = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    val backInTheDaysMoviesState: State<Flow<PagingData<FilmDto>>> = _backInTheDaysFilms

    private var _recommendedFilms = mutableStateOf<Flow<PagingData<FilmDto>>>(emptyFlow())
    var randomMovieId: Int? = null

    init {
        refreshAll()
    }

    fun refreshAll(
        genreId: Int? = selectedGenre.id,
        filmType: FilmType = selectedFilmType
    ) {
        if (filmGenres.size == 1) {
            getFilmGenre(selectedFilmType)
        }
        if (genreId == null) {
            selectedGenre = GenreDto(null, "All")
        }
        getTrendingFilms(genreId, filmType)
        getPopularFilms(genreId, filmType)
        getTopRatedFilm(genreId, filmType)
        getNowPlayingFilms(genreId, filmType)
        getUpcomingFilms(genreId)
        getBackInTheDaysFilms(genreId, filmType)
        randomMovieId?.let { id -> getRecommendedFilms(id, genreId, filmType) }
    }

    fun filterBySetSelectedGenre(genreDto: GenreDto) {
        selectedGenre = genreDto
        refreshAll(genreDto.id)
    }

    fun getFilmGenre(filmType: FilmType = selectedFilmType) {
        viewModelScope.launch {
            val defaultGenre = GenreDto(null, "All")
            when (val results = genreRepository.getMoviesGenre(filmType)) {
                is Resource.Success -> {
                    _filmGenres.clear()
                    _filmGenres.add(defaultGenre)
                    selectedGenre = defaultGenre
                    results.data?.genres?.forEach {
                        _filmGenres.add(it)
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
            _trendingMovies.value = if (genreId != null) {
                filmRepository.getTrendingFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getTrendingFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getPopularFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _popularFilms.value = if (genreId != null) {
                filmRepository.getPopularFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getPopularFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getTopRatedFilm(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _topRatedFilm.value = if (genreId != null) {
                filmRepository.getTopRatedFilm(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getTopRatedFilm(filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getNowPlayingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _nowPlayingFilm.value = if (genreId != null) {
                filmRepository.getNowPlayingFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getNowPlayingFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    fun getRecommendedFilms(movieId: Int, genreId: Int? = null, filmType: FilmType = selectedFilmType) {
        viewModelScope.launch {
            _recommendedFilms.value = if (genreId != null) {
                filmRepository.getRecommendedFilms(movieId, filmType).map { result ->
                    result.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getRecommendedFilms(movieId, filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getUpcomingFilms(genreId: Int?) {
        viewModelScope.launch {
            _upcomingFilms.value = if (genreId != null) {
                filmRepository.getUpcomingTvShows().map { results ->
                    results.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getUpcomingTvShows().cachedIn(viewModelScope)
            }
        }
    }

    private fun getBackInTheDaysFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _backInTheDaysFilms.value = if (genreId != null) {
                filmRepository.getBackInTheDaysFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds?.contains(genreId) ?: false
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getBackInTheDaysFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }
}
