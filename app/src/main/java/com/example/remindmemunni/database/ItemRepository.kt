package com.example.remindmemunni.database

import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getItems()
    val allSeries: LiveData<List<AggregatedSeries>> = itemDao.getSeries()

    suspend fun insert(item: Item): Int = itemDao.insert(item).toInt()
    suspend fun insert(series: Series): Int = itemDao.insert(series).toInt()

    fun getItem(itemId: Int): LiveData<Item> = itemDao.getItem(itemId)
    suspend fun getDirectItem(itemId: Int): Item = itemDao.getDirectItem(itemId)
    suspend fun completeItem(item: Item): Int {
        var newItemId = 0
        if (item.seriesId != 0) {
            val series = getDirectSerie(item.seriesId)
            if (item == series.items.last()) {
                val newItem = series.generateNextInSeries()
                if (newItem != null) {
                    newItemId = insert(newItem)
                    if (series.series.autoCreate) incrementSeries(item.seriesId)
                }
            }
        }
        delete(item)
        return newItemId
    }

    fun getSerie(seriesId: Int): LiveData<AggregatedSeries> = itemDao.getSerie(seriesId)
    suspend fun getDirectSerie(seriesId: Int): AggregatedSeries = itemDao.getDirectSerie(seriesId)
    suspend fun incrementSeries(seriesId: Int, increment: Double = 1.0) {
        if (seriesId == 0) return
        val series = itemDao.getDirectSerie(seriesId)
        series.series.curNum += increment
        itemDao.insert(series.series)
    }

    suspend fun delete(item: Item) { itemDao.delete(item) }
    suspend fun delete(series: Series) { itemDao.delete(series) }
}