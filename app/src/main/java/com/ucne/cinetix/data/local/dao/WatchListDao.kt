package com.ucne.cinetix.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ucne.cinetix.data.local.entities.WatchListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchListDao {

    @Upsert()
    suspend fun addToWatchList(movie: WatchListEntity)

    @Query("DELETE FROM WatchList WHERE watchListId =:watchListId")
    suspend fun removeFromWatchList(watchListId: Int)

    @Query("DELETE FROM WatchList")
    suspend fun deleteWatchList()

    @Query("SELECT EXISTS (SELECT 1 FROM WatchList WHERE watchListId = :watchListId)")
    suspend fun exists(watchListId: Int): Int

    @Query("SELECT * FROM WatchList ORDER BY addedOn DESC")
    fun getWatchList(): Flow<List<WatchListEntity>>
}