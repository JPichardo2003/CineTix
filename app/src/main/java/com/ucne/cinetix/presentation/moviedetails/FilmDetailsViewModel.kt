package com.ucne.cinetix.presentation.moviedetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.remote.dto.WatchListDto
import com.ucne.cinetix.data.repository.AuthRepository
import com.ucne.cinetix.data.repository.FilmRepository
import com.ucne.cinetix.data.repository.GenreRepository
import com.ucne.cinetix.data.repository.UsuarioRepository
import com.ucne.cinetix.data.repository.WatchListRepository
import com.ucne.cinetix.presentation.auth.AuthState
import com.ucne.cinetix.util.FilmType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FilmDetailsViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val genreRepository: GenreRepository,
    private val watchListRepository: WatchListRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilmDetailsUIState())
    val uiState = _uiState.asStateFlow()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    val usuarios = userRepository.getUsers()
        .stateIn(
            scope =viewModelScope,
            started =  SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    private fun checkAuthStatus(){
        viewModelScope.launch {
            authRepository.checkAuthStatus().collect{
                _authState.value = it
            }
            if(_authState.value is AuthState.Authenticated){
                _uiState.update {
                    it.copy(
                        email = (_authState.value as AuthState.Authenticated).email,
                    )
                }
            }
        }

    }

    private fun getUserIdByEmail() {
        viewModelScope.launch {
            val usuario = userRepository.getUserByEmail(uiState.value.email ?: "")
            _uiState.update {
                it.copy(
                    userId = usuario?.userId
                )
            }
        }
    }

    private fun getFilmsData() {
        val filmType = uiState.value.selectedFilmType
        val filmId = uiState.value.film?.id ?: 0
        getSimilarFilmsRemote(filmId, filmType)
    }

    private fun getSimilarFilmsRemote(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            if (filmType == FilmType.MOVIE){
                filmRepository.getSimilarMovieFromApi(filmId, filmType)
            }
            else {
                filmRepository.getSimilarSeriesFromApi(filmId, filmType)
            }
        }
        getSimilarFilmsFromDb(filmId, filmType)
    }

    private fun getSimilarFilmsFromDb(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            filmRepository.getSimilarFilmsFromDB(filmId, filmType).also {
                uiState.value.similarFilms = it
            }.cachedIn(viewModelScope)
        }
    }

    fun getFilmById(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            getUserIdByEmail()
            exists(filmId, uiState.value.userId ?: 0)
            val film = filmRepository.getFilmById(filmId, filmType)
            _uiState.update {
                it.copy(film = film)
            }
        }
    }

    private fun getFilmGenre(filmType: FilmType = uiState.value.selectedFilmType) {
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
    private fun addToWatchList(filmId: Int, userId: Int) {
        val date = SimpleDateFormat.getDateTimeInstance().format(Date())
        viewModelScope.launch {
            watchListRepository.addToWatchList(
                WatchListEntity(
                    userId = userId,
                    filmId = filmId,
                    addedOn = date
                )
            )
            try{
                watchListRepository.addWatchListToApi(
                    WatchListDto(
                        userId = userId,
                        filmId = filmId,
                        addedOn = date
                    )
                )
            }catch(e: Exception){
                Log.e("Error", "Error adding watchlist to API")
            }

        }
    }

    fun onWatchListChanged(filmId: Int?, userId: Int?) {
        viewModelScope.launch {
            var addedToWatchList = watchListRepository.exists(filmId?: 0, userId?: 0)
            if(!addedToWatchList){
                addToWatchList(filmId?: 0, userId?: 0)
                addedToWatchList = true
            }
            else{
                removeFromWatchList(filmId?: 0, userId?: 0)
                addedToWatchList = false
            }
            _uiState.update {
                it.copy(
                    addedToWatchList = addedToWatchList
                )
            }
        }
    }

    private fun exists(filmId: Int, userId: Int) {
        viewModelScope.launch {
            val exists = watchListRepository.exists(filmId, userId)
            _uiState.update { it.copy(addedToWatchList = exists) }
        }
    }

    private fun removeFromWatchList(filmId: Int, userId: Int) {
        viewModelScope.launch {
            watchListRepository.removeFromWatchList(filmId, userId)
            try{
                watchListRepository.removeFromWatchListToApi(filmId, userId)
            }catch(e: Exception){
                Log.e("Error", "Error deleting watchlist from API")
            }
        }
    }

    suspend fun getSimilars(filmType: FilmType, filmId: Int) {
        _uiState.update {
            val film = filmRepository.getFilmById(filmId, filmType)
            it.copy(
                selectedFilmType = filmType,
                film = film
            )
        }
        getFilmGenre(filmType)
        getFilmsData()
    }
}

data class FilmDetailsUIState(
    var similarFilms: Flow<PagingData<FilmEntity>> = emptyFlow(),
    val film: FilmEntity? = null,
    val userId: Int? = null,
    val filmGenres: List<GenreEntity> = listOf(GenreEntity(null, "All")),
    val selectedFilmType: FilmType = FilmType.MOVIE,
    val addedToWatchList: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String? = null
)