package com.example.remindmemunni.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.remindmemunni.database.Item
import com.example.remindmemunni.database.ItemRepository
import com.example.remindmemunni.database.ItemRoomDatabase
import kotlinx.coroutines.launch

class ItemsListViewModel(app: Application, seriesId: Int = 0)
    : AndroidViewModel(app) {

    val mItemsList: LiveData<List<Item>>
    private val itemRepository: ItemRepository

    init {
        val itemDao = ItemRoomDatabase.getDatabase(app).itemDao()
        itemRepository = ItemRepository(itemDao)
        mItemsList = if (seriesId == 0) {
            itemDao.getItems()
        } else {
            itemDao.getItemsInSeries(seriesId)
        }
    }

    fun insert(item: Item) = viewModelScope.launch { itemRepository.insert(item) }
    fun complete(item: Item) = viewModelScope.launch { itemRepository.completeItem(item) }
    fun delete(item: Item) = viewModelScope.launch { itemRepository.delete(item) }

    class ItemsListViewModelFactory(
        private val application: Application,
        private val seriesId: Int = 0
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemsListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ItemsListViewModel(application, seriesId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}