package com.example.spacexlaunches.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacexlaunches.data.MainDatabase
import com.example.spacexlaunches.data.models.LaunchEntity
import com.example.spacexlaunches.databinding.ActivityMainBinding
import com.example.spacexlaunches.presentation.MainViewModel.FilterType
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(MainDatabase.getDb(this))
    }
    private lateinit var adapter: LaunchAdapter

    private var lastRefreshTime: Long = 0
    private val REFRESH_INTERVAL = 5 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearch()
        setupFilterButtons()

        viewModel.loadData(this)

        lastRefreshTime = System.currentTimeMillis()

        Log.d("DataSourceLog", "Activity created - loading initial data")
    }

    private fun setupRecyclerView() {
        adapter = LaunchAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable?.toString() ?: ""
            viewModel.searchLaunches(query)

            binding.clearSearchButton.visibility =
                if (query.isNotBlank()) View.VISIBLE else View.GONE
        }

        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.setText("")
            viewModel.clearSearch()
            binding.clearSearchButton.visibility = View.GONE
        }
    }

    private fun setupFilterButtons() {
        binding.filterLayout.visibility = View.VISIBLE

        binding.allFilterButton.setOnClickListener {
            viewModel.setFilter(FilterType.ALL)
            updateFilterButtons(FilterType.ALL)
        }

        binding.upcomingFilterButton.setOnClickListener {
            viewModel.setFilter(FilterType.UPCOMING)
            updateFilterButtons(FilterType.UPCOMING)
        }

        binding.pastFilterButton.setOnClickListener {
            viewModel.setFilter(FilterType.PAST)
            updateFilterButtons(FilterType.PAST)
        }

        updateFilterButtons(FilterType.ALL)
    }

    private fun updateFilterButtons(selectedFilter: FilterType) {
        binding.allFilterButton.isSelected = selectedFilter == FilterType.ALL
        binding.upcomingFilterButton.isSelected = selectedFilter == FilterType.UPCOMING
        binding.pastFilterButton.isSelected = selectedFilter == FilterType.PAST

        updateButtonAppearance(binding.allFilterButton, selectedFilter == FilterType.ALL)
        updateButtonAppearance(binding.upcomingFilterButton, selectedFilter == FilterType.UPCOMING)
        updateButtonAppearance(binding.pastFilterButton, selectedFilter == FilterType.PAST)
    }

    private fun updateButtonAppearance(button: android.widget.Button, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundColor(getColor(android.R.color.holo_blue_dark))
            button.setTextColor(getColor(android.R.color.white))
        } else {
            button.setBackgroundColor(getColor(android.R.color.darker_gray))
            button.setTextColor(getColor(android.R.color.black))
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.filteredLaunches.collect { launches ->
                Log.d("Search", "Updating adapter with ${launches.size} filtered launches")

                adapter.submitList(launches)
                updateSearchUI(launches)
            }
        }

        lifecycleScope.launch {
            viewModel.currentLaunches.collect { launches ->
                if (!viewModel.isSearching.value) {
                    Log.d("Pagination", "Updating adapter with ${launches.size} launches")
                    adapter.submitList(launches)
                    updateSearchUI(launches)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isSearching.collect { isSearching ->
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
        }

        lifecycleScope.launch {
            viewModel.pageInfo.collect { info ->
                binding.pageInfoText.text = info
                Log.d("Pagination", "Page info: $info")
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
            viewModel.hasData.collect { hasData ->
                Log.d("LoadingDebug", "hasData changed: $hasData")
                if (hasData && viewModel.isLoading.value) {
                    lifecycleScope.launch {
                        delay(300)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.refreshButton.isEnabled = !isLoading
                binding.clearButton.isEnabled = !isLoading

                if (isLoading) {
                    binding.pageNavigationLayout.visibility = View.GONE
                    binding.pageInfoText.visibility = View.GONE
                    binding.emptyStateText.visibility = View.GONE
                    binding.filterLayout.visibility = View.GONE
                } else {
                    val hasData = viewModel.hasData.value
                    binding.pageNavigationLayout.visibility = if (hasData && !viewModel.isSearching.value) View.VISIBLE else View.GONE
                    binding.pageInfoText.visibility = if (hasData && !viewModel.isSearching.value) View.VISIBLE else View.GONE
                    binding.filterLayout.visibility = if (hasData && !viewModel.isSearching.value) View.VISIBLE else View.GONE

                    if (!hasData && !viewModel.isSearching.value) {
                        binding.emptyStateText.text = "No launches available"
                        binding.emptyStateText.visibility = View.VISIBLE
                    } else {
                        binding.emptyStateText.visibility = View.GONE
                    }
                }

                if (!viewModel.isSearching.value) {
                    val currentPage = viewModel.currentPage.value
                    val totalPages = viewModel.totalPages.value
                    updateNavigationButtons(currentPage, totalPages, isLoading)
                } else {
                    binding.previousButton.isEnabled = false
                    binding.nextButton.isEnabled = false
                }

                Log.d("LoadingDebug", "Is loading: $isLoading")
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let { message ->
                    showSnackbar(message)
                    Log.e("DataSourceLog", "Error: $message")

                    lifecycleScope.launch {
                        delay(3000)
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    private fun updateSearchUI(launches: List<LaunchEntity>) {
        if (launches.isEmpty()) {
            if (viewModel.isSearching.value) {
                binding.emptyStateText.text = "No launches found for '${viewModel.searchQuery.value}'"
                binding.emptyStateText.visibility = View.VISIBLE
            } else if (!viewModel.isLoading.value && !viewModel.hasData.value) {
                binding.emptyStateText.text = "No launches available"
                binding.emptyStateText.visibility = View.VISIBLE
            } else if (!viewModel.isLoading.value && viewModel.hasData.value) {
                when (viewModel.currentFilter.value) {
                    FilterType.UPCOMING -> binding.emptyStateText.text = "No upcoming launches"
                    FilterType.PAST -> binding.emptyStateText.text = "No past launches"

                    else -> binding.emptyStateText.text = "No launches available"
                }
                binding.emptyStateText.visibility = View.VISIBLE
            }
        } else {
            binding.emptyStateText.visibility = View.GONE
        }

        if (viewModel.isSearching.value) {
            val query = viewModel.searchQuery.value
            val resultCount = launches.size

            binding.searchInfoText.text = "Search: '$query' found $resultCount result(s)"
        }
    }

    private fun updatePageNavigation(currentPage: Int, totalPages: Int) {
        binding.pageNavigationText.text = "Page $currentPage of $totalPages"

        if (!viewModel.isSearching.value) {
            updateNavigationButtons(currentPage, totalPages, viewModel.isLoading.value)
        }

        Log.d("Pagination", "Updated navigation: Page $currentPage of $totalPages")
    }

    private fun updateNavigationButtons(currentPage: Int, totalPages: Int, isLoading: Boolean) {
        binding.previousButton.isEnabled = !isLoading && currentPage > 1
        binding.nextButton.isEnabled = !isLoading && currentPage < totalPages

        Log.d("Pagination", "Buttons - Previous: ${binding.previousButton.isEnabled}, Next: ${binding.nextButton.isEnabled}")
    }

    private fun setupClickListeners() {
        binding.refreshButton.setOnClickListener {
            Log.d("DataSourceLog", "Refresh button clicked")

            viewModel.refreshData(this) {
                lastRefreshTime = System.currentTimeMillis()

                Log.d("RefreshDebug", "Manual refresh completed, time updated")
            }
        }

        binding.clearButton.setOnClickListener {
            Log.d("DataSourceLog", "Clear button clicked")
            viewModel.clearDatabase()
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

        if (shouldRefreshData()) {
            Log.d("DataSourceLog", "Data is stale, refreshing...")
            viewModel.refreshData(this) {
                lastRefreshTime = System.currentTimeMillis()

                Log.d("RefreshDebug", "Auto refresh completed, time updated")
            }
        } else {
            Log.d("DataSourceLog", "Data is fresh, no refresh needed")
        }
    }

    private fun shouldRefreshData(): Boolean {
        return !viewModel.hasData.value || isDataStale()
    }

    private fun isDataStale(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRefresh = currentTime - lastRefreshTime

        Log.d("RefreshDebug", "Time since last refresh: ${timeSinceLastRefresh / 1000} seconds, limit: ${REFRESH_INTERVAL / 1000} seconds")

        return timeSinceLastRefresh > REFRESH_INTERVAL
    }
}