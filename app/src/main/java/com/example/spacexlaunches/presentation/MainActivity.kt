package com.example.spacexlaunches.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacexlaunches.data.ApiService
import com.example.spacexlaunches.data.MainDatabase
import com.example.spacexlaunches.databinding.ActivityMainBinding
import com.example.spacexlaunches.domain.usecase.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spacexdata.com/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        MainViewModelFactory(MainDatabase.getDb(this), apiService)
    }
    private lateinit var adapter: LaunchAdapter

    private lateinit var activityUseCase: ActivityUseCase
    private lateinit var dataRefreshUseCase: DataRefreshUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityUseCase = ActivityUseCase()
        dataRefreshUseCase = DataRefreshUseCase()

        setupRecyclerView()
        setupUseCases()
        setupObservers()
        setupClickListeners()

        if (!viewModel.isDataLoaded()) {
            viewModel.loadData(this)
        }

        Log.d("DataSourceLog", "Activity created - loading initial data")
    }

    private fun setupRecyclerView() {
        adapter = LaunchAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupUseCases() {
        activityUseCase.setupSearchLogic(
            binding.searchEditText,
            binding.clearSearchButton,
            object : ActivityUseCase.SearchCallback {
                override fun onSearch(query: String) {
                    viewModel.searchLaunches(query)
                }

                override fun onClearSearch() {
                    viewModel.currentLaunches.value?.let { launches ->
                        if (launches.isNotEmpty()) {
                            activityUseCase.setHasData(true)
                        }
                    }
                }
            }
        )

        setupFilterButtons()
    }

    private fun setupFilterButtons() {
        binding.filterLayout.visibility = View.VISIBLE

        binding.allFilterButton.setOnClickListener {
            viewModel.setFilter(FilterLaunchesUseCase.FilterType.ALL)
            updateFilterButtons(FilterLaunchesUseCase.FilterType.ALL)
        }

        binding.upcomingFilterButton.setOnClickListener {
            viewModel.setFilter(FilterLaunchesUseCase.FilterType.UPCOMING)
            updateFilterButtons(FilterLaunchesUseCase.FilterType.UPCOMING)
        }

        binding.pastFilterButton.setOnClickListener {
            viewModel.setFilter(FilterLaunchesUseCase.FilterType.PAST)
            updateFilterButtons(FilterLaunchesUseCase.FilterType.PAST)
        }

        updateFilterButtons(FilterLaunchesUseCase.FilterType.ALL)
    }

    private fun updateFilterButtons(selectedFilter: FilterLaunchesUseCase.FilterType) {
        activityUseCase.updateFilterButtons(
            selectedFilter,
            binding.allFilterButton,
            binding.upcomingFilterButton,
            binding.pastFilterButton,
            this
        )
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.currentLaunches.collect { launches ->
                Log.d("Pagination", "Updating adapter with ${launches.size} launches")
                adapter.submitList(launches)
                updateUI(launches)

                activityUseCase.setHasData(launches.isNotEmpty() || viewModel.isDataLoaded())
            }
        }

        lifecycleScope.launch {
            viewModel.currentPage.collect { currentPage ->
                val totalPages = viewModel.totalPages.value
                updatePageNavigation(currentPage, totalPages)
            }
        }

        lifecycleScope.launch {
            viewModel.totalPages.collect { totalPages ->
                val currentPage = viewModel.currentPage.value
                updatePageNavigation(currentPage, totalPages)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                updateLoadingState(isLoading)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let { message ->
                    showSnackbar(message)
                    Log.e("DataSourceLog", "Error: $message")

                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    private fun updateUI(launches: List<com.example.spacexlaunches.data.models.LaunchEntity>) {
        val isLoading = viewModel.isLoading.value
        val currentFilter = viewModel.currentFilter.value

        activityUseCase.updateSearchUI(
            launches,
            isLoading,
            currentFilter,
            binding.emptyStateText,
            binding.searchInfoText
        )

        val isSearching = activityUseCase.isSearching.value ?: false
        if (isSearching) {
            binding.searchInfoText.visibility = View.VISIBLE
            binding.pageNavigationLayout.visibility = View.GONE
            binding.pageInfoText.visibility = View.GONE
            binding.filterLayout.visibility = View.GONE
        } else {
            binding.searchInfoText.visibility = View.GONE
            binding.pageNavigationLayout.visibility = View.VISIBLE
            binding.pageInfoText.visibility = View.VISIBLE
            binding.filterLayout.visibility = View.VISIBLE
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading && !viewModel.isDataLoaded()) View.VISIBLE else View.GONE

        binding.refreshButton.isEnabled = !isLoading
        binding.clearButton.isEnabled = !isLoading

        val hasData = activityUseCase.hasData.value ?: false
        val isSearching = activityUseCase.isSearching.value ?: false

        if (isLoading && !viewModel.isDataLoaded()) {
            binding.pageNavigationLayout.visibility = View.GONE
            binding.pageInfoText.visibility = View.GONE
            binding.emptyStateText.visibility = View.GONE
            binding.filterLayout.visibility = View.GONE
        } else {
            binding.pageNavigationLayout.visibility = if (hasData && !isSearching) View.VISIBLE else View.GONE
            binding.pageInfoText.visibility = if (hasData && !isSearching) View.VISIBLE else View.GONE
            binding.filterLayout.visibility = if (hasData && !isSearching) View.VISIBLE else View.GONE
        }

        if (!isSearching) {
            val currentPage = viewModel.currentPage.value
            val totalPages = viewModel.totalPages.value
            activityUseCase.updateNavigationButtons(
                currentPage,
                totalPages,
                isLoading,
                binding.previousButton,
                binding.nextButton
            )
        } else {
            binding.previousButton.isEnabled = false
            binding.nextButton.isEnabled = false
        }
    }

    private fun updatePageNavigation(currentPage: Int, totalPages: Int) {
        binding.pageNavigationText.text = "Page $currentPage of $totalPages"

        if (!(activityUseCase.isSearching.value ?: false)) {
            activityUseCase.updateNavigationButtons(
                currentPage,
                totalPages,
                viewModel.isLoading.value,
                binding.previousButton,
                binding.nextButton
            )
        }
    }

    private fun setupClickListeners() {
        binding.refreshButton.setOnClickListener {
            Log.d("DataSourceLog", "Refresh button clicked")
            viewModel.refreshData(this)
            dataRefreshUseCase.updateRefreshTime()
        }

        binding.clearButton.setOnClickListener {
            Log.d("DataSourceLog", "Clear button clicked")
            viewModel.clearDatabase()
            activityUseCase.setHasData(false)
        }

        binding.previousButton.setOnClickListener {
            Log.d("Pagination", "Previous button clicked")
            viewModel.previousPage()
        }

        binding.nextButton.setOnClickListener {
            Log.d("Pagination", "Next button clicked")
            viewModel.nextPage()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        Log.d("DataSourceLog", "Activity resumed")

        val hasData = activityUseCase.hasData.value ?: false
        if (dataRefreshUseCase.shouldRefreshData(hasData)) {
            Log.d("DataSourceLog", "Data is stale, refreshing...")
            viewModel.refreshData(this)
            dataRefreshUseCase.updateRefreshTime()
        } else {
            Log.d("DataSourceLog", "Data is fresh, no refresh needed")
        }
    }
}