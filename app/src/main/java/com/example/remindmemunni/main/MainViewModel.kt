package com.example.remindmemunni.main

import androidx.lifecycle.*
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.PrimitiveDateTime
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    val curMunni: LiveData<Double> =
        itemRepository.munni.asLiveData(viewModelScope.coroutineContext)
    private val _munniRemaining = MutableLiveData<String>()
    val munniRemaining: LiveData<String> = _munniRemaining

    var monthsOffset = itemRepository.munniCalcEndMonth - LocalDate.now().monthValue
        set(value) {
            field = value
            itemRepository.munniCalcEndMonth = value + LocalDate.now().monthValue
            updateMunniCalc()
        }

    val allItems =
        itemRepository.allItems.asLiveData(viewModelScope.coroutineContext)
    val allSeries =
        itemRepository.allSeries.asLiveData(viewModelScope.coroutineContext)

    val filterText = MutableLiveData<String?>()
    val categoryFilter = MutableLiveData<String?>()

    init {
        updateMunniCalc()
    }

    fun setMunni(value: Double?) {
        itemRepository.setMunni(value ?: 0.0)
    }

    fun updateMunniCalc() {
        // Get end of [Current month + months] (aka start of next month) as epoch seconds
        val endLocalDateTime = LocalDate.now()
            .plusMonths(monthsOffset.toLong() + 1)
            .withDayOfMonth(1)
            .atTime(0, 0)
        val endEpoch = PrimitiveDateTime.fromLocalDateTime(endLocalDateTime).toEpoch()

        viewModelScope.launch {
            var remainingMunni = itemRepository.latestMunni

            val items = allItems.value
            if (items != null) for (item in items) {
                if (item.time < endEpoch) remainingMunni += item.cost
            }

            val series = allSeries.value
            if (series != null) for (serie in series) {
                remainingMunni += serie.getHiddenCost(endLocalDateTime)
            }

            val formatter = DateTimeFormatter.ofPattern("MMMM")
            _munniRemaining.value = "End of ${endLocalDateTime.minusMonths(1).format(formatter)} = $remainingMunni"
        }
    }
}