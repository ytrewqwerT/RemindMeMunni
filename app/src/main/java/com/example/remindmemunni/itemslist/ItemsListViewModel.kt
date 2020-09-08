package com.example.remindmemunni.itemslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ItemsListViewModel(itemRepository: ItemRepository, seriesId: Int = 0) : ViewModel() {

    private val sourceItems: Flow<List<Item>> =
        if (seriesId == 0) itemRepository.allItems else itemRepository.getItemsInSeries(seriesId)
    private val _filteredItems = MutableLiveData<List<Item>>()
    val filteredItems: LiveData<List<Item>> = _filteredItems

    // This implementation for receiving values may result in lost values on channel overflow, but
    // overflow is unlikely since (as of writing) lower/upper bound is only set on fragment creation
    // and the filter string is limited by the user's typing speed, which prooobably won't outpace
    // the O(n) filtering operation.
    val lowerTimeBoundChannel = Channel<Long>(CHANNEL_SIZE)
    val upperTimeBoundChannel = Channel<Long>(CHANNEL_SIZE)
    val filterStringChannel = Channel<String>(CHANNEL_SIZE)
    val filterCategoryChannel = Channel<String?>(CHANNEL_SIZE)

    init {
        lowerTimeBoundChannel.offer(0L)
        upperTimeBoundChannel.offer(Long.MAX_VALUE)
        filterStringChannel.offer("")
        filterCategoryChannel.offer(null)

        val filteredItemsFlow =
            sourceItems.combine(lowerTimeBoundChannel.receiveAsFlow()) { items, lower ->
                items.filter { it.time >= lower }
            }.combine(upperTimeBoundChannel.receiveAsFlow()) { items, upper ->
                items.filter { it.time <= upper }
            }.combine(filterStringChannel.receiveAsFlow()) { items, filterString ->
                if (filterString.isBlank()) items
                else items.filter { it.hasFilterText(filterString) }
            }.combine(filterCategoryChannel.receiveAsFlow()) { items, filterCategory ->
                if (filterCategory == null) items
                else items.filter { it.category == filterCategory }
            }

        // Using Flow.asLiveData() doesn't seem to update the LiveData after the creation of a new
        // Item with a non-zero time?!? (The combine() sequence above doesn't even execute?)
        // Manually collecting the flow seems to work fine though... :/
        //   filteredItems = filteredItemsFlow.asLiveData(viewModelScope.coroutineContext)
        viewModelScope.launch {
            filteredItemsFlow.collect { _filteredItems.value = it }
        }
    }

    companion object {
        private const val CHANNEL_SIZE = 5
    }
}