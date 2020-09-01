package com.example.remindmemunni.destinations.newseries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.ItemRepository

class NewSeriesViewModelFactory(
    private val itemRepository: ItemRepository,
    private val seriesId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewSeriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewSeriesViewModel(itemRepository, seriesId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}