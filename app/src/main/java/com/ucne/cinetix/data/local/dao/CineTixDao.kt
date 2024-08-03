package com.ucne.cinetix.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CineTixDao {
    // Query methods
    @Query("SELECT * FROM Films WHERE id = :id AND mediaType = :filmType")
    suspend fun getFilmById(id: Int, filmType: String): FilmEntity?

    @Query("SELECT * FROM Genres")
    fun getAllGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM Genres WHERE id IN (:genreIds)")
    fun getGenresByIds(genreIds: List<Int?>): Flow<List<GenreEntity>>

    // Insert methods
    @Upsert()
    suspend fun insertFilm(film: FilmEntity)

    @Upsert()
    suspend fun insertFilms(films: List<FilmEntity>)

    @Upsert()
    suspend fun insertGenre(genre: GenreEntity)

    @Upsert()
    suspend fun insertGenres(genres: List<GenreEntity>)

    // PagingSource methods
    @Query("""
    SELECT * FROM Films 
    WHERE mediaType = :filmType 
    AND voteAverage >= 8
    AND voteCount >= 200
    AND backdropPath IS NOT NULL
    ORDER BY 
             CASE 
                WHEN releaseDate IS NOT NULL THEN releaseDate 
                ELSE releaseDateSeries 
             END DESC 
    """)
    fun getTrendingFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType ORDER BY popularity DESC")
    fun getPopularFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType AND voteCount >= 200 ORDER BY voteAverage DESC")
    fun getTopRatedFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("""
    SELECT * FROM Films 
    WHERE mediaType = :filmType 
    AND (releaseDate IS NOT NULL OR releaseDateSeries IS NOT NULL) 
    AND (
        (releaseDate BETWEEN date('now', '-1 month') AND date('now')) 
        OR 
        (releaseDateSeries BETWEEN date('now', '-1 month') AND date('now'))
    ) 
    ORDER BY popularity DESC
    """)
    fun getNowPlayingFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("""
    SELECT * FROM Films 
    WHERE mediaType = :filmType 
    AND releaseDate IS NOT NULL 
    AND releaseDate BETWEEN date('now', '+1 day') AND date('now', '+4 year') 
    ORDER BY popularity DESC
    """)
    fun getUpcomingFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("""
    SELECT * FROM Films 
    WHERE mediaType = :filmType 
    AND (releaseDate IS NOT NULL OR releaseDateSeries IS NOT NULL) 
    AND (
        (releaseDate BETWEEN '1940-01-01' AND '1981-01-01') 
        OR 
        (releaseDateSeries BETWEEN '1940-01-01' AND '1981-01-01')
    ) 
    ORDER BY popularity DESC
    """)
    fun getBackInTheDaysFilms(filmType: String): PagingSource<Int, FilmEntity>

    //Obtener las films similares
    @Query("""
    SELECT * FROM Films 
    WHERE mediaType = :filmType
    AND id != :filmId
    ORDER BY popularity DESC
    """)
    fun getSimilarFilms(filmId: Int, filmType: String): PagingSource<Int, FilmEntity>

    @Query("""
        SELECT * FROM Films 
        WHERE title LIKE '%' || :searchParams || '%' OR titleSeries LIKE '%' || :searchParams || '%'
    """)
    fun multiSearch(searchParams: String): PagingSource<Int, FilmEntity>
}