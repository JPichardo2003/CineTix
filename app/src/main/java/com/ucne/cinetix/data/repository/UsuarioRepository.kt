package com.ucne.cinetix.data.repository

import com.ucne.cinetix.data.local.dao.UsuarioDao
import com.ucne.cinetix.data.local.entities.UsuarioEntity
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend fun saveUser(usuario: UsuarioEntity) = usuarioDao.save(usuario)
    suspend fun deleteUser(usuario: UsuarioEntity) = usuarioDao.delete(usuario)
    suspend fun getUserById(userId: Int) = usuarioDao.find(userId)
    suspend fun getUserByEmail(email: String) = usuarioDao.getUserByEmail(email)
    fun getUsers() = usuarioDao.getALl()
}