package com.example.remindmemunni.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series_table")
data class Series (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val cost: Double = 0.0,
    val curNum: Double = 0.0,
    val numPrefix: String = ""
) {

    override fun toString(): String {
        return "$id $name: \$$cost, at $numPrefix$curNum"
    }

}