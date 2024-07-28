package com.ucne.cinetix.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.local.entities.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CineTixDao {
    // Query methods
    @Query("SELECT * FROM Films WHERE id = :id")
    suspend fun getFilmById(id: Int): FilmEntity?

    @Query("SELECT * FROM Films")
    fun getAllFilms(): Flow<List<FilmEntity>>

    @Query("SELECT * FROM Films WHERE mediaType = 'movie' AND title IS NOT NULL")
    fun getAllMovies(): Flow<List<FilmEntity>>

    @Query("SELECT * FROM Films WHERE mediaType = 'tv' AND titleSeries IS NOT NULL")
    fun getAllTvShows(): Flow<List<FilmEntity>>

    @Query("SELECT * FROM Genres")
    fun getAllGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM SearchFilms WHERE id = :id")
    suspend fun getSearchResultById(id: Int): SearchEntity?

    // Insert methods
    @Upsert()
    suspend fun insertFilm(film: FilmEntity)

    @Upsert()
    suspend fun insertFilms(films: List<FilmEntity>)

    @Upsert()
    suspend fun insertGenre(genre: GenreEntity)

    @Upsert()
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Upsert()
    suspend fun insertSearchResult(searchResult: SearchEntity)

    @Upsert()
    suspend fun insertSearchResults(searchResults: List<SearchEntity>)

    // PagingSource methods
    @Query("SELECT * FROM Films WHERE mediaType = :filmType ORDER BY strftime('%d', date('now')) DESC")
    fun getTrendingFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType ORDER BY popularity DESC")
    fun getPopularFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType ORDER BY voteAverage DESC")
    fun getTopRatedFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType AND releaseDate IS NOT NULL ORDER BY releaseDate DESC")
    fun getNowPlayingFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType AND releaseDate IS NOT NULL ORDER BY releaseDate DESC")
    fun getUpcomingFilms(filmType: String): PagingSource<Int, FilmEntity>

    @Query("SELECT * FROM Films WHERE mediaType = :filmType AND releaseDate IS NOT NULL ORDER BY releaseDate ASC")
    fun getBackInTheDaysFilms(filmType: String): PagingSource<Int, FilmEntity>
}