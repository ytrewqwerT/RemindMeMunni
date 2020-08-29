package com.example.remindmemunni.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.ItemRepository

class SeriesViewModel(
    itemRepository: ItemRepository,
    seriesId: Int
) : ViewModel() {

    val series: LiveData<AggregatedSeries> = itemRepository.getSerie(seriesId)
}