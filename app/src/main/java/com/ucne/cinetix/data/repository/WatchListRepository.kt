package com.ucne.cinetix.data.repository

import com.ucne.cinetix.data.local.dao.WatchListDao
import com.ucne.cinetix.data.local.entities.WatchListEntity
import javax.inject.Inject

class WatchListRepository @Inject constructor(
    private val watchListDao: WatchListDao
) {
    suspend fun addToWatchList(movie: WatchListEntity) = watchListDao.addToWatchList(movie)
    suspend fun removeFromWatchList(watchListId: Int) = watchListDao.removeFromWatchList(watchListId)
    suspend fun deleteWatchList() = watchListDao.deleteWatchList()
    suspend fun exists(watchListId: Int) = watchListDao.exists(watchListId)
    fun getWatchList() = watchListDao.getWatchList()
}