package com.example.remindmemunni.itemslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.SingleLiveEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ItemsListViewModel(private val itemRepository: ItemRepository, seriesId: Int = 0)
    : ViewModel() {

    private val sourceItems: Flow<List<Item>> =
        if (seriesId == 0) itemRepository.allItems else itemRepository.getItemsInSeries(seriesId)
    val filteredItems: LiveData<List<Item>>

    val newItemEvent = SingleLiveEvent<Item>()

    // This implementation for receiving values may result in lost values on channel overflow, but
    // overflow is unlikely since (as of writing) lower/upper bound is only set on fragment creation
    // and the filter string is limited by the user's typing speed, which prooobably won't outpace
    // the O(n) filtering operation.
    val lowerTimeBoundChannel = Channel<Long>(CHANNEL_SIZE)
    val upperTimeBoundChannel = Channel<Long>(CHANNEL_SIZE)
    val filterStringChannel = Channel<String>(CHANNEL_SIZE)

    init {
        lowerTimeBoundChannel.offer(0L)
        upperTimeBoundChannel.offer(Long.MAX_VALUE)
        filterStringChannel.offer("")

        filteredItems = sourceItems.combine(lowerTimeBoundChannel.receiveAsFlow()) { items, lower ->
            items.filter { it.time >= lower }
        }.combine(upperTimeBoundChannel.receiveAsFlow()) { items, upper ->
            items.filter { it.time <= upper }
        }.combine(filterStringChannel.receiveAsFlow()) { items, filterString ->
            if (filterString.isBlank()) items
            else items.filter { it.hasFilterText(filterString) }
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun insert(item: Item) = viewModelScope.launch { itemRepository.insert(item) }
    fun complete(item: Item) = viewModelScope.launch {
        val series = itemRepository.getDirectSerie(item.seriesId)
        val nextItem = itemRepository.completeItem(item)

        // Nested if wasn't correctly smart-casting nextItem to non-null in IDE. :S
        nextItem?.let {
            if (series.series.autoCreate) {
                insert(it)
                itemRepository.incrementSeries(series.series.id)
            } else {
                newItemEvent.value = it
            }
        }
    }
    fun delete(item: Item) = viewModelScope.launch { itemRepository.delete(item) }
    fun delete(item: Int) {
        if (item != 0) viewModelScope.launch {
            delete(itemRepository.getDirectItem(item))
        }
    }

    companion object {
        private const val CHANNEL_SIZE = 5
    }
}