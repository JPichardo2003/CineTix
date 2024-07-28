package com.ucne.cinetix.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ucne.cinetix.data.local.typeconverters.Converters

@Entity(tableName = "Films")
data class FilmEntity(
    val adult: Boolean,
    val backdropPath: String?,
    val posterPath: String?,
    @TypeConverters(Converters::class)
    val genreIds: List<Int>?,
    @TypeConverters(Converters::class)
    val genres: List<GenreEntity>?,
    val mediaType: String?,
    @PrimaryKey
    val id: Int,
    val imdbId: String?,
    val originalLanguage: String,
    val overview: String,
    val popularity:Double,
    val releaseDate: String?,
    val releaseDateSeries: String?,
    val runtime: Int?,
    val title: String?,
    val titleSeries: String?,
    val video: Boolean?,
    val voteAverage: Double,
    val voteCount: Int
)