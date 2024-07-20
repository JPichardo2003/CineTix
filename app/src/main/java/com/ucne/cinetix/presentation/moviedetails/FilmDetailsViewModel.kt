package com.ucne.cinetix.presentation.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.data.repository.FilmRepository
import com.ucne.cinetix.util.FilmType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailsViewModel @Inject constructor(
    val repository: FilmRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FilmDetailsUIState())
    val uiState = _uiState.asStateFlow()

    fun getSimilarFilms(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            repository.getSimilarFilms(filmId, filmType).also {
                uiState.value.similarFilms = it
            }.cachedIn(viewModelScope)
        }
    }
}

data class FilmDetailsUIState(
    var similarFilms: Flow<PagingData<FilmDto>> = emptyFlow(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)