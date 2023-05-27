package com.ifkusyoba.dicogram.services.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ifkusyoba.dicogram.models.*
import com.ifkusyoba.dicogram.services.ApiService
import com.ifkusyoba.dicogram.services.SharedPreferences
import com.ifkusyoba.dicogram.services.paging.PagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.ifkusyoba.dicogram.models.Result as Results

class StoryRepository(private val preference: SharedPreferences, private val apiService: ApiService ) {
    private val TAG = StoryRepository::class.java.simpleName
    // *Login
    fun login(email: String, password: String): LiveData<Results<Login>> = liveData {
        emit(Results.Loading)
        try{
            val response = apiService.login(email, password)
            emit(Results.Success(response))
        } catch (exception: Exception){
            Log.e(TAG, "Login: ${exception.message.toString()}")
        }
    }
    // *Register
    fun register(name: String, email: String, password: String): LiveData<Results<Register>> = liveData {
        emit(Results.Loading)
        try{
            val response = apiService.register(name, email, password)
            emit(Results.Success(response))
        } catch (exception: Exception){
            Log.e(TAG, "Register: ${exception.message.toString()}")
            emit(Results.Error(exception.message.toString()))
        }
    }
    // *Mengambil data user
    fun getUserData(): LiveData<Users> {
        return preference.getData().asLiveData()
    }
    // *Save data user
    suspend fun saveUserData(user: Users) {
        preference.saveData(user)
    }
    // *Mengambil data story menggunakan paging
    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                PagingSource(apiService, preference)
            }
        ).liveData
    }
    // *Upload story
    fun upload(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?, lng: Double?):
            LiveData<Results<StoryUpload>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.uploadImage(token, file, description, lat, lng)
            emit(Results.Success(response))
        } catch (exception: Exception) {
            Log.e(TAG, "Upload: ${exception.message.toString()}")
            emit(Results.Error(exception.message.toString()))
        }
    }
    // *Logout
    suspend fun clearData() {
        preference.cleardata()
    }
    // *Get Location
    fun getLocation(token: String): LiveData<Results<StoryList>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.getStoryListLocation(token, 100)
            emit(Results.Success(response))
        } catch (exception: Exception) {
            Log.e(TAG, "getLocation: ${exception.message.toString()}")
            emit(Results.Error(exception.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            preferences: SharedPreferences,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(preferences, apiService)
            }.also { instance = it }
    }
}