package com.usuhackathon.trashtalk.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usuhackathon.trashtalk.data.AuthService
import com.usuhackathon.trashtalk.data.FirestoreService
import com.usuhackathon.trashtalk.data.UserProfile
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadData()
    }

    fun loadData() {
        val uid = AuthService.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                userProfile = FirestoreService.getUserProfile(uid)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }
}
