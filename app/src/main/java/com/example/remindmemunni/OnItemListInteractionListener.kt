package com.example.remindmemunni

import com.example.remindmemunni.database.Item

interface OnItemListInteractionListener {
    fun onInteraction(item: Item)
}