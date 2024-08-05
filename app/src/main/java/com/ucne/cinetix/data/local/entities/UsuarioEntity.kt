package com.ucne.cinetix.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val userId: Int? = null,
    val userName: String? = "",
    val email: String? = "",
    val password: String? = ""
)
