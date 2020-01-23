package com.example.remindmemunni

interface ListItemViewable {

    fun getListItemContents(): ListItemContents

    data class ListItemContents(
        val mMainText: String = "",
        val mSubLeftText: String = "",
        val mSubRightText: String = ""
    )
}