package com.example.remindmemunni.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import kotlinx.coroutines.launch

class SeriesViewModel(
    private val itemRepository: ItemRepository,
    seriesId: Int
) : ViewModel() {

    val series: LiveData<AggregatedSeries> = itemRepository.getSerie(seriesId)

    private fun deleteItem(item: Item) = viewModelScope.launch { itemRepository.delete(item) }
    fun deleteItem(item: Int) {
        if (item != 0) viewModelScope.launch {
            deleteItem(itemRepository.getDirectItem(item))
        }
    }
}