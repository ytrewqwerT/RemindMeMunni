package com.example.remindmemunni

import java.time.LocalDateTime

class PrimitiveDateTime {
    var mYear: Int = 0
    var mMonth: Int = 0
    var mDayOfMonth: Int = 0
    var mHour: Int = 0
    var mMinute: Int = 0

    fun toLocalDateTime(): LocalDateTime?  = when (mYear) {
        0 -> null
        else -> LocalDateTime.of(mYear, mMonth+1, mDayOfMonth, mHour, mMinute)
    }
}