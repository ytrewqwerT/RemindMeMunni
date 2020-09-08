package com.example.remindmemunni.serieslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SeriesListViewModel(itemRepository: ItemRepository) : ViewModel() {

    private val series: Flow<List<AggregatedSeries>> = itemRepository.allSeries
    private val _filteredSeries = MutableLiveData<List<AggregatedSeries>>()
    val filteredSeries: LiveData<List<AggregatedSeries>> = _filteredSeries

    // Not the greatest solution (see the comment in [ItemsListViewModel]), but it's sufficient.
    val filterStringChannel = Channel<String>(CHANNEL_SIZE)
    val filterCategoryChannel = Channel<String?>(CHANNEL_SIZE)

    init {
        filterStringChannel.offer("")
        filterCategoryChannel.offer(null)

        val filteredSeriesFlow =
            series.combine(filterStringChannel.receiveAsFlow()) { series, filterString ->
                if (filterString.isBlank()) series
                else series.filter { it.series.hasFilterText(filterString) }
            }.combine(filterCategoryChannel.receiveAsFlow()) { series, filterCategory ->
                if (filterCategory == null) series
                else series.filter { it.series.category == filterCategory }
            }
        // Flow.asLiveData() doesn't seem to want to emit updates after changes to a series...
        // (Similar issue in [ItemsListViewModel])
        viewModelScope.launch {
            filteredSeriesFlow.collect { _filteredSeries.value = it }
        }
    }

    companion object {
        private const val CHANNEL_SIZE = 5
    }
}