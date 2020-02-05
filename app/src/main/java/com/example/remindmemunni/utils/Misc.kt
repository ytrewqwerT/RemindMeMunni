package com.example.remindmemunni.utils

fun Double?.toStringTrimmed(): String = when (this) {
    null -> "null"
    this.toLong().toDouble() -> this.toLong().toString()
    else -> this.toString()
}
