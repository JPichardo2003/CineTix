package com.ucne.cinetix.data.repository

import android.util.Log
import com.ucne.cinetix.data.local.dao.UsuarioDao
import com.ucne.cinetix.data.local.entities.UsuarioEntity
import com.ucne.cinetix.data.remote.CineTixApi
import com.ucne.cinetix.data.remote.dto.UsuarioDto
import com.ucne.cinetix.data.remote.dto.toEntity
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val cineTixApi: CineTixApi
) {
    suspend fun saveUser(usuario: UsuarioEntity) = usuarioDao.save(usuario)
    suspend fun deleteUser(usuario: UsuarioEntity) = usuarioDao.delete(usuario)
    suspend fun getUserById(userId: Int) = usuarioDao.find(userId)
    suspend fun getUserByEmail(email: String) = usuarioDao.getUserByEmail(email)
    fun getUsers() = usuarioDao.getALl()

    suspend fun getUsersFromApi(){
        try{
            val users = cineTixApi.getUsuarios()
            users.forEach{
                saveUser(it.toEntity())
            }
        }catch(e: Exception){
            Log.e("Error", "Error fetching users")
        }
    }

    suspend fun addUserToApi(usuario: UsuarioDto){
        try{
            cineTixApi.addUsuarios(usuario)
        }catch(e: Exception){
            Log.e("Error", "Error adding user to api")
        }
    }
}