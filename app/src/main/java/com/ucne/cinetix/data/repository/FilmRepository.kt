package com.ucne.cinetix.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.ucne.cinetix.data.local.dao.CineTixDao
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
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
    //FILM
    fun getTrendingFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { TrendingFilmSource(theMovieDbApi, filmType) }
    }

    fun getPopularFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { PopularFilmSource(theMovieDbApi, filmType) }
    }

    fun getTopRatedFilm(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { TopRatedFilmSource(theMovieDbApi, filmType) }
    }

    fun getNowPlayingFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { NowPlayingFilmSource(theMovieDbApi, filmType) }
    }

    fun getUpcomingTvShows(): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { UpcomingFilmSource(theMovieDbApi) }
    }

    fun getBackInTheDaysFilms(filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { BackInTheDaysFilmSource(theMovieDbApi, filmType) }
    }

    fun getRecommendedFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { RecommendedFilmSource(theMovieDbApi, filmId = movieId, filmType) }
    }

    fun getSimilarFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmDto>> {
        return getPagedFilmsWithDatabaseInsertion { SimilarFilmSource(theMovieDbApi, filmId = movieId, filmType) }
    }

    suspend fun getMovieDetails(movieId: Int): FilmDto {
        insertFilmInDatabase(cineTixDao, theMovieDbApi.getMovieDetails(movieId = movieId))
        return theMovieDbApi.getMovieDetails(movieId = movieId)
    }

    suspend fun getTvShowDetails(seriesId: Int): FilmDto {
        insertFilmInDatabase(cineTixDao, theMovieDbApi.getTvShowDetails(seriesId = seriesId))
        return theMovieDbApi.getTvShowDetails(seriesId = seriesId)
    }

    //LOCAL
    suspend fun insertFilmInDatabase(cineTixDao: CineTixDao, filmDto: FilmDto) {
        val filmEntity = filmDto.toEntity()
        cineTixDao.insertFilm(filmEntity)
    }

    // Insertar cada película en la base de datos
    private fun getPagedFilmsWithDatabaseInsertion(
        pagingSourceFactory: () -> PagingSource<Int, FilmDto>
    ): Flow<PagingData<FilmDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { filmDto ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        insertFilmInDatabase(cineTixDao, filmDto)
                    } catch (e: Exception) {
                        Log.e("FilmRepository", "Error inserting film into database", e)
                    }
                }
                filmDto
            }
        }
    }
}
/*TODO: Que todo lo que se muestre sea por DBROOM*/