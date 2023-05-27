package com.ifkusyoba.dicogram.utility

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ifkusyoba.dicogram.services.ApiConfig
import com.ifkusyoba.dicogram.services.SharedPreferences
import com.ifkusyoba.dicogram.services.repository.StoryRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("dicogram")
object inject {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = SharedPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.startApiService()
        return StoryRepository.getInstance(preferences, apiService)
    }
}