package com.example.remindmemunni

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class ScrollSpinner<T: ListItemViewable>(context: Context, attrs: AttributeSet)
    : RecyclerView(context, attrs) {

    private var mItemViewHeight: Int = 1
    private val mPaint: Paint = Paint()
    private var mItems: List<T?> = emptyList()
    private var mPaddingSize = 1
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
            position < mPaddingSize -> mPaddingSize
            position > mItems.size - mPaddingSize -> mItems.size - mPaddingSize - 1
            else -> position
        }
    }

    init {
        super.setAdapter(mAdapter)
        layoutManager = LinearLayoutManager(context)
        mSnapHelper.attachToRecyclerView(this)
        mPaint.color = resources.getColor(R.color.colorAccent, context.theme)
        mPaint.strokeWidth = 3F
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)

        val itemView = mSnapHelper.findSnapView(layoutManager)
        if (itemView != null) {
            mItemViewHeight = itemView.height + (itemView.marginTop + itemView.marginBottom) / 2
        }

        var lineHeight = (top + bottom).toFloat() / 2 - mItemViewHeight.toFloat() / 2
        c?.drawLine(0F, lineHeight, width.toFloat(), lineHeight, mPaint)
        lineHeight += mItemViewHeight
        c?.drawLine(0F, lineHeight, width.toFloat(), lineHeight, mPaint)
        Log.d("ScrollSpinner", "$this")
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Setting adapters is not supported")
    override fun setAdapter(adapter: Adapter<*>?) {
        throw UnsupportedOperationException(
            "${ScrollSpinner::class.simpleName} doesn't support setting adapters"
        )
    }

    fun setItems(items: List<T?>) {
        // TODO: Set paddingSize such that there is enough to allow all items to be selected
        val newItems = items.toMutableList()
        for (i in 1..mPaddingSize) {
            newItems.add(0, null)
            newItems.add(null)
        }
        mItems = newItems
        mAdapter.setItems(mItems)
    }

    fun getSelectedItem(): T? {
        val snapView = mSnapHelper.findSnapView(layoutManager) ?: return null
        val position = layoutManager?.getPosition(snapView) ?: return null
        return mAdapter.getItem(position)
    }
}