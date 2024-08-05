package com.ucne.cinetix.data.repository

import com.ucne.cinetix.data.local.dao.WatchListDao
import com.ucne.cinetix.data.local.entities.WatchListEntity
import javax.inject.Inject

class WatchListRepository @Inject constructor(
    private val watchListDao: WatchListDao
) {
    suspend fun addToWatchList(movie: WatchListEntity) = watchListDao.addToWatchList(movie)
    suspend fun removeFromWatchList(watchListId: Int, userId: Int) =
        watchListDao.removeFromWatchList(watchListId, userId)
    suspend fun deleteWatchList(userId: Int) = watchListDao.deleteWatchList(userId)
    suspend fun exists(filmId: Int, userId: Int) = watchListDao.exists(filmId, userId)
    fun getWatchList() = watchListDao.getWatchList()
    fun getFilmsByUser(userId: Int) = watchListDao.getFilmsByUser(userId)
    fun getFilmsById(filmId: List<Int>) = watchListDao.getFilmsById(filmId)

}