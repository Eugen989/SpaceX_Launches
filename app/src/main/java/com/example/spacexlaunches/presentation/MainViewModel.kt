package com.example.spacexlaunches.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spacexlaunches.data.databases.Item
import com.example.spacexlaunches.data.databases.MainDatabase
import com.example.spacexlaunches.data.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(database: MainDatabase) : ViewModel() {

    private val repository = Repository(database)

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    init {
        loadItems()
        viewModelScope.launch {
            // Вставляем тестовые данные при первом запуске
            if (_items.value.isNullOrEmpty()) {
                repository.insertSampleData()
                loadItems()
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            repository.getAllItems().collect { itemsList ->
                _items.value = itemsList
            }
        }
    }

    fun addNewItem(name: String, price: Int) {
        viewModelScope.launch {
            repository.insertItem(Item(name = name, price = price))
            loadItems()
        }
    }
}