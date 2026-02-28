package com.usuhackathon.trashtalk.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usuhackathon.trashtalk.data.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

data class HomeState(
    val userProfile: UserProfile? = null,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val chores: List<Chore> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    var state by mutableStateOf(HomeState())
        private set

    init {
        loadData()
    }

    fun loadData() {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val profile = FirestoreService.getUserProfile(uid)
                state = state.copy(userProfile = profile)
                
                if (profile.leagueID.isNotEmpty()) {
                    val leaderboardResp = RetrofitClient.instance.getLeaderboard(profile.leagueID, uid)
                    val choresResp = RetrofitClient.instance.listChores(profile.leagueID, uid)
                    state = state.copy(
                        leaderboard = leaderboardResp.leaderboard,
                        chores = choresResp
                    )
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun joinLeague(leagueId: String) {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.instance.joinLeague(mapOf("user_uid" to uid, "league_id" to leagueId))
                if (resp.success) {
                    val profile = state.userProfile?.copy(leagueID = leagueId)
                    if (profile != null) {
                        FirestoreService.upsertUserProfile(uid, profile)
                    }
                    loadData()
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            }
        }
    }

    fun createLeague(name: String, description: String) {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.instance.createLeague(mapOf(
                    "user_uid" to uid,
                    "name" to name,
                    "description" to description
                ))
                if (resp.success && resp.league_id != null) {
                    val profile = state.userProfile?.copy(leagueID = resp.league_id)
                    if (profile != null) {
                        FirestoreService.upsertUserProfile(uid, profile)
                    }
                    loadData()
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            }
        }
    }

    fun completeChore(chore: Chore, comments: String, imageUri: Uri?, context: Context) {
        val uid = AuthService.currentUser?.uid ?: return
        val leagueId = state.userProfile?.leagueID ?: return
        
        viewModelScope.launch {
            try {
                val userUidBody = uid.toRequestBody("text/plain".toMediaTypeOrNull())
                val leagueIdBody = leagueId.toRequestBody("text/plain".toMediaTypeOrNull())
                val choreIdBody = chore.id.toRequestBody("text/plain".toMediaTypeOrNull())
                val commentsBody = comments.toRequestBody("text/plain".toMediaTypeOrNull())
                
                var imagePart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("proof_image", file.name, requestFile)
                    }
                }

                val resp = RetrofitClient.instance.completeChore(
                    userUidBody,
                    leagueIdBody,
                    choreIdBody,
                    commentsBody,
                    imagePart
                )
                
                if (resp.success) {
                    loadData()
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}
