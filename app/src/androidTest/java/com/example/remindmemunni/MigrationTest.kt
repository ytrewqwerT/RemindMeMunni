package com.example.remindmemunni

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.remindmemunni.database.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    companion object {
        const val TEST_DB = "migration-test"
        const val DOUBLE_DELTA = 0.05
    }

    @get: Rule val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ItemRoomDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val refSeries = Series(
            id = 5, name = "Bottle", cost = 45.5,
            curNum = 1.0, numPrefix = "No."
        )
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO series_table (id, name, cost, curNum, numPrefix)" +
                    "VALUES (${refSeries.id}, '${refSeries.name}', ${refSeries.cost}," +
                    "${refSeries.curNum}, '${refSeries.numPrefix}')")
            close()
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        val cursor = db.query("SELECT * FROM series_table")
        assertEquals(cursor.count, 1)
        cursor.moveToFirst()
        assertEquals(cursor.columnCount, 7)
        compareSeries(refSeries, cursor)
    }

    @Test
    fun migrate2To3() {
        val refSeries = Series(
            id = 5, name = "Bottle", cost = 45.5,
            curNum = 1.0, numPrefix = "No.",
            recurMonths = 5, recurDays = 10
        )
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO series_table (id, name, cost, curNum, numPrefix, recurMonths, recurDays)" +
                    "VALUES (${refSeries.id}, '${refSeries.name}', ${refSeries.cost}," +
                    "${refSeries.curNum}, '${refSeries.numPrefix}'," +
                    "${refSeries.recurMonths}, ${refSeries.recurDays})")
            close()
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        val cursor = db.query("SELECT * FROM series_table")
        assertEquals(cursor.count, 1)
        cursor.moveToFirst()
        assertEquals(cursor.columnCount, 8)
        compareSeries(refSeries, cursor)
    }

    @Test
    fun migrate3To4() {
        val refSeries = Series(
            id = 5, name = "Bottle", cost = 45.5,
            curNum = 1.0, numPrefix = "No.",
            recurMonths = 5, recurDays = 10,
            autoCreate = false
        )
        val autoCreateTrue = if (refSeries.autoCreate) 1 else 0
        val refItem = Item(id = 8, seriesId = 2, name = "O", cost = -10.5, time = 10583740)
        helper.createDatabase(TEST_DB, 3).apply {
            execSQL("INSERT INTO series_table" +
                    "(id, name, cost, curNum, numPrefix, recurMonths, recurDays, autoCreate)" +
                    "VALUES (${refSeries.id}, '${refSeries.name}', ${refSeries.cost}," +
                    "${refSeries.curNum}, '${refSeries.numPrefix}'," +
                    "${refSeries.recurMonths}, ${refSeries.recurDays}," +
                    "${autoCreateTrue})")
            execSQL("INSERT INTO item_table (id, seriesId, name, cost, time)" +
                    "VALUES (${refItem.id}, ${refItem.seriesId}," +
                    "'${refItem.name}', ${refItem.cost}, ${refItem.time})")
            close()
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4)

        val seriesCursor = db.query("SELECT * FROM series_table")
        assertEquals(seriesCursor.count, 1)
        seriesCursor.moveToFirst()
        assertEquals(seriesCursor.columnCount, 9)
        compareSeries(refSeries, seriesCursor)

        val itemCursor = db.query("SELECT * FROM item_table")
        assertEquals(itemCursor.count, 1)
        itemCursor.moveToFirst()
        assertEquals(itemCursor.columnCount, 6)
        compareItem(refItem, itemCursor)
    }

    @Test
    fun migrate4To5() {
        val refItem = Item(
            id = 8, seriesId = 2, name = "O", cost = -10.5, time = 10583740, category = "Cheese"
        )
        helper.createDatabase(TEST_DB, 4).apply {
            execSQL("INSERT INTO item_table (id, seriesId, name, cost, time, category)" +
                    "VALUES (${refItem.id}, ${refItem.seriesId}," +
                    "'${refItem.name}', ${refItem.cost}, ${refItem.time}, '${refItem.category}')")
            close()
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5)

        val itemCursor = db.query("SELECT * FROM item_table")
        assertEquals(itemCursor.count, 1)
        itemCursor.moveToFirst()
        assertEquals(itemCursor.columnCount, 7)
        compareItem(refItem, itemCursor)
    }

    @Test
    fun migrateFirstToLast() {
        val refSeries = Series(
            id = 5, name = "Bottle", cost = 45.5,
            curNum = 1.0, numPrefix = "No."
        )
        val refItem = Item(id = 8, seriesId = 2, name = "O", cost = -10.5, time = 10583740)
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO series_table (id, name, cost, curNum, numPrefix)" +
                    "VALUES (${refSeries.id}, '${refSeries.name}', ${refSeries.cost}," +
                    "${refSeries.curNum}, '${refSeries.numPrefix}')")
            execSQL("INSERT INTO item_table (id, seriesId, name, cost, time)" +
                    "VALUES (${refItem.id}, ${refItem.seriesId}," +
                    "'${refItem.name}', ${refItem.cost}, ${refItem.time})")
            close()
        }
        val db = helper.runMigrationsAndValidate(
            TEST_DB, 5, true,
            MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5
        )

        val seriesCursor = db.query("SELECT * FROM series_table")
        assertEquals(seriesCursor.count, 1)
        seriesCursor.moveToFirst()
        assertEquals(seriesCursor.columnCount, 9)
        compareSeries(refSeries, seriesCursor)

        val itemCursor = db.query("SELECT * FROM item_table")
        assertEquals(itemCursor.count, 1)
        itemCursor.moveToFirst()
        assertEquals(itemCursor.columnCount, 7)
        compareItem(refItem, itemCursor)
    }

    private fun compareSeries(original: Series, generated: Cursor) {
        val columnCount = generated.columnCount
        // Version 1
        assertEquals(generated.getInt(0), original.id)
        assertEquals(generated.getString(1), original.name)
        assertEquals(generated.getDouble(2), original.cost, DOUBLE_DELTA)
        assertEquals(generated.getDouble(3), original.curNum, DOUBLE_DELTA)
        assertEquals(generated.getString(4), original.numPrefix)
        // Version 2
        if (columnCount > 5) {
            assertEquals(generated.getInt(5), original.recurMonths)
            assertEquals(generated.getInt(6), original.recurDays)
        }
        // Version 3
        if (columnCount > 7) {
            assertEquals(generated.getInt(7) == 1, original.autoCreate)
        }
        // Version 4
        if (columnCount > 8) {
            assertEquals(generated.getString(8), original.category)
        }
    }

    private fun compareItem(original: Item, generated: Cursor) {
        val columnCount = generated.columnCount
        // Version 1 - 3
        assertEquals(generated.getInt(0), original.id)
        assertEquals(generated.getInt(1), original.seriesId)
        assertEquals(generated.getString(2), original.name)
        assertEquals(generated.getDouble(3), original.cost, DOUBLE_DELTA)
        assertEquals(generated.getLong(4), original.time)
        // Version 4
        if (columnCount > 5) {
            assertEquals(generated.getString(5), original.category)
        }
        // Version 5
        if (columnCount > 6) {
            assertEquals(generated.getInt(6) == 1, original.notify)
        }
    }
}