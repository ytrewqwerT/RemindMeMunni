package com.example.remindmemunni.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM item_table ORDER BY time ASC")
    fun getItems(): LiveData<List<Item>>

    @Transaction
    @Query("SELECT * FROM series_table ORDER BY id ASC")
    fun getSeries(): LiveData<List<AggregatedSeries>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(series: Series)

    @Delete
    suspend fun delete(item: Item)

    @Delete
    suspend fun delete(series: Series)

    @Query("DELETE FROM item_table")
    suspend fun deleteAllItems()

    @Query("DELETE FROM series_table")
    suspend fun deleteAllSeries()

}