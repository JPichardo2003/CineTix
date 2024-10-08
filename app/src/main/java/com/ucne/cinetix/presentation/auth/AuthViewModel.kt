package com.ucne.cinetix.presentation.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.cinetix.data.local.entities.UsuarioEntity
import com.ucne.cinetix.data.remote.dto.UsuarioDto
import com.ucne.cinetix.data.repository.AuthRepository
import com.ucne.cinetix.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUIState())
    val uiState = _uiState.asStateFlow()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    val usuarios = userRepository.getUsers()
        .stateIn(
            scope =viewModelScope,
            started =  SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus(){
        viewModelScope.launch {
            authRepository.checkAuthStatus().collect{
                _authState.value = it
            }
            if(_authState.value is AuthState.Authenticated){
                _uiState.update {
                    it.copy(
                        email = (_authState.value as AuthState.Authenticated).email,
                    )
                }
            }
            getUsuariosFromApi()
            getUserNameByEmail()
            getPasswordByEmail()
        }
    }

    fun login(email : String,password : String){
        viewModelScope.launch {
            authRepository.login(email,password).collect{
                _authState.value = it
            }
        }
    }

    fun signUp(email : String, password : String){
        viewModelScope.launch {
            val usernameExists = userRepository.existUserName(_uiState.value.userName)
            if (usernameExists) {
                _authState.value = AuthState.Error("El nombre de usuario ya existe.")
            } else {
                authRepository.signUp(email,password).collect{
                    _authState.value = it
                }
                if(_authState.value is AuthState.Authenticated){
                    saveUser()
                }
            }
        }
    }

    fun signOut(){
        viewModelScope.launch {
            authRepository.signOut().collect{
                _authState.value = it
            }
        }
    }

    private fun getUserNameByEmail() {
        viewModelScope.launch {
            val usuario = userRepository.getUserByEmail(uiState.value.email ?: "")
            _uiState.update {
                it.copy(
                    userName = usuario?.userName ?: ""
                )
            }
        }
    }

    private fun getPasswordByEmail() {
        viewModelScope.launch {
            val usuario = userRepository.getUserByEmail(uiState.value.email ?: "")
            _uiState.update {
                it.copy(
                    password = usuario?.password ?: ""
                )
            }
        }
    }

    private fun getUsuariosFromApi(){
        viewModelScope.launch {
            userRepository.getUsersFromApi()
        }
    }

    private fun saveUser(){
        viewModelScope.launch {
            userRepository.saveUser(_uiState.value.toEntity())
            try{
                userRepository.addUserToApi(_uiState.value.toDto())
            }catch(e: Exception){
                Log.e("Error", "Error adding user to api")
            }
        }
    }

    fun onUserNameChanged(userName : String){
        if(!userName.startsWith(" ")) {
            _uiState.update {
                it.copy(
                    userName = userName
                )
            }
        }
    }

    fun onEmailChanged(email : String){
        if(!email.startsWith(" ")){
            _uiState.update {
                it.copy(
                    email = email
                )
            }
        }
    }

    fun onPasswordChanged(password : String){
        if(!password.startsWith(" ")){
            _uiState.update {
                it.copy(
                    password = password
                )
            }
        }
    }
}

sealed class AuthState{
    data class Authenticated(val email: String) : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}

data class UsuarioUIState(
    val userId: Int? = null,
    var userName: String = "",
    var email: String? = "",
    var password: String? = ""
)

fun UsuarioUIState.toEntity() = UsuarioEntity(
    userId = userId,
    userName = userName,
    email = email,
    password = password
)

fun UsuarioUIState.toDto() = UsuarioDto(
    userId = userId,
    userName = userName,
    email = email,
    password = password
)