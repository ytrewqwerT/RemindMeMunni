package com.example.remindmemunni.destinations.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.ItemRepository

class SeriesViewModel(
    private val itemRepository: ItemRepository,
    private val seriesId: Int
) : ViewModel() {

    val series: LiveData<AggregatedSeries> =
        itemRepository.getSerie(seriesId).asLiveData(viewModelScope.coroutineContext)

    suspend fun generateNextItemInSeries() =
        itemRepository.getDirectSerie(seriesId).generateNextInSeries()
}