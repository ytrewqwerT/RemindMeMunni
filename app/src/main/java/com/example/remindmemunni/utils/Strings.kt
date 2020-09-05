package com.example.remindmemunni.utils

import androidx.annotation.StringRes
import com.example.remindmemunni.App

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()) =
        App.context.getString(stringRes, *formatArgs)
}