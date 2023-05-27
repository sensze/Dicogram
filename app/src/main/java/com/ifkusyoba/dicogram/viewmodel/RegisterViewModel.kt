package com.ifkusyoba.dicogram.viewmodel

import androidx.lifecycle.ViewModel
import com.ifkusyoba.dicogram.services.repository.StoryRepository

class RegisterViewModel(private val repository: StoryRepository): ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}