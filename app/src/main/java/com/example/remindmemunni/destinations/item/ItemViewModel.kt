package com.example.remindmemunni.destinations.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmemunni.data.ItemRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ItemViewModel(
    private val itemRepository: ItemRepository,
    itemId: Int
) : ViewModel() {

    private val _name = MutableLiveData("")
    private val _series = MutableLiveData("")
    private val _category = MutableLiveData("")
    private val _cost = MutableLiveData("")
    private val _time = MutableLiveData("")
    private val _notify = MutableLiveData(false)
    val name: LiveData<String> = _name
    val series: LiveData<String> = _series
    val category: LiveData<String> = _category
    val cost: LiveData<String> = _cost
    val time: LiveData<String> = _time
    val notify: LiveData<Boolean> = _notify

    init {
        viewModelScope.launch {
            itemRepository.getItem(itemId).collect {
                _name.value = it.name
                setSeries(it.seriesId)
                setCategory(it.category)
                setTime(it.getDateString())
                setCost(it.getCostString())
                _notify.value = it.notify
            }
        }
    }

    private suspend fun setSeries(seriesId: Int) {
        _series.value = if (seriesId == 0) ""
        else "Series: ${itemRepository.getDirectSerie(seriesId).series.name}"
    }
    private fun setCategory(categoryString: String) {
        _category.value = if (categoryString.isEmpty()) ""
        else "Category: $categoryString"
    }
    private fun setTime(timeString: String) {
        _time.value = if (timeString.isEmpty()) ""
        else "Time: $timeString"
    }
    private fun setCost(costString: String) {
        _cost.value = if (costString.isEmpty()) ""
        else "Cost: $costString"
    }
}