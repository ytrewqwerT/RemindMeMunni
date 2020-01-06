package com.example.remindmemunni

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series_table")
data class Series (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val cost: Double,
    val curNum: String,
    val numPrefix: String
)