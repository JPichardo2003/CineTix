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
import com.ucne.cinetix.paging.BackInTheDaysFilmSource
import com.ucne.cinetix.paging.NowPlayingFilmSource
import com.ucne.cinetix.paging.PopularFilmSource
import com.ucne.cinetix.paging.RecommendedFilmSource
import com.ucne.cinetix.paging.SimilarFilmSource
import com.ucne.cinetix.paging.TopRatedFilmSource
import com.ucne.cinetix.paging.TrendingFilmSource
import com.ucne.cinetix.paging.UpcomingFilmSource
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
    // API methods using FilmDto to fill the database
    fun getTrendingFilms(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { TrendingFilmSource(theMovieDbApi, filmType) }
    }

    fun getPopularFilms(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { PopularFilmSource(theMovieDbApi, filmType) }
    }

    fun getTopRatedFilm(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { TopRatedFilmSource(theMovieDbApi, filmType) }
    }

    fun getNowPlayingFilms(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { NowPlayingFilmSource(theMovieDbApi, filmType) }
    }

    fun getUpcomingTvShows(): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(FilmType.MOVIE) { UpcomingFilmSource(theMovieDbApi) }
    }

    fun getBackInTheDaysFilms(filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { BackInTheDaysFilmSource(theMovieDbApi, filmType) }
    }

    fun getRecommendedFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { RecommendedFilmSource(theMovieDbApi, filmId = movieId, filmType) }
    }

    fun getSimilarFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsWithDatabaseInsertion(filmType) { SimilarFilmSource(theMovieDbApi, filmId = movieId, filmType) }
    }

    suspend fun getMovieDetails(movieId: Int): FilmDto {
        val movieDetails = theMovieDbApi.getMovieDetails(movieId = movieId)
        insertFilmInDatabase(movieDetails, FilmType.MOVIE)
        return movieDetails
    }

    suspend fun getTvShowDetails(seriesId: Int): FilmDto {
        val tvShowDetails = theMovieDbApi.getTvShowDetails(seriesId = seriesId)
        insertFilmInDatabase(tvShowDetails, FilmType.TV)
        return tvShowDetails
    }

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

//    /*fun getRecommendedFilmsFromDB(movieId: Int, filmType: FilmType): Flow<PagingData<FilmEntity>> {
//        return getPagedFilmsFromDatabase { cineTixDao.getRecommendedFilms(movieId, filmType.name.lowercase()) }
//    }*/

    fun getSimilarFilmsFromDB(movieId: Int, filmType: FilmType): Flow<PagingData<FilmEntity>> {
        return getPagedFilmsFromDatabase { cineTixDao.getSimilarFilms(movieId, filmType.name.lowercase()) }
    }

    // Helpers
    suspend fun insertFilmInDatabase(filmDto: FilmDto, filmType: FilmType) {
        val filmEntity = filmDto.toEntity(filmType)
        cineTixDao.insertFilm(filmEntity)
    }

    suspend fun insertFilmsInDatabase(filmDtos: List<FilmDto>, filmType: FilmType) {
        val filmEntities = filmDtos.map { it.toEntity(filmType) }
        cineTixDao.insertFilms(filmEntities)
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
                // Insert film in the database and convert to FilmEntity
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

    //NewIDEA
    suspend fun getTrendingMovieFromApi(filmType: FilmType){
        try{
            val movies = theMovieDbApi.getTrendingMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching trending movies")
        }
    }
    suspend fun getTrendingTvShowFromApi(filmType: FilmType){
        try{
            val series = theMovieDbApi.getTrendingTvSeries(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
            )
            insertFilmsInDatabase(series.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching trending series")

        }
    }

    suspend fun getPopularMovieFromApi(filmType: FilmType){
        try{
            val movies = theMovieDbApi.getPopularMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching popular movies")
        }
    }

    suspend fun getPopularSeriesFromApi(filmType: FilmType){
        try{
            val series = theMovieDbApi.getPopularTvShows(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
                )
            insertFilmsInDatabase(series.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching popular series")
        }
    }

    suspend fun getTopRatedMovieFromApi(filmType: FilmType){
        try{
            val movies = theMovieDbApi.getTopRatedMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching top rated films")
        }
    }

    suspend fun getTopRatedSeriesFromApi(filmType: FilmType){
        try{
            val series = theMovieDbApi.getTopRatedTvShows(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
                )
            insertFilmsInDatabase(series.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching top rated series")
        }
    }

    suspend fun getNowPlayingMovieFromApi(filmType: FilmType){
        try{
            val movies = theMovieDbApi.getNowPlayingMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching now playing films")
        }
    }

    suspend fun getNowPlayingSeriesFromApi(filmType: FilmType){
        try{
            val series = theMovieDbApi.getOnTheAirTvShows(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
                )
            insertFilmsInDatabase(series.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching now playing series")
        }
    }

    suspend fun getUpcomingMoviesFromApi(filmType: FilmType){
        try{
            val movies = theMovieDbApi.getUpcomingMovies(
                1,
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
                )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching upcoming movies")
        }
    }

    suspend fun getBackInTheDaysMovieFromApi(filmType: FilmType){
        try{
            val movies = theMovieDbApi.getBackInTheDaysMovies(
                1,
                "1940-01-01",
                "1981-01-01",
                Constants.API_KEY,
                Constants.LANGUAGE
            )
            insertFilmsInDatabase(movies.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching back in the days films")
        }
    }

    suspend fun getBackInTheDaysSeriesFromApi(filmType: FilmType){
        try{
            val series = theMovieDbApi.getBackInTheDaysTvShows(
                1,
                "1940-01-01",
                "1981-01-01",
                Constants.API_KEY,
                Constants.LANGUAGE_COUNTRY_CODE
                )
            insertFilmsInDatabase(series.results, filmType)
        }catch (e: Exception){
            Log.e("Error", "Error fetching back in the days series")
        }
    }
}

/*TODO: Que todo lo que se muestre sea por DBROOM (DONE)*/