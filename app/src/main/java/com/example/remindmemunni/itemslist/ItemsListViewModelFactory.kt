package com.example.remindmemunni.itemslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.ItemRepository

class ItemsListViewModelFactory(
    private val itemRepository: ItemRepository,
    private val seriesId: Int = 0
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsListViewModel(itemRepository, seriesId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}