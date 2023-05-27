package com.ifkusyoba.dicogram.services

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ifkusyoba.dicogram.models.Users
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import java.util.Date

class SharedPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val name = stringPreferencesKey("name")
    private val userId = stringPreferencesKey("userId")
    private val token = stringPreferencesKey("token")
    private val state = booleanPreferencesKey("state")

    fun getData(): Flow<Users> {
        return dataStore.data.map { preferences ->
            Users(
                preferences[name] ?: "",
                preferences[userId] ?: "",
                preferences[token] ?: "",
                preferences[state] ?: false
            )
        }
    }

    suspend fun saveData(user: Users) {
        dataStore.edit { preferences ->
            preferences[name] = user.name
            preferences[userId] = user.userId
            preferences[token] = user.token
            preferences[state] = user.isLogin
        }
    }

    suspend fun login() {
        dataStore.edit { preferences -> preferences[state] = true }
    }
    suspend fun cleardata() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): SharedPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}