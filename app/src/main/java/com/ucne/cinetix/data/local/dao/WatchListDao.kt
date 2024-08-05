package com.ucne.cinetix.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.WatchListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchListDao {
    @Upsert()
    suspend fun addToWatchList(movie: WatchListEntity)

    @Query("DELETE FROM WatchList WHERE filmId =:filmId AND userId = :userId")
    suspend fun removeFromWatchList(filmId: Int, userId: Int)

    @Query("DELETE FROM WatchList WHERE userId = :userId")
    suspend fun deleteWatchList(userId: Int)

    @Query("SELECT EXISTS " +
            "(SELECT 1 FROM WatchList WHERE filmId = :filmId AND userId = :userId)"
    )
    suspend fun exists(filmId: Int, userId: Int): Boolean

    @Query("SELECT * FROM WatchList ORDER BY addedOn DESC")
    fun getWatchList(): Flow<List<WatchListEntity>>

    @Query("SELECT * FROM WatchList WHERE userId = :userId ORDER BY addedOn DESC")
    fun getFilmsByUser(userId: Int): Flow<List<WatchListEntity>>?

    @Query("SELECT * FROM Films WHERE id IN (:filmId)")
    fun getFilmsById(filmId: List<Int>): Flow<List<FilmEntity>>?

}