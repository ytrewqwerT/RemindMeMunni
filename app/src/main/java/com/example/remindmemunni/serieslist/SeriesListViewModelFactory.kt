package com.example.remindmemunni.serieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.ItemRepository

class SeriesListViewModelFactory(
    private val itemRepository: ItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeriesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeriesListViewModel(itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}