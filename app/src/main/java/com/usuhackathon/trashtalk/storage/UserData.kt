package com.usuhackathon.trashtalk.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_data")

class UserData(private val context: Context) {

    companion object {


        val USERNAME_KEY = stringPreferencesKey("username")
        val SESSION_ID_KEY = stringPreferencesKey("session_id")
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map {
            preferences -> preferences[USERNAME_KEY]
    }

    suspend fun saveSession(username: String, sessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
            preferences[SESSION_ID_KEY] = sessionId
        }
    }

}



