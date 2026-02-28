package com.usuhackathon.trashtalk.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usuhackathon.trashtalk.storage.UserData
import kotlinx.coroutines.launch


class LoginViewModel(private val userData: UserData) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")


    fun onLoginClicked() {
        viewModelScope.launch {
            userData.saveSession(username, "fake_session_id")
        }
    }

}