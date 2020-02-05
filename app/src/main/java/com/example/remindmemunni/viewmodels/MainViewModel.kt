package com.example.remindmemunni.viewmodels

import android.util.Log
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
    val curMunni = MutableLiveData<String>()
    var monthsOffset = 0
        set(value) {
            field = value
            updateMunniCalc()
        }

    val allItems = itemRepository.allItems
    val allSeries = itemRepository.allSeries

    init {
        updateMunniCalc()
        curMunni.value = itemRepository.munni.toString()
        curMunni.observeForever {
            itemRepository.munni = it.toDoubleOrNull() ?: 0.0
            updateMunniCalc()
            Log.d("Nice", "${itemRepository.munni}")
        }
    }

    fun updateMunniCalc() {
        // Get end of [Current month + months] (aka start of next month) as epoch seconds
        val endLocalDateTime = LocalDate.now()
            .plusMonths(monthsOffset.toLong() + 1)
            .withDayOfMonth(1)
            .atTime(0, 0)
        val endEpoch = PrimitiveDateTime.fromLocalDateTime(endLocalDateTime).toEpoch()

        var remainingMunni = itemRepository.munni
        val items = allItems.value
        if (items != null) {
            for (item in items) {
                if (item.time < endEpoch) remainingMunni += item.cost
            }
        }
        val series = allSeries.value
        if (series != null) {
            for (serie in series) {
                remainingMunni += serie.getHiddenCost(endLocalDateTime)
            }
        }

        val formatter = DateTimeFormatter.ofPattern("MMMM")
        _munniRemaining.value = "End of ${endLocalDateTime.minusMonths(1).format(formatter)} = $remainingMunni"
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