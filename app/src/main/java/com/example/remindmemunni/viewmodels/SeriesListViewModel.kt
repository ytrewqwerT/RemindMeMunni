package com.example.remindmemunni.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.Series
import kotlinx.coroutines.launch

class SeriesListViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    val series: LiveData<List<AggregatedSeries>> = itemRepository.allSeries

    fun insert(serie: Series) = viewModelScope.launch { itemRepository.insert(serie) }
    fun delete(serie: Series) = viewModelScope.launch { itemRepository.delete(serie) }

    fun setFilter(filterText: String?) {
        // TODO
        Log.e("SeriesListViewModel", "Series filtering not implemented ($filterText)")
    }

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
}