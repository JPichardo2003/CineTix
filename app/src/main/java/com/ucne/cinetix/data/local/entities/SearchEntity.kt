package com.ucne.cinetix.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ucne.cinetix.data.local.typeconverters.Converters

@Entity(tableName = "SearchFilms")
data class SearchEntity (
    val adult: Boolean?,
    val backdropPath: String?,
    @TypeConverters(Converters::class)
    val genreIds: List<Int>?,
    @TypeConverters(Converters::class)
    val genres: List<GenreEntity>?,
    @PrimaryKey
    val id: Int?,
    val imdbId: String?,
    val mediaType: String?,
    @TypeConverters(Converters::class)
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val releaseDate: String?,
    val releaseDateSeries: String?,
    val title: String?,
    val titleSeries: String?,
    val video: Boolean?,
    val runtime: Int?,
    val voteAverage: Double?,
    val voteCount: Int?
)