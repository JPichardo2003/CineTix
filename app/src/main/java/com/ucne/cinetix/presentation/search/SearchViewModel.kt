package com.ucne.cinetix.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.ucne.cinetix.data.remote.dto.SearchDto
import com.ucne.cinetix.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState = _uiState.asStateFlow()

    init {
        searchRemoteMovie()
    }

    fun searchRemoteMovie() {
        viewModelScope.launch {
            if (uiState.value.searchParam.isNotEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
                delay(500)
                val flow = searchRepository.multiSearch(uiState.value.searchParam)
                    .map { result ->
                        result.filter { movie ->
                            ((movie.title != null || movie.originalName != null
                                    || movie.originalTitle != null) &&
                                    (movie.mediaType == "tv" || movie.mediaType == "movie"))
                        }
                    }.cachedIn(viewModelScope)
                _uiState.update { it.copy(multiSearch = flow) }
            }
        }
    }

    fun onSearchParamChanged(newParam: String) {
        _uiState.update { it.copy(searchParam = newParam) }
    }
}

data class SearchUIState(
    var searchParam: String = "",
    var previousSearch: String = "",
    val multiSearch: Flow<PagingData<SearchDto>> = emptyFlow(),
    var isLoading: Boolean = false
)