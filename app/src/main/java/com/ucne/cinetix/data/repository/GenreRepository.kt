package com.ucne.cinetix.data.repository

import com.ucne.cinetix.data.local.dao.CineTixDao
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.GenreDto
import com.ucne.cinetix.data.remote.dto.GenreResponse
import com.ucne.cinetix.data.remote.dto.toEntity
import com.ucne.cinetix.util.FilmType
import com.ucne.cinetix.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class GenreRepository @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi,
    private val cineTixDao: CineTixDao
) {
    suspend fun getMoviesGenre(filmType: FilmType): Resource<GenreResponse> {
        val response = try {
            if (filmType == FilmType.MOVIE) {
                theMovieDbApi.getMovieGenres()
            } else {
                theMovieDbApi.getTvShowGenres()
            }
        }
        catch (e: Exception){
            return Resource.Error("Unknown error occurred!")
        }
        response.genres.let { genres ->
            // Mapea los géneros a GenreEntity y los inserta en Room
            val genreEntities = genres.map { it.toEntity() }
            cineTixDao.insertGenres(genreEntities)
        }
        return Resource.Success(response)
    }

    suspend fun getGenresFromDB(filmType: FilmType): List<GenreEntity> {
        return try {
            val genreEntities = theMovieDbApi.getMovieGenres().genres
            genreEntities.map { it.toEntity() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}