package com.example.spacexlaunches.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexlaunches.data.models.LaunchEntity
import com.example.spacexlaunches.domain.usecase.FilterLaunchesUseCase
import com.example.spacexlaunches.domain.usecase.GetLaunchesUseCase
import com.example.spacexlaunches.domain.usecase.RefreshDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val getLaunchesUseCase: GetLaunchesUseCase,
    private val filterLaunchesUseCase: FilterLaunchesUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : ViewModel() {

    private val _currentLaunches = MutableStateFlow<List<LaunchEntity>>(emptyList())
    val currentLaunches: StateFlow<List<LaunchEntity>> = _currentLaunches

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages

    private val _pageInfo = MutableStateFlow("")
    val pageInfo: StateFlow<String> = _pageInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentFilter = MutableStateFlow(FilterLaunchesUseCase.FilterType.ALL)
    val currentFilter: StateFlow<FilterLaunchesUseCase.FilterType> = _currentFilter

    private var allLaunches: List<LaunchEntity> = emptyList()

    fun loadData(context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            getLaunchesUseCase.loadData(context, object : GetLaunchesUseCase.LoadDataCallback {
                override fun onSuccess(launches: List<LaunchEntity>) {
                    allLaunches = launches
                    setupPagination()
                    _isLoading.value = false
                }

                override fun onError(message: String) {
                    _errorMessage.value = message
                    _isLoading.value = false
                }

                override fun onLoading(isLoading: Boolean) {
                    _isLoading.value = isLoading
                }
            })
        }
    }

    private fun setupPagination() {
        _totalPages.value = filterLaunchesUseCase.calculateTotalPages(allLaunches, 10)
        _currentPage.value = 1
        updateCurrentPage()
    }

    private fun updateCurrentPage() {
        val filteredLaunches = filterLaunchesUseCase.filterLaunches(allLaunches, _currentFilter.value)
        val pageData = filterLaunchesUseCase.getPaginatedData(filteredLaunches, _currentPage.value, 10)

        _currentLaunches.value = pageData
        _pageInfo.value = filterLaunchesUseCase.generatePageInfo(
            filteredLaunches,
            pageData,
            _currentPage.value,
            10
        )
    }

    fun setFilter(filter: FilterLaunchesUseCase.FilterType) {
        _currentFilter.value = filter
        _currentPage.value = 1
        updateCurrentPage()
    }

    fun searchLaunches(query: String) {
        val filtered = filterLaunchesUseCase.searchLaunches(_currentLaunches.value, query)
        _currentLaunches.value = filtered
    }

    fun nextPage() {
        if (_currentPage.value < _totalPages.value) {
            _currentPage.value = _currentPage.value + 1
            updateCurrentPage()
        }
    }

    fun previousPage() {
        if (_currentPage.value > 1) {
            _currentPage.value = _currentPage.value - 1
            updateCurrentPage()
        }
    }

    fun refreshData(context: Context) {
        refreshDataUseCase.refreshData(context, object : RefreshDataUseCase.RefreshCallback {
            override fun onSuccess() {
                loadData(context)
            }

            override fun onError(message: String) {
                _errorMessage.value = message
            }

            override fun onLoading(isLoading: Boolean) {
                _isLoading.value = isLoading
            }
        })
    }

    fun clearDatabase() {
        refreshDataUseCase.clearDatabase(Runnable {
            _currentLaunches.value = emptyList()
            _currentPage.value = 1
            _totalPages.value = 1
            _pageInfo.value = ""
            allLaunches = emptyList()
        })
    }

    fun clearError() {
        _errorMessage.value = null
    }
}