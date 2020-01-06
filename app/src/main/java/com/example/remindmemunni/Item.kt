package com.example.remindmemunni

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity(tableName = "item_table")
data class Item (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val seriesId: Int,
    val name: String,
    val cost: Double,
    val time: Long
) {

    override fun toString(): String {
        val offset = OffsetDateTime.now().offset
        val date = LocalDateTime.ofEpochSecond(time, 0, offset)
        return "$name: \$$cost, $date"
    }

}