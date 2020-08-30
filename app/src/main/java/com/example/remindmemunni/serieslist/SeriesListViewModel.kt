package com.example.remindmemunni.serieslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.Series
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SeriesListViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val series: Flow<List<AggregatedSeries>> = itemRepository.allSeries
    val filteredSeries: LiveData<List<AggregatedSeries>>

    // Not the greatest solution (see the comment in [ItemsListViewModel]), but it's sufficient.
    val filterStringChannel = Channel<String>(CHANNEL_SIZE)

    init {
        filterStringChannel.offer("")

        filteredSeries = series.combine(filterStringChannel.receiveAsFlow()) { series, filterString ->
            if (filterString.isBlank()) series
            else series.filter { it.series.hasFilterText(filterString) }
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun insert(serie: Series) = viewModelScope.launch { itemRepository.insert(serie) }
    fun delete(serie: Series) = viewModelScope.launch { itemRepository.delete(serie) }
    fun insert(serie: AggregatedSeries) {
        viewModelScope.launch {
            itemRepository.insert(serie.series)
            for (item in serie.items) itemRepository.insert(item)
        }
    }
    fun delete(serie: AggregatedSeries) {
        viewModelScope.launch {
            itemRepository.delete(serie.series)
            for (item in serie.items) itemRepository.delete(item)
        }
    }

    companion object {
        private const val CHANNEL_SIZE = 5
    }
}