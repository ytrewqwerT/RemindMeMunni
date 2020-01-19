package com.example.remindmemunni.activityseries

import android.app.Application
import androidx.lifecycle.*
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.Item
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import kotlinx.coroutines.launch

class SeriesViewModel(
    application: Application,
    seriesId: Int
) : AndroidViewModel(application) {

    private val repository: ItemRepository

    val series: LiveData<AggregatedSeries>

    init {
        val itemDao = ItemRoomDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
        series = repository.getSerie(seriesId)
    }

    fun insert(item: Item) = viewModelScope.launch {
        repository.insert(item)
    }

    class SeriesViewModelFactory(
        private val application: Application,
        private val seriesId: Int
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            SeriesViewModel(application, seriesId) as T
    }
}