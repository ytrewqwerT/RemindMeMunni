package com.example.remindmemunni.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.remindmemunni.R

class ScrollSpinner<T: ListItemViewable>(context: Context, attrs: AttributeSet)
    : RecyclerView(context, attrs) {

    private var itemViewHeight: Int = 1
    private val indicatorPaint: Paint = Paint()
    private var items: List<T?> = emptyList()
    private var listPaddingSize = 1
    private val customAdapter = ListItemRecyclerViewAdapter<T>(null)

    private val snapHelper: SnapHelper = object : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: LayoutManager?,
            velocityX: Int, velocityY: Int
        ): Int {
            val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
            return position.takeIf { it == NO_POSITION } ?: boundSize(position)
        }

        private fun boundSize(position: Int): Int = when {
            position < listPaddingSize -> listPaddingSize
            position > items.size - listPaddingSize -> items.size - listPaddingSize - 1
            else -> position
        }
    }

    init {
        super.setAdapter(customAdapter)
        layoutManager = LinearLayoutManager(context)
        snapHelper.attachToRecyclerView(this)
        indicatorPaint.color = resources.getColor(R.color.colorAccent, context.theme)
        indicatorPaint.strokeWidth = 3F
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)

        snapHelper.findSnapView(layoutManager)?.let { itemView ->
            itemViewHeight = itemView.height + (itemView.marginTop + itemView.marginBottom) / 2
        }

        var lineHeight = (top + bottom).toFloat() / 2 - itemViewHeight.toFloat() / 2
        c?.drawLine(0F, lineHeight, width.toFloat(), lineHeight, indicatorPaint)
        lineHeight += itemViewHeight
        c?.drawLine(0F, lineHeight, width.toFloat(), lineHeight, indicatorPaint)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        throw UnsupportedOperationException(
            "${ScrollSpinner::class.simpleName} doesn't support setting adapters"
        )
    }

    fun setItems(items: List<T?>) {
        // TODO: Set paddingSize such that there is enough to allow all items to be selected
        val newItems = items.toMutableList()
        // Pad the items with dummies to allow all items to be selected
        for (i in 1..listPaddingSize) newItems.add(0, null)
        for (i in 1..listPaddingSize) newItems.add(null)

        this.items = newItems
        customAdapter.setItems(this.items)
    }

    fun getSelectedItem(): T? {
        val snapView = snapHelper.findSnapView(layoutManager) ?: return null
        val position = layoutManager?.getPosition(snapView) ?: return null
        return customAdapter.getItem(position)
    }
}