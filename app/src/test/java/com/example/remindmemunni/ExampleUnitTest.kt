package com.example.remindmemunni

import com.example.remindmemunni.utils.PrimitiveDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun primitiveDateTimeConversions() {
        var pdt =
            PrimitiveDateTime(2000, 1, 1, 9, 0)
        val ldt = pdt.toLocalDateTime()
        assertEquals(2000, ldt?.year)
        assertEquals(1, ldt?.monthValue)
        assertEquals(1, ldt?.dayOfMonth)
        assertEquals(9, ldt?.hour)
        assertEquals(0, ldt?.minute)

        pdt = PrimitiveDateTime.fromLocalDateTime(ldt!!)
        assertEquals(pdt.mYear, 2000)
        assertEquals(pdt.mMonth, 1)
        assertEquals(pdt.mDayOfMonth, 1)
        assertEquals(pdt.mHour, 9)
        assertEquals(pdt.mMinute, 0)

        val testVal = 39600L
        val epoch = PrimitiveDateTime.fromEpoch(testVal).toEpoch()
        assertEquals(testVal, epoch)
    }
}
