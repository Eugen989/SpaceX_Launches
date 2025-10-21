package com.example.spacexlaunches.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexlaunches.data.Api
import com.example.spacexlaunches.data.databases.LaunchEntity
import com.example.spacexlaunches.data.databases.MainDatabase
import com.example.spacexlaunches.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(database: MainDatabase) : ViewModel() {

    private val repository = Repository(database)
    private val api = Api(database)

    private val _launches = MutableStateFlow<List<LaunchEntity>>(emptyList())
    val launches: StateFlow<List<LaunchEntity>> = _launches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLaunchesFromDatabase()
        fetchLaunchesFromApi()
    }

    private fun loadLaunchesFromDatabase() {
        viewModelScope.launch {
            repository.getAllLaunches().collect { launchesList ->
                _launches.value = launchesList
            }
        }
    }

    fun fetchLaunchesFromApi() {
        _isLoading.value = true

        api.fetchAndSaveLaunches()

        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            loadLaunchesFromDatabase()
            _isLoading.value = false
        }
    }

    fun refreshData() {
        fetchLaunchesFromApi()
    }

    fun clearDatabase() {
        viewModelScope.launch {
            repository.clearAllLaunches()
            loadLaunchesFromDatabase()
        }
    }
}