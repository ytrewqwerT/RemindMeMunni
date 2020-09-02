package com.example.remindmemunni.destinations.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.ItemRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ItemViewModel(
    itemRepository: ItemRepository,
    itemId: Int
) : ViewModel() {

    private val _name = MutableLiveData("")
    private val _series = MutableLiveData("")
    private val _category = MutableLiveData("")
    private val _time = MutableLiveData("")
    private val _cost = MutableLiveData("")
    val name: LiveData<String> = _name
    val series: LiveData<String> = _series
    val category: LiveData<String> = _category
    val time: LiveData<String> = _time
    val cost: LiveData<String> = _cost

    init {
        viewModelScope.launch {
            itemRepository.getItem(itemId).collect {
                _name.value = it.name
                if (it.seriesId != 0) _series.value = itemRepository.getDirectSerie(it.seriesId).series.name
                _category.value = it.category
                _time.value = it.getDateString()
                _cost.value = it.getCostString()
            }
        }
    }
}