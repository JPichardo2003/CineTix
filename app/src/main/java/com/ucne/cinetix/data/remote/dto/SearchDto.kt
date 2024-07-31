package com.ucne.cinetix.data.remote.dto

import com.squareup.moshi.Json
import com.ucne.cinetix.data.local.entities.FilmEntity
import com.ucne.cinetix.data.local.entities.GenreEntity
import com.ucne.cinetix.data.local.entities.SearchEntity
import com.ucne.cinetix.util.FilmType

//Es lo mismo que FilmDto pero en esta clase all es nullable
data class SearchDto(
    @Json(name="adult")
    val adult: Boolean?,
    @Json(name="backdrop_path")
    val backdropPath: String?,
    @Json(name="genre_ids")
    val genreIds: List<Int>?,
    @Json(name="genres")
    val genres: List<GenreDto>?,
    @Json(name="id")
    val id: Int?,
    @Json(name="imdb_id")
    val imdbId: String?,
    @Json(name="media_type")
    val mediaType: String?,
    @Json(name="origin_country")
    val originCountry: List<String>?,
    @Json(name="original_language")
    val originalLanguage: String?,
    @Json(name="original_name")
    val originalName: String?,
    @Json(name="original_title")
    val originalTitle: String?,
    @Json(name="overview")
    val overview: String?,
    @Json(name="popularity")
    val popularity: Double?,
    @Json(name="poster_path")
    val posterPath: String?,
    @Json(name="release_date")
    val releaseDate: String?,
    @Json(name = "first_air_date") //Alternative Name
    val releaseDateSeries: String?,
    @Json(name="title")
    val title: String?,
    @Json(name="name")//Alternative Name
    val titleSeries: String?,
    @Json(name="video")
    val video: Boolean?,
    @Json(name="runtime")
    val runtime: Int?,
    @Json(name="vote_average")
    val voteAverage: Double?,
    @Json(name="vote_count")
    val voteCount: Int?
)

fun SearchDto.toEntity(filmType: FilmType): FilmEntity{
    return FilmEntity(
        adult = this.adult ?: false,
        backdropPath = this.backdropPath,
        posterPath = this.posterPath,
        genreIds = this.genreIds,
        genres = this.genres?.map { GenreEntity(it.id, it.name) },
        mediaType = filmType.name.lowercase(),
        id = this.id ?: 0,
        imdbId = this.imdbId,
        originalLanguage = this.originalLanguage ?: "",
        overview = this.overview ?: "",
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

data class MultiSearchResponse(
    @Json(name="page")
    val page: Int,
    @Json(name="results")
    val results: List<FilmDto>,
    @Json(name="total_pages")
    val totalPages: Int,
    @Json(name="total_results")
    val totalResults: Int
)