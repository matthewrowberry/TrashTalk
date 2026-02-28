package com.usuhackathon.trashtalk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usuhackathon.trashtalk.data.*
import kotlinx.coroutines.launch

data class TimelineState(
    val completions: List<UserCompletion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val leagueId: String = ""
)

class TimelineViewModel : ViewModel() {
    var state by mutableStateOf(TimelineState())
        private set

    fun loadTimeline(targetUid: String) {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val profile = FirestoreService.getUserProfile(uid)
                state = state.copy(leagueId = profile.leagueID)
                if (profile.leagueID.isNotEmpty()) {
                    val resp = RetrofitClient.instance.getUserCompletions(
                        leagueId = profile.leagueID,
                        targetUid = targetUid,
                        requesterUid = uid
                    )
                    state = state.copy(completions = resp.completions)
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }
}
