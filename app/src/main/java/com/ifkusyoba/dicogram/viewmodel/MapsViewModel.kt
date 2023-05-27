package com.ifkusyoba.dicogram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ifkusyoba.dicogram.models.Users
import com.ifkusyoba.dicogram.services.repository.StoryRepository

class MapsViewModel(private val repository: StoryRepository): ViewModel() {
    fun getUser(): LiveData<Users> {
        return repository.getUserData()
    }
    fun getLocation(token: String) = repository.getLocation(token)
}