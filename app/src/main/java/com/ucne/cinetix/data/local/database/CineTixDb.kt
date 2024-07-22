package com.ucne.cinetix.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ucne.cinetix.data.local.dao.WatchListDao
import com.ucne.cinetix.data.local.entities.WatchListEntity

@Database(
    entities = [
        WatchListEntity::class
    ],
    version = 4,
    exportSchema = false
)

abstract class CineTixDb : RoomDatabase() {
    abstract fun watchListDao(): WatchListDao
}