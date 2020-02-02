package com.example.remindmemunni

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.database.MIGRATION_1_2
import com.example.remindmemunni.database.MIGRATION_2_3
import com.example.remindmemunni.database.Series
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
        assertEquals(cursor.getInt(0), refSeries.id)
        assertEquals(cursor.getString(1), refSeries.name)
        assertEquals(cursor.getDouble(2), refSeries.cost, DOUBLE_DELTA)
        assertEquals(cursor.getDouble(3), refSeries.curNum, DOUBLE_DELTA)
        assertEquals(cursor.getString(4), refSeries.numPrefix)
        assertEquals(cursor.getInt(5), refSeries.recurMonths)
        assertEquals(cursor.getInt(6), refSeries.recurDays)
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
        assertEquals(cursor.getInt(0), refSeries.id)
        assertEquals(cursor.getString(1), refSeries.name)
        assertEquals(cursor.getDouble(2), refSeries.cost, DOUBLE_DELTA)
        assertEquals(cursor.getDouble(3), refSeries.curNum, DOUBLE_DELTA)
        assertEquals(cursor.getString(4), refSeries.numPrefix)
        assertEquals(cursor.getInt(5), refSeries.recurMonths)
        assertEquals(cursor.getInt(6), refSeries.recurDays)
        assertEquals(cursor.getInt(7) == 1, refSeries.autoCreate)
    }

    @Test
    fun migrateFirstToLast() {
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
        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_1_2, MIGRATION_2_3)

        val cursor = db.query("SELECT * FROM series_table")
        assertEquals(cursor.count, 1)
        cursor.moveToFirst()
        assertEquals(cursor.columnCount, 8)
        assertEquals(cursor.getInt(0), refSeries.id)
        assertEquals(cursor.getString(1), refSeries.name)
        assertEquals(cursor.getDouble(2), refSeries.cost, DOUBLE_DELTA)
        assertEquals(cursor.getDouble(3), refSeries.curNum, DOUBLE_DELTA)
        assertEquals(cursor.getString(4), refSeries.numPrefix)
        assertEquals(cursor.getInt(5), refSeries.recurMonths)
        assertEquals(cursor.getInt(6), refSeries.recurDays)
        assertEquals(cursor.getInt(7) == 1, refSeries.autoCreate)
    }
}