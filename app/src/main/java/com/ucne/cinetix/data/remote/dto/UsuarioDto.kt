package com.ucne.cinetix.data.remote.dto

import com.ucne.cinetix.data.local.entities.UsuarioEntity

data class UsuarioDto (
    val userId: Int? = null,
    val userName: String? = "",
    val email: String? = "",
    val password: String? = ""
)

fun UsuarioDto.toEntity(): UsuarioEntity {
    return UsuarioEntity(
        userId = this.userId,
        userName = this.userName,
        email = this.email,
        password = this.password
    )
}