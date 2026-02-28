package com.usuhackathon.trashtalk.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthService {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun signUp(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw IllegalStateException("Sign up succeeded but uid was null")
    }
}