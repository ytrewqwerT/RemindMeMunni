package com.example.remindmemunni.destinations.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.ItemRepository

class ItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val itemId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemRepository, itemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}