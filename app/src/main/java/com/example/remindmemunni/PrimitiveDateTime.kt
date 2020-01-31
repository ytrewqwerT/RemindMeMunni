package com.example.remindmemunni

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class PrimitiveDateTime(
    var mYear: Int = 0,
    var mMonth: Int = 0,
    var mDayOfMonth: Int = 0,
    var mHour: Int = 0,
    var mMinute: Int = 0
) {

    fun toLocalDateTime(): LocalDateTime?  = when (mYear) {
        0 -> null
        else -> LocalDateTime.of(mYear, mMonth, mDayOfMonth, mHour, mMinute)
    }

    fun toEpoch(): Long {
        val localDateTime = toLocalDateTime()
        val zonedDateTime = localDateTime?.atZone(ZoneId.systemDefault())
        return zonedDateTime?.toEpochSecond() ?: 0
    }

     companion object {
         fun fromLocalDateTime(time: LocalDateTime): PrimitiveDateTime = PrimitiveDateTime(
             time.year, time.monthValue, time.dayOfMonth,
             time.hour, time.minute
         )

         fun fromEpoch(epochSeconds: Long): PrimitiveDateTime {
             if (epochSeconds == 0L) return PrimitiveDateTime()
             val localDateTime = LocalDateTime.ofInstant(
                 Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault()
             )
             return fromLocalDateTime(localDateTime)
         }
     }
}