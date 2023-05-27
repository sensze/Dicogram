package com.ifkusyoba.dicogram.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifkusyoba.dicogram.models.Users
import com.ifkusyoba.dicogram.services.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository): ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
    fun logout() {
        viewModelScope.launch {
            repository.clearData()
        }
    }
    fun saveUser(user: Users) {
        viewModelScope.launch {
            repository.saveUserData(user)
        }
    }
}