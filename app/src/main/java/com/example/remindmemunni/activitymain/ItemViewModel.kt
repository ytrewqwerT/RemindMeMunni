package com.example.remindmemunni.activitymain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.*
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val itemRepository: ItemRepository
    val allItems: LiveData<List<Item>>
    val allSeries: LiveData<List<AggregatedSeries>>

    init {
        val itemDao = ItemRoomDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository =
            ItemRepository(itemDao)
        allItems = itemRepository.allItems
        allSeries = itemRepository.allSeries
    }

    fun insert(item: Item) = viewModelScope.launch {
        itemRepository.insert(item)
    }

    fun insert(series: Series) = viewModelScope.launch {
        itemRepository.insert(series)
    }



}