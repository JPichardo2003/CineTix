package com.ucne.cinetix.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ucne.cinetix.data.local.dao.CineTixDao
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.data.remote.dto.toEntity
import com.ucne.cinetix.paging.SearchFilmSource
import com.ucne.cinetix.util.Constants
import com.ucne.cinetix.util.FilmType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi,
    private val cineTixDao: CineTixDao
) {
    fun multiSearch(searchParams: String): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SearchFilmSource(theMovieDbApi = theMovieDbApi, searchParams = searchParams)
            }
        ).flow
    }

    fun searchFromDB(searchParams: String): Flow<PagingData<FilmEntity>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = { cineTixDao.multiSearch(searchParams) }
        ).flow
    }

    suspend fun getSearchApiToDb(searchParams: String){
        try{
            val films = theMovieDbApi.multiSearch(
                searchParams,
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            films.results.forEach { filmDto ->
                val filmType = if (filmDto.mediaType == "tv") FilmType.TV else FilmType.MOVIE
                insertFilmsInDatabase(listOf(filmDto), filmType)
            }
        }catch (e: Exception) {
            Log.e("Error", "Error fetching search films")
        }
    }

    suspend fun insertFilmsInDatabase(filmDtos: List<FilmDto>, filmType: FilmType) {
        val filmEntities = filmDtos.map { it.toEntity(filmType) }
        cineTixDao.insertFilms(filmEntities)
    }
}