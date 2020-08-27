package com.example.remindmemunni.itemslist

import androidx.lifecycle.*
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ItemsListViewModel(private val itemRepository: ItemRepository, seriesId: Int = 0)
    : ViewModel() {

    private val sourceItems: LiveData<List<Item>>
    private val _items = MediatorLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items
    val newItemEvent = SingleLiveEvent<Int>()

    val lowerTimeBound = MutableLiveData(0L)
    val upperTimeBound = MutableLiveData(Long.MAX_VALUE)

    private var filterString: String = ""
    private val _filteredItems = MediatorLiveData<List<Item>>()
    val filteredItems: LiveData<List<Item>> get() = _filteredItems

    init {
        sourceItems = if (seriesId == 0) {
            itemRepository.allItems
        } else {
            itemRepository.getItemsInSeries(seriesId)
        }
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
        val newItemId = itemRepository.completeItem(item)
        val series = itemRepository.getDirectSerie(item.seriesId)
        if (newItemId != 0 && !series.series.autoCreate) newItemEvent.value = newItemId
    }
    fun delete(item: Item) = viewModelScope.launch { itemRepository.delete(item) }
    fun delete(item: Int) {
        if (item == 0) return
        viewModelScope.launch {
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
            _filteredItems.value = items
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

    class ItemsListViewModelFactory(
        private val itemRepository: ItemRepository,
        private val seriesId: Int = 0
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemsListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ItemsListViewModel(
                    itemRepository,
                    seriesId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}