package com.example.remindmemunni.serieslist

import androidx.lifecycle.*
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.Series
import kotlinx.coroutines.launch

class SeriesListViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    val series: LiveData<List<AggregatedSeries>> = itemRepository.allSeries

    private var filterString: String = ""
    private val _filteredSeries = MediatorLiveData<List<AggregatedSeries>>()
    val filteredSeries: LiveData<List<AggregatedSeries>> get() = _filteredSeries

    init {
        _filteredSeries.addSource(series) { updateFilteredSeries() }
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

    fun setFilter(filterText: String?) {
        filterString = filterText ?: ""
        updateFilteredSeries()
    }
    private fun updateFilteredSeries() {
        val series = series.value
        if (series == null) {
            _filteredSeries.value = series
        } else {
            val result = ArrayList<AggregatedSeries>(series)
            if (filterString.isNotEmpty()) {
                for (serie in series) {
                    if (!serie.series.hasFilterText(filterString)) result.remove(serie)
                }
            }
            _filteredSeries.value = result
        }
    }

    class SeriesListViewModelFactory(
        private val itemRepository: ItemRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeriesListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SeriesListViewModel(
                    itemRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}