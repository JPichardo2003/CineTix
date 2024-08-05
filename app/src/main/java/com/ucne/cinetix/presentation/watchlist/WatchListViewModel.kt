package com.ucne.cinetix.presentation.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.repository.AuthRepository
import com.ucne.cinetix.data.repository.UsuarioRepository
import com.ucne.cinetix.data.repository.WatchListRepository
import com.ucne.cinetix.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val watchListRepository: WatchListRepository,
    private val userRepository: UsuarioRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchListUIState())
    val uiState = _uiState.asStateFlow()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    val watchList = watchListRepository.getWatchList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val usuarios = userRepository.getUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
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


    fun getUserIdByEmail() {
        viewModelScope.launch {
            val usuario = userRepository.getUserByEmail(uiState.value.email ?: "")
            _uiState.update {
                it.copy(
                    userId = usuario?.userId
                )
            }
        }
    }

    fun getFilmsByUser(userId: Int) {
        viewModelScope.launch {
            val filmsByUser = watchListRepository.getFilmsByUser(userId)
            _uiState.update { it.copy(watchList = filmsByUser) }
        }
    }


    private fun exists(mediaId: Int, userId: Int) {
        viewModelScope.launch {
            val exists = watchListRepository.exists(mediaId, userId)
            _uiState.update { it.copy(addedToWatchList = exists) }
        }
    }

    fun removeFromWatchList(filmId: Int, userId: Int) {
        viewModelScope.launch {
            watchListRepository.removeFromWatchList(filmId, userId)
        }
    }


    fun deleteWatchList(userId: Int) {
        viewModelScope.launch {
            watchListRepository.deleteWatchList(userId)
        }
    }

    fun getFilmsById(filmId: List<Int>) {
        viewModelScope.launch {
            watchListRepository.getFilmsById(filmId)?.collect { films ->
                _uiState.update { it.copy(film = films) }
            }
        }
    }
}

data class WatchListUIState(
    val addedToWatchList: Boolean = false,
    var watchList: Flow<List<WatchListEntity>>? = emptyFlow(),
    val film: List<FilmEntity> = emptyList(),
    val email: String? = null,
    val userId: Int? = null
)
