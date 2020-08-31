package com.example.remindmemunni

import androidx.lifecycle.*
import com.example.remindmemunni.data.ItemRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivityViewModel(itemRepository: ItemRepository) : ViewModel() {
    private var latestCategories: List<String> = emptyList()
    private val _categories = itemRepository.getCategories().map { categories ->
        categories.filter { it.isNotBlank() }
    }
    val categories = _categories.asLiveData(viewModelScope.coroutineContext)

    private val _categoryFilter = MutableLiveData<String?>()
    val categoryFilter: LiveData<String?> = _categoryFilter

    init {
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

    companion object {
        const val CATEGORY_ALL = "All"
        const val CATEGORY_NONE = "Uncategorised"
    }
}