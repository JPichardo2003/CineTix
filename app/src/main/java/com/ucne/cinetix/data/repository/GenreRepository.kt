package com.ucne.cinetix.data.repository

import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.response.GenreResponse
import com.ucne.cinetix.util.FilmType
import com.ucne.cinetix.util.Resource
import java.lang.Exception
import javax.inject.Inject

class GenreRepository @Inject constructor(private val theMovieDbApi: TheMovieDbApi) {
    suspend fun getMoviesGenre(filmType: FilmType): Resource<GenreResponse> {
        val response = try {
            if (filmType == FilmType.MOVIE) theMovieDbApi.getMovieGenres() else theMovieDbApi.getTvShowGenres()
        } catch (e: Exception){
            return Resource.Error("Unknown error occurred!")
        }
        return Resource.Success(response)
    }
}