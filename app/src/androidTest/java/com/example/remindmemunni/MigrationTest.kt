package com.example.remindmemunni

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.remindmemunni.database.ItemRoomDatabase
import com.example.remindmemunni.database.MIGRATION_1_2
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
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO series_table (name, cost, curNum, numPrefix)" +
                    "VALUES ('Bottle', 45.5, 1.0, 'No.')")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        val cursor = db.query("SELECT * FROM series_table")
        assertEquals(cursor.count, 1)
        cursor.moveToFirst()
        assertEquals(cursor.columnCount, 7)
        assertEquals(cursor.getString(1), "Bottle")
        assertEquals(cursor.getDouble(2), 45.5, DOUBLE_DELTA)
        assertEquals(cursor.getDouble(3), 1.0, DOUBLE_DELTA)
        assertEquals(cursor.getString(4), "No.")
        assertEquals(cursor.getInt(5), 0)
        assertEquals(cursor.getInt(6), 0)
    }
}