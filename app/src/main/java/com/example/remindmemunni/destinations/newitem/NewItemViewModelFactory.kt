package com.example.remindmemunni.destinations.newitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository

class NewItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val templateItem: Item,
    private val isItemEdit: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewItemViewModel(itemRepository, templateItem, isItemEdit) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}