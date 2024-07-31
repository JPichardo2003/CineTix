package com.ucne.cinetix.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ucne.cinetix.data.local.dao.CineTixDao
import com.ucne.cinetix.data.local.dao.WatchListDao
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.local.entities.SearchEntity
import com.ucne.cinetix.data.local.entities.WatchListEntity
import com.ucne.cinetix.data.local.typeconverters.Converters

@Database(
    entities = [
        FilmEntity::class,
        GenreEntity::class,
        SearchEntity::class,
        WatchListEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CineTixDb : RoomDatabase() {
    abstract fun watchListDao(): WatchListDao
    abstract fun cineTixDao(): CineTixDao
}