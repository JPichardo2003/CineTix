package com.ucne.cinetix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.ucne.cinetix.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Upsert
    suspend fun save(usuario: UsuarioEntity)

    @Delete
    suspend fun delete(usuario: UsuarioEntity)

    @Query("SELECT * FROM Usuarios WHERE userId = :userId LIMIT 1")
    suspend fun find(userId: Int): UsuarioEntity?

    @Query("SELECT * FROM Usuarios")
    fun getALl(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM Usuarios WHERE email LIKE :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM Usuarios WHERE userName = :userName")
    suspend fun exists(userName: String): Boolean
}