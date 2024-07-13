package com.ucne.cinetix.data.remote

import com.ucne.cinetix.data.remote.response.FilmResponse
import com.ucne.cinetix.data.remote.response.GenreResponse
import com.ucne.cinetix.util.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbApi {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): FilmResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): FilmResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): FilmResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): FilmResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): FilmResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendedMovies(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): FilmResponse

    @GET("discover/movie?")
    suspend fun getBackInTheDaysMovies(
        @Query("page") page: Int = 0,
        @Query("primary_release_date.gte") gteReleaseDate: String = "1940-01-01",
        @Query("primary_release_date.lte") lteReleaseDate: String = "1981-01-01",
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE,
        @Query("sort_by") sortBy: String = "vote_count.desc"
    ): FilmResponse

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): GenreResponse

    @GET("genre/tv/list")
    suspend fun getTvShowGenres(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE
    ): GenreResponse

    @GET("trending/tv/day")
    suspend fun getTrendingTvSeries(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE
    ): FilmResponse

    @GET("tv/popular")
    suspend fun getPopularTvShows(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE
    ): FilmResponse


    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE
    ): FilmResponse

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTvShows(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE
    ): FilmResponse

    @GET("tv/{tv_id}/recommendations")
    suspend fun getRecommendedTvShows(
        @Path("tv_id") filmId: Int,
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE
    ): FilmResponse

    @GET("discover/tv?")
    suspend fun getBackInTheDaysTvShows(
        @Query("page") page: Int = 0,
        @Query("first_air_date.gte") gteFirstAirDate: String = "1940-01-01",
        @Query("first_air_date.lte") lteFirstAirDate: String = "1981-01-01",
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("language") language: String = Constants.LANGUAGE_COUNTRY_CODE,
        @Query("sort_by") sortBy: String = "vote_count.desc"
    ): FilmResponse
}