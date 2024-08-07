package com.ucne.cinetix.data.repository

import android.util.Log
import com.ucne.cinetix.data.local.dao.WatchListDao
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.remote.CineTixApi
import com.ucne.cinetix.data.remote.dto.WatchListDto
import com.ucne.cinetix.data.remote.dto.toEntity
import javax.inject.Inject

class WatchListRepository @Inject constructor(
    private val watchListDao: WatchListDao,
    private val cineTixApi: CineTixApi
) {
    suspend fun addToWatchList(movie: WatchListEntity) = watchListDao.addToWatchList(movie)
    suspend fun removeFromWatchList(watchListId: Int, userId: Int) =
        watchListDao.removeFromWatchList(watchListId, userId)
    suspend fun deleteWatchList(userId: Int) = watchListDao.deleteWatchList(userId)
    suspend fun exists(filmId: Int, userId: Int) = watchListDao.exists(filmId, userId)
    fun getWatchList() = watchListDao.getWatchList()
    fun getFilmsByUser(userId: Int) = watchListDao.getFilmsByUser(userId)
    fun getFilmsById(filmId: List<Int>) = watchListDao.getFilmsById(filmId)

    suspend fun getWatchListFromApi(){
        try{
            val watchList = cineTixApi.getAllWatchList()
            watchList.forEach{ washListItem ->
                val exist = watchListDao.existsByFilmIdAndUserId(
                    washListItem.userId,
                    washListItem.filmId
                )
                if(!exist){
                    addToWatchList(washListItem.toEntity())
                }
            }
        }catch(e: Exception){
            Log.e("Error", "Error fetching watchlist")
        }
    }

    suspend fun addWatchListToApi(watchList: WatchListDto){
        try{
            cineTixApi.addWatchList(watchList)
        }catch(e: Exception){
            Log.e("Error", "Error adding watchlist")
        }
    }

    suspend fun removeFromWatchListToApi(filmId: Int, userId: Int){
        try{
            cineTixApi.removeFromWatchList(filmId, userId)
        }catch(e: Exception){
            Log.e("Error", "Error removing watchlist")
        }
    }

    suspend fun deleteWatchListByUserIdToApi(userId: Int) {
        try {
            cineTixApi.deleteWatchListByUserId(userId)
        } catch (e: Exception) {
            Log.e("Error", "Error deleting watchlist")
        }
    }
}