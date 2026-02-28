package com.usuhackathon.trashtalk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usuhackathon.trashtalk.data.*
import kotlinx.coroutines.launch

data class SettingsState(
    val chores: List<Chore> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val leagueID: String = ""
)

class SettingsViewModel : ViewModel() {
    var state by mutableStateOf(SettingsState())
        private set

    init {
        loadChores()
    }

    fun loadChores() {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val profile = FirestoreService.getUserProfile(uid)
                state = state.copy(leagueID = profile.leagueID)
                if (profile.leagueID.isNotEmpty()) {
                    val chores = RetrofitClient.instance.listChores(profile.leagueID, uid)
                    state = state.copy(chores = chores)
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun addChore(name: String, description: String, points: Int) {
        val uid = AuthService.currentUser?.uid ?: return
        val leagueId = state.leagueID
        if (leagueId.isEmpty()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val resp = RetrofitClient.instance.createChore(
                    CreateChoreRequest(
                        user_uid = uid,
                        league_id = leagueId,
                        name = name,
                        description = description,
                        points = points
                    )
                )
                if (resp.success) {
                    loadChores()
                } else {
                    state = state.copy(error = resp.error ?: "Failed to add chore", isLoading = false)
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateChore(choreId: String, name: String?, description: String?, points: Int?) {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val req = EditChoreRequest(
                    user_uid = uid,
                    chore_id = choreId,
                    name = name,
                    description = description,
                    points = points
                )

                val resp = RetrofitClient.instance.editChore(req)
                if (resp.success) {
                    loadChores()
                } else {
                    state = state.copy(error = resp.error ?: "Failed to update chore", isLoading = false)
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteChore(choreId: String) {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val resp = RetrofitClient.instance.deleteChore(mapOf(
                    "user_uid" to uid,
                    "chore_id" to choreId
                ))
                if (resp.success) {
                    loadChores()
                } else {
                    state = state.copy(error = resp.error ?: "Failed to delete chore", isLoading = false)
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}
