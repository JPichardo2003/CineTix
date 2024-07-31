package com.ucne.cinetix.data.repository

import android.util.Log
import com.ucne.cinetix.data.local.dao.CineTixDao
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.GenreResponse
import com.ucne.cinetix.data.remote.dto.toEntity
import com.ucne.cinetix.util.FilmType
import com.ucne.cinetix.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class GenreRepository @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi,
    private val cineTixDao: CineTixDao
) {
    suspend fun getMoviesGenre(filmType: FilmType): Resource<GenreResponse> {
        val movieGenresResponse = try {
            theMovieDbApi.getMovieGenres()
        } catch (e: Exception) {
            return Resource.Error("Error fetching movie genres: ${e.message}")
        }
        val tvGenresResponse = try {
            theMovieDbApi.getTvShowGenres()
        } catch (e: Exception) {
            return Resource.Error("Error fetching TV show genres: ${e.message}")
        }

        // Combine the genres from both responses
        val allGenres = movieGenresResponse.genres + tvGenresResponse.genres

        val genreEntities = allGenres.map { it.toEntity() }
        cineTixDao.insertGenres(genreEntities)

        val combinedResponse = GenreResponse(genres = allGenres)

        return Resource.Success(combinedResponse)
    }

    fun getGenresFromDB(filmType: FilmType): Flow<List<GenreEntity>> = flow {
        val genreIds = try {
            val response = if (filmType == FilmType.MOVIE) {
                theMovieDbApi.getMovieGenres()
            } else {
                theMovieDbApi.getTvShowGenres()
            }
            response.genres.map { it.id }
        } catch (e: Exception) {
            Log.e("Error", "Error loading Genres from API")
            emptyList<Int>()
        }

        if(genreIds.isNotEmpty()){
            cineTixDao.getGenresByIds(genreIds).collect { genres ->
                emit(genres.sortedBy { it.name })
            }
        }else{
            cineTixDao.getAllGenres().collect { genres ->
                emit(genres)
            }
        }
    }

    suspend fun insertGenres(genres: List<GenreEntity>) {
        cineTixDao.insertGenres(genres)
    }
}

/*TODO:VER COMO DISTINGUIR GENEROS POR FILMTYPE*/