package com.example.remindmemunni.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.utils.PrimitiveDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val _munniRemaining = MutableLiveData<String>()
    val munniRemaining: LiveData<String> = _munniRemaining

    val allItems = itemRepository.allItems

    init {
        updateMunniCalc(0)
    }

    fun updateMunniCalc(months: Int) {
        // Get end of [Current month + months] (aka start of next month) as epoch seconds
        val endLocalDateTime = LocalDate.now()
            .plusMonths(months.toLong() + 1)
            .withDayOfMonth(1)
            .atTime(0, 0)
        val endEpoch = PrimitiveDateTime.fromLocalDateTime(endLocalDateTime).toEpoch()

        val allItems = itemRepository.allItems.value
        var costDelta = 0.0
        if (allItems != null) {
            for (item in allItems) {
                if (item.time < endEpoch) costDelta += item.cost
            }
        }

        val formatter = DateTimeFormatter.ofPattern("MMMM")
        _munniRemaining.value = "End of ${endLocalDateTime.minusMonths(1).format(formatter)} = $costDelta"
    }

    class MainViewModelFactory(private val itemRepository: ItemRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(itemRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}