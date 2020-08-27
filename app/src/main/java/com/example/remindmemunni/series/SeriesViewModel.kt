package com.example.remindmemunni.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.ItemRepository

class SeriesViewModel(
    itemRepository: ItemRepository,
    seriesId: Int
) : ViewModel() {

    val series: LiveData<AggregatedSeries> = itemRepository.getSerie(seriesId)

    class SeriesViewModelFactory(
        private val itemRepository: ItemRepository,
        private val seriesId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SeriesViewModel(
                    itemRepository,
                    seriesId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}