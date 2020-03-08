package com.example.remindmemunni.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Item::class, Series::class], version = 5, exportSchema = true)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {

        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context: Context): ItemRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()
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

val MIGRATION_2_3 = object : Migration(2,3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE series_table ADD COLUMN autoCreate INTEGER NOT NULL DEFAULT 1")
    }
}

val MIGRATION_3_4 = object : Migration(3,4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE series_table ADD COLUMN category TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE item_table ADD COLUMN category TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_4_5 = object : Migration(4,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE item_table ADD COLUMN notify INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE series_table ADD COLUMN notify INTEGER NOT NULL DEFAULT 0")
    }
}