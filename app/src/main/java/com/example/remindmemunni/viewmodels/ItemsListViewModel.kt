package com.example.remindmemunni.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.Item
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ItemsListViewModel(private val itemRepository: ItemRepository, seriesId: Int = 0)
    : ViewModel() {

    val mItemsList: LiveData<List<Item>>
    val newItemEvent = SingleLiveEvent<Int>()

    init {
        mItemsList = if (seriesId == 0) {
            itemRepository.allItems
        } else {
            itemRepository.getItemsInSeries(seriesId)
        }
    }

    fun insert(item: Item) = viewModelScope.launch { itemRepository.insert(item) }
    fun complete(item: Item) = viewModelScope.launch {
        val newItemId = itemRepository.completeItem(item)
        val series = itemRepository.getDirectSerie(item.seriesId)
        if (newItemId != 0 && !series.series.autoCreate) newItemEvent.value = newItemId
    }
    fun delete(item: Item) = viewModelScope.launch { itemRepository.delete(item) }
    fun delete(item: Int) {
        if (item == 0) return
        viewModelScope.launch {
            delete(itemRepository.getDirectItem(item))
        }
    }

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
}