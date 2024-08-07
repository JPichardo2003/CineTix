package com.ucne.cinetix.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.ucne.cinetix.data.local.dao.CineTixDao
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.data.remote.dto.toEntity
import com.ucne.cinetix.paging.RecommendedFilmSource
import com.ucne.cinetix.util.Constants
import com.ucne.cinetix.util.FilmType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilmRepository @Inject constructor(
    private val theMovieDbApi: TheMovieDbApi,
    private val cineTixDao: CineTixDao
) {
    // LOCAL methods using FilmEntity
    suspend fun getFilmById(id: Int, filmType: FilmType): FilmEntity? {
        return cineTixDao.getFilmById(id, filmType.name.lowercase())
    }

    fun getTrendingFilmsFromDB(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getTrendingFilms(filmType.name.lowercase()) }
    }

    fun getPopularFilmsFromDB(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getPopularFilms(filmType.name.lowercase()) }
    }

    fun getTopRatedFilmFromDB(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getTopRatedFilms(filmType.name.lowercase()) }
    }

    fun getNowPlayingFilmsFromDB(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getNowPlayingFilms(filmType.name.lowercase()) }
    }

    fun getUpcomingTvShowsFromDB(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getUpcomingFilms(filmType.name.lowercase()) }
    }

    fun getBackInTheDaysFilmsFromDB(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getBackInTheDaysFilms(filmType.name.lowercase()) }
    }

    fun getSimilarFilmsFromDB(filmId: Int, filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getSimilarFilms(filmId ,filmType.name.lowercase()) }
    }

    // Helpers
    private suspend fun insertFilmInDatabase(filmDto: FilmDto, filmType: FilmType) {
        val filmEntity = filmDto.toEntity(filmType)
        cineTixDao.insertFilm(filmEntity)
    }

    private suspend fun insertFilmsInDatabase(filmDtos: List<FilmDto>, filmType: FilmType) {
        val filmEntities = filmDtos.map { it.toEntity(filmType) }
        cineTixDao.insertFilms(filmEntities)
    }

    fun getRecommendedFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) {
            RecommendedFilmSource(theMovieDbApi, filmId = movieId, filmType)
        }
    }

    suspend fun getAllMoviesFromApi(filmType: FilmType) {
        try {
            val trendingMovies = theMovieDbApi.getTrendingMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            val popularMovies = theMovieDbApi.getPopularMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            val topRatedMovies = theMovieDbApi.getTopRatedMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            val nowPlayingMovies = theMovieDbApi.getNowPlayingMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            val upcomingMovies = theMovieDbApi.getUpcomingMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            val backInTheDaysMovies = theMovieDbApi.getBackInTheDaysMovies(
                1,
                "1940-01-01",
                "1981-01-01",
                Constants.API_KEY,
                Constants.LANGUAGE
            )

            val allMovies = trendingMovies.results +
                    popularMovies.results +
                    topRatedMovies.results +
                    nowPlayingMovies.results +
                    upcomingMovies.results +
                    backInTheDaysMovies.results

            insertFilmsInDatabase(allMovies, filmType)
        } catch (e: Exception) {
            Log.e("Error", "Error fetching movies: ${e.message}")
        }
    }

    suspend fun getAllSeriesFromApi(filmType: FilmType) {
        try {
            val trendingSeries = theMovieDbApi.getTrendingTvSeries(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            val popularSeries = theMovieDbApi.getPopularTvShows(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            val topRatedSeries = theMovieDbApi.getTopRatedTvShows(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            val nowPlayingSeries = theMovieDbApi.getOnTheAirTvShows(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            val backInTheDaysSeries = theMovieDbApi.getBackInTheDaysTvShows(
                1,
                "1940-01-01",
                "1981-01-01",
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )

            val allSeries = trendingSeries.results +
                    popularSeries.results +
                    topRatedSeries.results +
                    nowPlayingSeries.results +
                    backInTheDaysSeries.results

            insertFilmsInDatabase(allSeries, filmType)
        } catch (e: Exception) {
            Log.e("Error", "Error fetching series: ${e.message}")
        }
    }



    suspend fun getSimilarMovieFromApi(filmId: Int, filmType: FilmType){
        try{
            val movies = theMovieDbApi.getSimilarMovies(
                filmId,
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching similar movies")
        }
    }

    suspend fun getSimilarSeriesFromApi(filmId: Int, filmType: FilmType){
        try{
            val series = theMovieDbApi.getSimilarTvShows(
                filmId,
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            insertFilmsInDatabase(series.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching similar series")
        }
    }

    private fun getPagedFilmsWithDatabaseInsertion(
        filmType: FilmType,
        pagingSourceFactory: () -> PagingSource<Int, FilmDto>
    ): Flow<PagingData<FilmEntity>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { filmDto ->
                CoroutineScope(Dispatchers.IO).launch {
                    insertFilmInDatabase(filmDto, filmType)
                }
                filmDto.toEntity(filmType)
            }
        }
    }

    private fun getPagedFilmsFromDatabase(
        pagingSourceFactory: () -> PagingSource<Int, FilmEntity>
    ): Flow<PagingData<FilmEntity>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}