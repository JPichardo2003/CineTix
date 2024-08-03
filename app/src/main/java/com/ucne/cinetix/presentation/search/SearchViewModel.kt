package com.ucne.cinetix.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
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
                searchRepository.getSearchApiToDb(uiState.value.searchParam)
                searchFromDB()
            }
        }
    }

    private fun searchFromDB() {
        viewModelScope.launch {
            if (uiState.value.searchParam.isNotEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
                val flow = searchRepository.searchFromDB(uiState.value.searchParam)
                _uiState.update { it.copy(multiSearch = flow, isLoading = false) }
            }
        }
    }

    fun onSearchParamChanged(newParam: String) {
        _uiState.update { it.copy(searchParam = newParam) }
    }
}

data class SearchUIState(
    var searchParam: String = "",
    val filmGenres: List<GenreEntity> = listOf(GenreEntity(null, "All")),
    val multiSearch: Flow<PagingData<FilmEntity>> = emptyFlow(),
    var isLoading: Boolean = false
)