package com.ucne.cinetix.data.remote

import com.ucne.cinetix.data.remote.dto.UsuarioDto
import com.ucne.cinetix.data.remote.dto.WatchListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CineTixApi {
    //USUARIOS
    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): UsuarioDto
    @GET("api/Usuarios")
    suspend fun getUsuarios(): List<UsuarioDto>
    @POST("api/Usuarios")
    suspend fun addUsuarios(@Body usuarioDto: UsuarioDto?): UsuarioDto
    @PUT("api/Usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuarioDto: UsuarioDto?): UsuarioDto
    @DELETE("api/Usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>

    //WATCHLIST
    @GET("api/WatchList/{id}")
    suspend fun getWatchList(@Path("id") id: Int): WatchListDto
    @GET("api/WatchList")
    suspend fun getAllWatchList(): List<WatchListDto>
    @POST("api/WatchList")
    suspend fun addWatchList(@Body watchListDto: WatchListDto?): WatchListDto
    @PUT("api/WatchList/{id}")
    suspend fun updateWatchList(@Path("id") id: Int, @Body watchListDto: WatchListDto?): WatchListDto
    @DELETE("api/WatchList/{id}")
    suspend fun deleteWatchList(@Path("id") id: Int): Response<Unit>
    @DELETE("api/WatchList/user/{userId}")
    suspend fun deleteWatchListByUserId(@Path("userId") userId: Int): Response<Unit>
    @DELETE("api/WatchList/{filmId}/{userId}")
    suspend fun removeFromWatchList(@Path("filmId") filmId: Int, @Path("userId") userId: Int): Response<Unit>
}