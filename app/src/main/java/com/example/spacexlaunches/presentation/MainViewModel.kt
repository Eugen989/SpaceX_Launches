package com.example.spacexlaunches.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexlaunches.data.MainDatabase
import com.example.spacexlaunches.data.models.LaunchEntity
import com.example.spacexlaunches.data.repository.LaunchRepository
import com.example.spacexlaunches.data.repository.PaginationRepository
import com.example.spacexlaunches.data.repository.Repository
import com.example.spacexlaunches.utils.Constants
import com.example.spacexlaunches.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(database: MainDatabase) : ViewModel() {

    enum class FilterType {
        ALL, UPCOMING, PAST
    }

    private val repository = Repository(database)
    private val databaseRef = database

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(com.example.spacexlaunches.data.ApiService::class.java)

    private val launchRepository = LaunchRepository(database, apiService)
    private val paginationRepository = PaginationRepository(database, apiService)

    private val _currentLaunches = MutableStateFlow<List<LaunchEntity>>(emptyList())
    val currentLaunches: StateFlow<List<LaunchEntity>> = _currentLaunches.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _pageInfo = MutableStateFlow("")
    val pageInfo: StateFlow<String> = _pageInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _dataSource = MutableStateFlow<DataSource>(DataSource.UNKNOWN)
    val dataSource: StateFlow<DataSource> = _dataSource.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentDataSourceLog = MutableStateFlow("Not initialized")
    val currentDataSourceLog: StateFlow<String> = _currentDataSourceLog.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _filteredLaunches = MutableStateFlow<List<LaunchEntity>>(emptyList())
    val filteredLaunches: StateFlow<List<LaunchEntity>> = _filteredLaunches.asStateFlow()

    private val _currentFilter = MutableStateFlow(FilterType.ALL)
    val currentFilter: StateFlow<FilterType> = _currentFilter.asStateFlow()

    private val _hasData = MutableStateFlow(false)
    val hasData: StateFlow<Boolean> = _hasData.asStateFlow()

    private var allLaunches: List<LaunchEntity> = emptyList()
    private var originalLaunches: List<LaunchEntity> = emptyList()

    init {
        Log.d("DataSourceLog", "ViewModel initialized")
        _currentDataSourceLog.value = "ViewModel initialized"

        viewModelScope.launch {
            val count = database.getDao().getLaunchesCount()
            _hasData.value = count > 0
            Log.d("LoadingDebug", "Initial data check: $count items")
        }
    }

    fun loadData(context: Context) {
        _isLoading.value = true
        _errorMessage.value = null
        _hasData.value = false

        viewModelScope.launch {
            try {
                if (NetworkUtils.isInternetAvailable(context)) {
                    Log.d("DataSourceLog", "Loading all data from API")

                    _dataSource.value = DataSource.API
                    _currentDataSourceLog.value = "API (loading...)"

                    val success = paginationRepository.loadAllLaunches()

                    if (success) {
                        Log.d("DataSourceLog", "All API data loaded successfully")
                        _currentDataSourceLog.value = "API (loaded)"
                        _hasData.value = true

                        setupPagination()
                    } else {
                        Log.d("DataSourceLog", "API data loading failed")
                        _currentDataSourceLog.value = "API (failed) → Database"
                        _errorMessage.value = "Failed to load data from API"

                        checkDatabaseForData()
                    }
                } else {
                    Log.d("DataSourceLog", "Using cached data from database")
                    _dataSource.value = DataSource.DATABASE
                    _currentDataSourceLog.value = "DATABASE (offline)"

                    val success = paginationRepository.loadAllLaunches()
                    if (success) {
                        _hasData.value = true
                        setupPagination()
                    } else {
                        Log.d("DataSourceLog", "No data available")
                        _currentDataSourceLog.value = "NO DATA"
                        _errorMessage.value = "No internet connection and no cached data available"
                        _hasData.value = false
                    }
                }

            } catch (e: Exception) {
                Log.e("DataSourceLog", "Error loading data: ${e.message}")

                _currentDataSourceLog.value = "ERROR"
                _errorMessage.value = "Error loading data: ${e.message}"

                checkDatabaseForData()
            }
        }
    }

    private suspend fun checkDatabaseForData() {
        val count = databaseRef.getDao().getLaunchesCount()
        _hasData.value = count > 0
        if (count > 0) {
            setupPagination()
        } else {
            _isLoading.value = false
        }
        Log.d("LoadingDebug", "Database check: $count items, hasData: ${_hasData.value}")
    }

    private fun setupPagination() {
        allLaunches = paginationRepository.getAllLaunches()
        _totalPages.value = paginationRepository.getTotalPages()
        _currentPage.value = 1

        updateCurrentPage()
        _isLoading.value = false

        Log.d("Pagination", "Pagination setup: total pages = ${_totalPages.value}")
    }

    fun setFilter(filter: FilterType) {
        _currentFilter.value = filter
        applyCurrentFilter()
        Log.d("Filter", "Filter changed to: $filter")
    }

    private fun applyCurrentFilter() {
        val filteredLaunches = when (_currentFilter.value) {
            FilterType.ALL -> allLaunches
            FilterType.UPCOMING -> allLaunches.filter { it.upcoming }
            FilterType.PAST -> allLaunches.filter { !it.upcoming }
        }

        updatePaginationForFilteredData(filteredLaunches)

        if (_isSearching.value) {
            applySearchFilter(_searchQuery.value)
        }
    }

    private fun updatePaginationForFilteredData(filteredLaunches: List<LaunchEntity>) {
        val pageSize = 10
        val totalPages = if (filteredLaunches.isEmpty()) 1 else (filteredLaunches.size + pageSize - 1) / pageSize

        _totalPages.value = totalPages
        _currentPage.value = 1

        val currentPageData = if (filteredLaunches.isEmpty()) {
            emptyList()
        } else {
            val start = 0
            val end = pageSize.coerceAtMost(filteredLaunches.size)
            filteredLaunches.subList(start, end)
        }

        _currentLaunches.value = currentPageData
        originalLaunches = currentPageData

        _pageInfo.value = if (filteredLaunches.isEmpty()) {
            "No launches"
        } else {
            "Showing 1-${currentPageData.size} of ${filteredLaunches.size} launches"
        }

        if (_isSearching.value) {
            applySearchFilter(_searchQuery.value)
        } else {
            _filteredLaunches.value = currentPageData
        }

        updateButtonStates()
    }

    private fun updateCurrentPage() {
        val currentPage = _currentPage.value
        val pageData = paginationRepository.getPage(currentPage)

        allLaunches = paginationRepository.getAllLaunches()
        originalLaunches = pageData
        _currentLaunches.value = pageData

        if (_currentFilter.value != FilterType.ALL) {
            applyCurrentFilter()
        } else {
            _pageInfo.value = paginationRepository.getCurrentPageInfo(currentPage)
            if (_isSearching.value) {
                applySearchFilter(_searchQuery.value)
            } else {
                _filteredLaunches.value = pageData
            }
        }

        Log.d("Pagination", "Page $currentPage/${_totalPages.value}: ${pageData.size} items")
        updateButtonStates()
    }

    fun searchLaunches(query: String) {
        _searchQuery.value = query
        _isSearching.value = query.isNotBlank()

        if (query.isBlank()) {
            _filteredLaunches.value = _currentLaunches.value
            Log.d("Search", "Search cleared, showing ${_currentLaunches.value.size} items")
        } else {
            applySearchFilter(query)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
        _filteredLaunches.value = _currentLaunches.value

        Log.d("Search", "Search cleared")
    }

    private fun applySearchFilter(query: String) {
        val filtered = _currentLaunches.value.filter { launch ->
            listOf(
                launch.name,
                launch.details,
                launch.rocketType,
                launch.rocketName,
                launch.rocketCompany
            ).any { field ->
                field?.contains(query, ignoreCase = true) == true
            }
        }
        _filteredLaunches.value = filtered
    }

    fun nextPage() {
        if (_currentFilter.value != FilterType.ALL) {
            val currentPage = _currentPage.value
            val totalPages = _totalPages.value

            if (currentPage < totalPages) {
                _currentPage.value = currentPage + 1
                updateFilteredPage()
            }
        } else {
            val currentPage = _currentPage.value
            val totalPages = _totalPages.value

            if (currentPage < totalPages) {
                _currentPage.value = currentPage + 1
                updateCurrentPage()
                Log.d("Pagination", "Next page: $currentPage -> ${_currentPage.value}")
            }
        }
    }

    fun previousPage() {
        if (_currentFilter.value != FilterType.ALL) {
            val currentPage = _currentPage.value
            if (currentPage > 1) {
                _currentPage.value = currentPage - 1
                updateFilteredPage()
            }
        } else {
            val currentPage = _currentPage.value
            if (currentPage > 1) {
                _currentPage.value = currentPage - 1
                updateCurrentPage()
                Log.d("Pagination", "Previous page: $currentPage -> ${_currentPage.value}")
            }
        }
    }

    private fun updateFilteredPage() {
        val currentPage = _currentPage.value
        val pageSize = 10
        val filteredLaunches = when (_currentFilter.value) {
            FilterType.ALL -> allLaunches
            FilterType.UPCOMING -> allLaunches.filter { it.upcoming }
            FilterType.PAST -> allLaunches.filter { !it.upcoming }
        }

        val start = (currentPage - 1) * pageSize
        if (start >= filteredLaunches.size) {
            _currentLaunches.value = emptyList()
        } else {
            val end = (start + pageSize).coerceAtMost(filteredLaunches.size)
            _currentLaunches.value = filteredLaunches.subList(start, end)
        }

        val totalItems = filteredLaunches.size
        val startItem = start + 1
        val endItem = (currentPage * pageSize).coerceAtMost(totalItems)
        _pageInfo.value = "Showing $startItem-$endItem of $totalItems launches"

        originalLaunches = _currentLaunches.value

        if (_isSearching.value) {
            applySearchFilter(_searchQuery.value)
        } else {
            _filteredLaunches.value = _currentLaunches.value
        }

        updateButtonStates()
    }

    private fun updateButtonStates() {
        val currentPage = _currentPage.value
        val totalPages = _totalPages.value

        Log.d("Pagination", "Button states - Current: $currentPage, Total: $totalPages, " +
                "Previous enabled: ${currentPage > 1}, Next enabled: ${currentPage < totalPages}")
    }

    fun goToPage(page: Int) {
        if (page in 1.._totalPages.value) {
            _currentPage.value = page
            if (_currentFilter.value != FilterType.ALL) {
                updateFilteredPage()
            } else {
                updateCurrentPage()
            }
        }
    }

    fun clearDatabase() {
        Log.d("DataSourceLog", "Clearing database...")
        _currentDataSourceLog.value = "Clearing database..."

        viewModelScope.launch {
            repository.clearAllLaunches()
            _dataSource.value = DataSource.DATABASE
            _currentDataSourceLog.value = "DATABASE (cleared)"
            _errorMessage.value = "Database cleared"

            _currentLaunches.value = emptyList()
            _currentPage.value = 1
            _totalPages.value = 1
            _pageInfo.value = ""
            _hasData.value = false

            clearSearch()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshData(context: Context, onComplete: (() -> Unit)? = null) {
        Log.d("DataSourceLog", "Refreshing data...")
        _currentDataSourceLog.value = "Refreshing..."
        _isLoading.value = true

        viewModelScope.launch {
            try {
                if (NetworkUtils.isInternetAvailable(context)) {
                    Log.d("DataSourceLog", "Refreshing all data from API")

                    _dataSource.value = DataSource.API
                    _currentDataSourceLog.value = "API (refreshing...)"

                    repository.clearAllLaunches()

                    val success = paginationRepository.loadAllLaunches()

                    if (success) {
                        Log.d("DataSourceLog", "Refresh successful")

                        _currentDataSourceLog.value = "API (refreshed)"
                        _hasData.value = true

                        setupPagination()
                        onComplete?.invoke()
                    } else {
                        Log.d("DataSourceLog", "Refresh failed")

                        _currentDataSourceLog.value = "API (refresh failed)"
                        _errorMessage.value = "Failed to refresh data from API"
                        checkDatabaseForData()
                        onComplete?.invoke()
                    }
                } else {
                    Log.d("DataSourceLog", "No internet - using cached data")

                    _dataSource.value = DataSource.DATABASE
                    _currentDataSourceLog.value = "DATABASE (offline refresh)"
                    _errorMessage.value = "No internet connection - showing cached data"

                    val success = paginationRepository.loadAllLaunches()
                    if (success) {
                        _hasData.value = true
                        setupPagination()
                        onComplete?.invoke()
                    } else {
                        checkDatabaseForData()
                        onComplete?.invoke()
                    }
                }

            } catch (e: Exception) {
                Log.e("DataSourceLog", "Error refreshing data: ${e.message}")

                _currentDataSourceLog.value = "REFRESH ERROR"
                _errorMessage.value = "Error refreshing data: ${e.message}"
                checkDatabaseForData()
                onComplete?.invoke()
            }
        }
    }

    fun getCurrentDataSourceInfo(): String {
        return when (_dataSource.value) {
            DataSource.API -> "API (Online - Live data from SpaceX)"
            DataSource.DATABASE -> "Database (Offline - Cached data)"
            DataSource.UNKNOWN -> "Unknown source"
        }
    }
}

enum class DataSource {
    API, DATABASE, UNKNOWN
}