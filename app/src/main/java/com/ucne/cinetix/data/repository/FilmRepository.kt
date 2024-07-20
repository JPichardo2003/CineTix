package com.ucne.cinetix.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ucne.cinetix.paging.BackInTheDaysFilmSource
import com.ucne.cinetix.paging.NowPlayingFilmSource
import com.ucne.cinetix.paging.PopularFilmSource
import com.ucne.cinetix.paging.RecommendedFilmSource
import com.ucne.cinetix.paging.TopRatedFilmSource
import com.ucne.cinetix.paging.TrendingFilmSource
import com.ucne.cinetix.paging.UpcomingFilmSource
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.paging.SimilarFilmSource
import com.ucne.cinetix.util.FilmType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilmRepository @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi
) {
    fun getTrendingFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                TrendingFilmSource(theMovieDbApi, filmType)
            }
        ).flow
    }

    fun getPopularFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                PopularFilmSource(theMovieDbApi, filmType)
            }
        ).flow
    }

    fun getTopRatedFilm(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                TopRatedFilmSource(theMovieDbApi, filmType)
            }
        ).flow
    }

    fun getNowPlayingFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                NowPlayingFilmSource(theMovieDbApi, filmType)
            }
        ).flow
    }

    fun getUpcomingTvShows(): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                UpcomingFilmSource(theMovieDbApi)
            }
        ).flow
    }

    fun getBackInTheDaysFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                BackInTheDaysFilmSource(theMovieDbApi, filmType)
            }
        ).flow
    }

    fun getRecommendedFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                RecommendedFilmSource(theMovieDbApi, filmId = movieId, filmType)
            }
        ).flow
    }

    fun getSimilarFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SimilarFilmSource(theMovieDbApi, filmId = movieId, filmType)
            }
        ).flow
    }

    suspend fun getMovieDetails(movieId: Int): FilmDto {
        return theMovieDbApi.getMovieDetails(movieId = movieId)
    }

    suspend fun getTvShowDetails(seriesId: Int): FilmDto {
        return theMovieDbApi.getTvShowDetails(seriesId = seriesId)
    }

}