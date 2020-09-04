package com.example.remindmemunni

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.AggregatedSeries
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.data.Series
import com.example.remindmemunni.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ActionViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    private val _oneTimeAction = SingleLiveEvent<Action>()
    val oneTimeAction: LiveData<Action> = _oneTimeAction

    fun view(item: Item) { _oneTimeAction.value = Action.ItemView(item) }
    fun insert(item: Item) = viewModelScope.launch { itemRepository.insert(item) }
    fun edit(item: Item) { _oneTimeAction.value = Action.ItemEdit(item) }
    fun complete(item: Item) = viewModelScope.launch {
        val series = itemRepository.getDirectSerie(item.seriesId)
        val nextItem = itemRepository.completeItem(item)

        // Nested if wasn't correctly smart-casting nextItem to non-null in IDE. :S
        if (nextItem == null) {
            _oneTimeAction.value = Action.ItemFinish(item)
        } else {
            if (series.series.autoCreate) {
                insert(nextItem)
                itemRepository.incrementSeries(series.series.id)
                _oneTimeAction.value = Action.ItemFinish(item)
            } else {
                _oneTimeAction.value = Action.ItemEdit(nextItem)
            }
        }
    }
    fun deleteItem(item: Item) {
        viewModelScope.launch { itemRepository.delete(item) }
        _oneTimeAction.value = Action.ItemDelete(item)
    }

    fun view(serie: Series) { _oneTimeAction.value = Action.SerieView(serie) }
    fun insert(serie: AggregatedSeries) = viewModelScope.launch {
        itemRepository.insert(serie.series)
        for (item in serie.items) itemRepository.insert(item)
    }
    fun insert(serie: Series) = viewModelScope.launch { itemRepository.insert(serie) }
    fun edit(serie: Series) { _oneTimeAction.value = Action.SerieEdit(serie) }
    fun promptDelete(serie: AggregatedSeries) { _oneTimeAction.value = Action.SerieDelete(serie) }
    fun deleteSerieDeep(serie: AggregatedSeries) {
        viewModelScope.launch {
            itemRepository.delete(serie.series)
            for (item in serie.items) itemRepository.delete(item)
        }
    }
    fun deleteSerieShallow(serie: AggregatedSeries) = viewModelScope.launch {
        itemRepository.delete(serie.series)
        for (item in serie.items) itemRepository.insert(item.copy(seriesId = 0))
    }
}