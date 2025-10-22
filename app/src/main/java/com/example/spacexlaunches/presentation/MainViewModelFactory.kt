package com.example.spacexlaunches.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spacexlaunches.data.MainDatabase

class MainViewModelFactory(private val database: MainDatabase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(database) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}