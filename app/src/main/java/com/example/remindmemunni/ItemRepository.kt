package com.example.remindmemunni

import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getItems()
    val allSeries: LiveData<List<AggregatedSeries>> = itemDao.getSeries()

    suspend fun insert(item: Item) {
        itemDao.insert(item)
    }

    suspend fun insert(series: Series) {
        itemDao.insert(series)
    }

}