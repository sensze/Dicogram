package com.ifkusyoba.dicogram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ifkusyoba.dicogram.models.Users
import com.ifkusyoba.dicogram.services.repository.StoryRepository
import com.ifkusyoba.dicogram.models.Story

class StoryViewModel(private val repository: StoryRepository): ViewModel() {
    fun getUser(): LiveData<Users> {
        return repository.getUserData()
    }
    // List -> PagingData
    fun getStory(): LiveData<PagingData<Story>> {
        return repository.getStory().cachedIn(viewModelScope)
    }

}