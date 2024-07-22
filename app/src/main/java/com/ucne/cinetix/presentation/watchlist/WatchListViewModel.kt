package com.ucne.cinetix.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.repository.WatchListRepository
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
    private val watchListRepository: WatchListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchListUIState())
    val uiState = _uiState.asStateFlow()

    val watchList = watchListRepository.getWatchList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            getWatchList()
        }
    }

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

    private fun getWatchList() {
        viewModelScope.launch {
            val watchListFlow = watchListRepository.getWatchList()
            _uiState.update { it.copy(watchList = watchListFlow) }
        }
    }

    fun deleteWatchList() {
        viewModelScope.launch {
            watchListRepository.deleteWatchList()
        }
    }
}

data class WatchListUIState(
    val addedToWatchList: Int = 0,
    val watchList: Flow<List<WatchListEntity>> = emptyFlow()
)
