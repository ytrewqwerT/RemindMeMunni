package com.example.remindmemunni.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM item_table ORDER BY time ASC")
    fun getItems(): LiveData<List<Item>>
    @Query("SELECT * FROM item_table WHERE (:itemId)= id")
    fun getItem(itemId: Int): LiveData<Item>
    @Query("SELECT * FROM item_table WHERE (:itemId)= id")
    suspend fun getDirectItem(itemId: Int): Item

    @Query("SELECT * FROM item_table WHERE notify = 1")
    fun getNotifyItems(): List<Item>

    @Transaction
    @Query("SELECT * FROM series_table ORDER BY name ASC")
    fun getSeries(): LiveData<List<AggregatedSeries>>
    @Transaction
    @Query("SELECT * FROM series_table WHERE (:seriesId)= id")
    fun getSerie(seriesId: Int): LiveData<AggregatedSeries>
    @Transaction
    @Query("SELECT * FROM series_table WHERE (:seriesId)= id")
    suspend fun getDirectSerie(seriesId: Int): AggregatedSeries

    @Query("SELECT * FROM item_table WHERE (:seriesId)= seriesId ORDER BY time ASC")
    fun getItemsInSeries(seriesId: Int): LiveData<List<Item>>

    @Query("SELECT DISTINCT category from item_table " +
            "UNION " +
            "SELECT DISTINCT category from series_table")
    fun getCategories(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(series: Series): Long

    @Delete
    suspend fun delete(item: Item)
    @Delete
    suspend fun delete(series: Series)

    @Query("DELETE FROM item_table")
    suspend fun deleteAllItems()
    @Query("DELETE FROM series_table")
    suspend fun deleteAllSeries()
}