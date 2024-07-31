package com.ucne.cinetix.data.remote.dto

import com.squareup.moshi.Json
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.util.FilmType

data class FilmDto(
    @Json(name="adult")
    val adult: Boolean,
    @Json(name="backdrop_path")
    val backdropPath: String?,
    @Json(name="poster_path")
    val posterPath: String?,
    @Json(name="genre_ids")
    val genreIds: List<Int>?,
    @Json(name="genres")
    val genres: List<GenreDto>?,
    @Json(name="media_type")
    val mediaType: String?,
    @Json(name="id")
    val id: Int,
    @Json(name="imdb_id")
    val imdbId: String?,
    @Json(name="original_language")
    val originalLanguage: String?,
    @Json(name="overview")
    val overview: String?,
    @Json(name="popularity")
    val popularity: Double?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "first_air_date")
    val releaseDateSeries: String?,//Alternative Name
    @Json(name="runtime")
    val runtime: Int?,
    @Json(name="title")
    val title: String?,
    @Json(name="name")//Alternative Name
    val titleSeries: String?,
    @Json(name="video")
    val video: Boolean?,
    @Json(name="vote_average")
    val voteAverage: Double?,
    @Json(name="vote_count")
    val voteCount: Int?
)

fun FilmDto.toEntity(filmType: FilmType): FilmEntity {
    return FilmEntity(
        adult = this.adult,
        backdropPath = this.backdropPath,
        posterPath = this.posterPath,
        genreIds = this.genreIds,
        genres = this.genres?.map { GenreEntity(it.id, it.name) },
        mediaType = filmType.name.lowercase(),
        id = this.id,
        imdbId = this.imdbId,
        originalLanguage = this.originalLanguage ?: "No language",
        overview = this.overview ?: "No overview",
        popularity = this.popularity ?: 0.0,
        releaseDate = this.releaseDate,
        releaseDateSeries = this.releaseDateSeries,
        runtime = this.runtime,
        title = this.title,
        titleSeries = this.titleSeries,
        video = this.video,
        voteAverage = this.voteAverage ?: 0.0,
        voteCount = this.voteCount ?: 0
    )
}

data class FilmResponse(
    @Json(name="page")
    val page: Int,
    @Json(name="results")
    val results: List<FilmDto>,
    @Json(name="total_pages")
    val totalPages: Int,
    @Json(name="total_results")
    val totalResults: Int
)