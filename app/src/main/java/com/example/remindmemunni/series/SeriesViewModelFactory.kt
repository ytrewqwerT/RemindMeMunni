package com.example.remindmemunni.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.ItemRepository

class SeriesViewModelFactory(
    private val itemRepository: ItemRepository,
    private val seriesId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeriesViewModel(itemRepository, seriesId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}