package com.usuhackathon.trashtalk.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun upsertUserProfile(uid: String, profile: UserProfile) {
        val data = hashMapOf(
            "displayName" to profile.displayName,
            "email" to profile.email,
            "leagueID" to profile.leagueID,
            "points" to profile.points
        )
        db.collection("users").document(uid).set(data).await()
    }

    suspend fun getUserProfile(uid: String): UserProfile {
        val snap = db.collection("users").document(uid).get().await()
        if (!snap.exists()) throw IllegalStateException("No Firestore user doc found at users/$uid")

        return UserProfile(
            displayName = snap.getString("displayName").orEmpty(),
            email = snap.getString("email").orEmpty(),
            leagueID = snap.getString("leagueID").orEmpty(),
            points = snap.getLong("points") ?: 0L
        )
    }
}