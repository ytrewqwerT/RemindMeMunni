package com.example.remindmemunni.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Item::class, Series::class], version = 2, exportSchema = true)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {

        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context: Context): ItemRoomDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE series_table ADD COLUMN recurMonths INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE series_table ADD COLUMN recurDays INTEGER NOT NULL DEFAULT 0")
    }
}