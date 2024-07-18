package com.ucne.cinetix.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.SearchDto
import com.ucne.cinetix.paging.SearchFilmSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi
) {
    fun multiSearch(searchParams: String): Flow<PagingData<SearchDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SearchFilmSource(theMovieDbApi = theMovieDbApi, searchParams = searchParams)
            }
        ).flow
    }
}