package com.example.remindmemunni.activityseries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase

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

    class SeriesViewModelFactory(
        private val application: Application,
        private val seriesId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SeriesViewModel(application, seriesId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}