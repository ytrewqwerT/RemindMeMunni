package com.example.remindmemunni

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class ScrollSpinner<T: ListItemViewable>(context: Context, attrs: AttributeSet)
    : RecyclerView(context, attrs) {

    private val mAdapter: CustomRecyclerViewAdapter<T> = CustomRecyclerViewAdapter(null)
    private val mSnapHelper: SnapHelper = object : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: LayoutManager?,
            velocityX: Int, velocityY: Int
        ): Int {
            return when (
                val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
            ) {
                NO_POSITION -> NO_POSITION
                else -> boundSize(position)
            }
        }
        private fun boundSize(position: Int): Int = when {
            position < paddingSize -> paddingSize
            position > mItems.size - paddingSize -> mItems.size - paddingSize - 1
            else -> position
        }
    }
    private var mItems: List<T?> = emptyList()
    private var paddingSize = 5

    init {
        super.setAdapter(mAdapter)
        layoutManager = LinearLayoutManager(context)
        mSnapHelper.attachToRecyclerView(this)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        throw IllegalAccessException("${ScrollSpinner::class.simpleName} doesn't support setting adapters")
    }

    fun setItems(items: List<T?>) {
        // TODO: Set paddingSize such that there is enough to allow all items to be selected
        val newItems = items.toMutableList()
        for (i in 1..paddingSize) {
            newItems.add(0, null)
            newItems.add(null)
        }

        mItems = newItems
        mAdapter.setItems(mItems)
        smoothScrollToPosition(paddingSize + 2)
    }

    fun getSelectedItem(): T? {
        val snapView = mSnapHelper.findSnapView(layoutManager) ?: return null
        val position = layoutManager?.getPosition(snapView) ?: return null
        return mAdapter.getItem(position)
    }
}