package com.example.remindmemunni

import androidx.lifecycle.*
import com.example.remindmemunni.data.ItemRepository
import com.example.remindmemunni.utils.PrimitiveDateTime
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    private var latestCategories: List<String> = emptyList()
    private val _categories = itemRepository.getCategories().map { categories ->
        categories.filter { it.isNotBlank() }
    }
    val categories = _categories.asLiveData(viewModelScope.coroutineContext)

    private val _categoryFilter = MutableLiveData<String?>()
    val categoryFilter: LiveData<String?> = _categoryFilter
    val searchFilter = MutableLiveData<String?>()

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

    init {
        _categoryFilter.value = null
        monthsOffset = monthsOffset.coerceIn(0..11)
        updateMunniCalc()

        viewModelScope.launch {
            _categories.collect { latestCategories = listOf(CATEGORY_ALL, CATEGORY_NONE) + it }
        }
    }

    // Returns true if a category was successfully set (including; false if
    fun setCategoryFilter(category: String): Boolean {
        if (latestCategories.contains(category).not()) return false
        _categoryFilter.value = when (category) {
            CATEGORY_ALL -> null
            CATEGORY_NONE -> ""
            else -> category
        }
        return true
    }

    fun setMunni(value: Double?) { itemRepository.setMunni(value ?: 0.0) }

    fun updateMunniCalc() {
        // Get end of [Current month + months] (aka start of next month) as epoch seconds
        val endLocalDateTime = LocalDate.now()
            .plusMonths(monthsOffset.toLong() + 1)
            .withDayOfMonth(1)
            .atTime(0, 0)
        val endEpoch = PrimitiveDateTime.fromLocalDateTime(endLocalDateTime).toEpoch()

        viewModelScope.launch {
            val items = itemRepository.allItems.first()
            val series = itemRepository.allSeries.first()

            var remainingMunni = itemRepository.latestMunni
            for (item in items) if (item.time < endEpoch) remainingMunni += item.cost
            for (serie in series) remainingMunni += serie.getHiddenCost(endLocalDateTime)

            val formatter = DateTimeFormatter.ofPattern("MMMM")
            val endTimeStr = endLocalDateTime.minusMonths(1).format(formatter)
            _munniRemaining.value = "End of ${endTimeStr}: \$$remainingMunni"
        }
    }

    suspend fun getSerie(serieId: Int) = itemRepository.getDirectSerie(serieId)

    companion object {
        const val CATEGORY_ALL = "All"
        const val CATEGORY_NONE = "Uncategorised"
    }
}