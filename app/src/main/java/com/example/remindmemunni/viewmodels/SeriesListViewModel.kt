package com.example.remindmemunni.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.database.Series
import kotlinx.coroutines.launch

class SeriesListViewModel(app: Application) : AndroidViewModel(app) {

    private val itemRepository: ItemRepository
    val series: LiveData<List<AggregatedSeries>>

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        series = itemRepository.allSeries
    }

    fun insert(serie: Series) = viewModelScope.launch { itemRepository.insert(serie) }
    fun delete(serie: Series) = viewModelScope.launch { itemRepository.delete(serie) }
}