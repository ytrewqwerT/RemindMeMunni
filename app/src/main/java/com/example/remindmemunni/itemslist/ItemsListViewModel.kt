package com.example.remindmemunni.itemslist

import androidx.lifecycle.*
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ItemsListViewModel(private val itemRepository: ItemRepository, seriesId: Int = 0)
    : ViewModel() {

    private val sourceItems: LiveData<List<Item>> =
        if (seriesId == 0) itemRepository.allItems else itemRepository.getItemsInSeries(seriesId)

    private val _items = MediatorLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items
    val newItemEvent = SingleLiveEvent<Item>()

    val lowerTimeBound = MutableLiveData(0L)
    val upperTimeBound = MutableLiveData(Long.MAX_VALUE)

    private var filterString: String = ""
    private val _filteredItems = MediatorLiveData<List<Item>>()
    val filteredItems: LiveData<List<Item>> get() = _filteredItems

    init {
        _items.addSource(sourceItems) { updateItemsList() }
        _items.addSource(lowerTimeBound) { updateItemsList() }
        _items.addSource(upperTimeBound) { updateItemsList() }
        _filteredItems.addSource(items) { updateFilteredItems() }
    }

    private fun updateItemsList() {
        val allItems = sourceItems.value ?: return
        val lowerTime = lowerTimeBound.value ?: 0L
        val upperTime = upperTimeBound.value ?: Long.MAX_VALUE
        var lowerIndex = 0
        while (lowerIndex < allItems.size && allItems[lowerIndex].time < lowerTime)
            lowerIndex++
        var upperIndex = lowerIndex
        while (upperIndex < allItems.size && allItems[upperIndex].time < upperTime)
            upperIndex++
        _items.value = allItems.subList(lowerIndex, upperIndex)
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

    fun setFilter(filterText: String?) {
        filterString = filterText ?: ""
        updateFilteredItems()
    }
    private fun updateFilteredItems() {
        val items = items.value
        if (items == null) {
            _filteredItems.value = emptyList()
        } else {
            val result = ArrayList<Item>(items)
            if (filterString.isNotEmpty()) {
                for (item in items) {
                    if (!item.hasFilterText(filterString)) result.remove(item)
                }
            }
            _filteredItems.value = result
        }
    }
}