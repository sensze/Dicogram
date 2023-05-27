package com.ifkusyoba.dicogram.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifkusyoba.dicogram.services.repository.StoryRepository
import com.ifkusyoba.dicogram.utility.inject
import com.ifkusyoba.dicogram.viewmodel.ViewModelProvider as ViewModelProviders

@Suppress("UNCHECKED_CAST")
class ViewModelProvider(private val repository: StoryRepository): ViewModelProvider.NewInstanceFactory() {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(repository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> return RegisterViewModel(repository) as T
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> return StoryViewModel(repository) as T
            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> return UploadStoryViewModel(repository) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> return MapsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
    }
    companion object {
        @Volatile
        private var instance: ViewModelProviders? = null
        fun getInstance(context: Context): ViewModelProviders {
            return instance ?: synchronized(this) {
                instance ?: ViewModelProviders(inject.provideRepository(context))
            }.also { instance = it }
        }
    }
}