package com.usuhackathon.trashtalk.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val uid: String) : AuthResult()
    data class Error(val message: String) : AuthResult()

    /** Login-specific */
    data object AccountDoesNotExist : AuthResult()
    data object InvalidPassword : AuthResult()
}

object AuthService {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun signUp(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw IllegalStateException("Sign up succeeded but uid was null")
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Login succeeded but uid was null")
            AuthResult.Success(uid)
        } catch (e: FirebaseAuthInvalidUserException) {
            // Typically: ERROR_USER_NOT_FOUND
            AuthResult.AccountDoesNotExist
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Typically: wrong password (or malformed email; you can differentiate if you want)
            AuthResult.InvalidPassword
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }
}