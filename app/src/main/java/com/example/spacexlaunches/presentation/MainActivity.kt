package com.example.spacexlaunches.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacexlaunches.data.Api
import com.example.spacexlaunches.data.databases.MainDatabase
import com.example.spacexlaunches.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(MainDatabase.getDb(this))
    }
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = Api()
        api.getData()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.items.observe(this) { items ->
            adapter.submitList(items)
            binding.itemCountText.text = "Items in database: ${items.size}"
        }
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val priceText = binding.priceEditText.text.toString().trim()

            if (name.isNotEmpty() && priceText.isNotEmpty()) {
                val price = priceText.toIntOrNull() ?: 0
                viewModel.addNewItem(name, price)
                binding.nameEditText.text.clear()
                binding.priceEditText.text.clear()
            }
        }

        binding.clearButton.setOnClickListener {
            lifecycleScope.launch {
                // Здесь нужно добавить функциональность очистки базы данных
            }
        }
    }
}

// Factory для ViewModel
class MainViewModelFactory(private val database: MainDatabase) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}