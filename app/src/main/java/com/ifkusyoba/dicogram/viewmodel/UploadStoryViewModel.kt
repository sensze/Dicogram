package com.ifkusyoba.dicogram.viewmodel

import androidx.lifecycle.ViewModel
import com.ifkusyoba.dicogram.services.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getUser() = storyRepository.getUserData()
    fun upload(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?, lng: Double?) =
        storyRepository.upload(token, file, description, lat, lng)
}