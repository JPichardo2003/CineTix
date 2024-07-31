package com.ucne.cinetix.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.remote.dto.toEntity
import com.ucne.cinetix.data.repository.FilmRepository
import com.ucne.cinetix.data.repository.GenreRepository
import com.ucne.cinetix.util.FilmType
import com.ucne.cinetix.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            getFilmGenre()
        }
        if (genreId == null) {
            _uiState.update { it.copy(selectedGenre = GenreEntity(null, "All")) }
        }

        getDataFromAPi(filmType)
        getTrendingFilms(genreId, filmType)
        getPopularFilms(genreId, filmType)
        getTopRatedFilm(genreId, filmType)
        getNowPlayingFilms(genreId, filmType)
        getUpcomingFilms(genreId)
        getBackInTheDaysFilms(genreId, filmType)
        uiState.value.randomMovieId?.let { id -> getRecommendedFilms(id, genreId, filmType) }
    }

    private fun getDataFromAPi(filmType: FilmType = uiState.value.selectedFilmType){
        viewModelScope.launch {
            if(filmType == FilmType.MOVIE){
                filmRepository.getTrendingMovieFromApi(filmType)
                filmRepository.getPopularMovieFromApi(filmType)
                filmRepository.getTopRatedMovieFromApi(filmType)
                filmRepository.getNowPlayingMovieFromApi(filmType)
                filmRepository.getBackInTheDaysMovieFromApi(filmType)
                filmRepository.getUpcomingMoviesFromApi(filmType)
            }else{
                filmRepository.getTrendingTvShowFromApi(filmType)
                filmRepository.getPopularSeriesFromApi(filmType)
                filmRepository.getTopRatedSeriesFromApi(filmType)
                filmRepository.getNowPlayingSeriesFromApi(filmType)
                filmRepository.getBackInTheDaysSeriesFromApi(filmType)
            }

        }
    }

    fun filterBySetSelectedGenre(genre: GenreEntity) {
        _uiState.update { it.copy(selectedGenre = genre) }
        refreshAll(genre.id)
    }

    fun getFilmGenre(filmType: FilmType = uiState.value.selectedFilmType) {
        viewModelScope.launch {
            val defaultGenre = GenreEntity(null, "All")
            genreRepository.getGenresFromDB(filmType).onEach { genresFromDB ->
                if (genresFromDB.isEmpty()) {
                    val apiResponse = genreRepository.getMoviesGenre(filmType)
                    if (apiResponse is Resource.Success) {
                        genreRepository.insertGenres(apiResponse.data?.genres?.map { it.toEntity() } ?: emptyList())
                    } else {
                        Log.e("Error", "Error loading Genres from API")
                    }
                }
                _uiState.update {
                    it.copy(
                        filmGenres = listOf(defaultGenre) + genresFromDB
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getTrendingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    trendingMoviesState = if (genreId != null) {
                        filmRepository.getTrendingFilmsFromDB(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getTrendingFilmsFromDB(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getPopularFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(popularFilmsState = if (genreId != null) {
                    filmRepository.getPopularFilmsFromDB(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getPopularFilmsFromDB(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getTopRatedFilm(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(topRatedFilmState = if (genreId != null) {
                    filmRepository.getTopRatedFilmFromDB(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getTopRatedFilmFromDB(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getNowPlayingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(nowPlayingMoviesState = if (genreId != null) {
                    filmRepository.getNowPlayingFilmsFromDB(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getNowPlayingFilmsFromDB(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getUpcomingFilms(genreId: Int?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(upcomingMoviesState = if (genreId != null) {
                    filmRepository.getUpcomingTvShowsFromDB(filmType = FilmType.MOVIE).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getUpcomingTvShowsFromDB(filmType = FilmType.MOVIE).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getBackInTheDaysFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(backInTheDaysMoviesState = if (genreId != null) {
                    filmRepository.getBackInTheDaysFilmsFromDB(filmType).map { results ->
                        results.filter { movie ->
                            movie.genreIds?.contains(genreId) ?: false
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    filmRepository.getBackInTheDaysFilmsFromDB(filmType).cachedIn(viewModelScope)
                })
            }
        }
    }

    private fun getRecommendedFilms(movieId: Int, genreId: Int? = null, filmType: FilmType = uiState.value.selectedFilmType) {
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

    fun onGenreChanged(genre: GenreEntity) {
        _uiState.update { it.copy(selectedGenre = genre) }
    }

    fun onFilmTypeChanged(filmType: FilmType) {
        _uiState.update { it.copy(selectedFilmType = filmType) }
    }
}


data class HomeUIState(
    val filmGenres: List<GenreEntity> = listOf(GenreEntity(null, "All")),
    var selectedGenre: GenreEntity = GenreEntity(null, "All"),
    var selectedFilmType: FilmType = FilmType.MOVIE,
    val filmFromApi: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val trendingMoviesState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val popularFilmsState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val topRatedFilmState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val nowPlayingMoviesState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val upcomingMoviesState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val backInTheDaysMoviesState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val recommendedFilmsState: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val randomMovieId: Int? = null,
    val movieDetails: FilmEntity? = null,
    val tvShowDetails: FilmEntity? = null
)
