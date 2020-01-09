package com.example.remindmemunni

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Item::class, Series::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {

        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ItemRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                ).addCallback(ItemDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }

    }

    private class ItemDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch { populateDatabase(database.itemDao()) }
            }
        }

        suspend fun populateDatabase(itemDao: ItemDao) {
            itemDao.deleteAllItems()
            itemDao.deleteAllSeries()

            itemDao.insert(Item(seriesId = 0, name = "Hello", cost = 1.0))
            itemDao.insert(Item(seriesId = 0, name = "World", cost = 2.0))

            itemDao.insert(Series(name = "A Series", cost = 1.0, curNum = 1.0))
            itemDao.insert(Series(name = "Has Arrived", cost = 5.5, curNum = 1.5))
        }
    }
}