package com.ucne.cinetix.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.ucne.cinetix.presentation.auth.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository  @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun checkAuthStatus(): Flow<AuthState> = flow {
        emit(AuthState.Loading)
        val currentUser = auth.currentUser
        if (currentUser == null) {
            emit(AuthState.Unauthenticated)
        } else {
            emit(AuthState.Authenticated(currentUser.email ?: ""))
        }
    }

    fun login(email: String, password: String): Flow<AuthState> = flow {
        if (email.isEmpty() || password.isEmpty()) {
            emit(AuthState.Error("Email or password can't be empty"))
            return@flow
        }
        emit(AuthState.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                emit(AuthState.Authenticated(email))
            } else {
                emit(AuthState.Error("Authentication failed"))
            }
        } catch (e: Exception) {
            emit(AuthState.Error(e.message ?: "Something went wrong"))
        }
    }

    fun signUp(email: String, password: String): Flow<AuthState> = flow {
        if (email.isEmpty() || password.isEmpty()) {
            emit(AuthState.Error("Email or password can't be empty"))
            return@flow
        }
        emit(AuthState.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                emit(AuthState.Authenticated(email))
            } else {
                emit(AuthState.Error("Registration failed"))
            }
        } catch (e: Exception) {
            emit(AuthState.Error(e.message ?: "Something went wrong"))
        }
    }

    fun signOut(): Flow<AuthState> = flow {
        auth.signOut()
        emit(AuthState.Unauthenticated)
    }
}