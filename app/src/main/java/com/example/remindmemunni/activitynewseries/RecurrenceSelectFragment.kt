package com.example.remindmemunni.activitynewseries

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.R
import com.example.remindmemunni.getSnapPosition
import kotlin.ClassCastException

class RecurrenceSelectFragment: DialogFragment() {

    private lateinit var listener: RecurrenceSelectListener



    interface RecurrenceSelectListener {
        fun onDialogConfirm(dialog: DialogFragment, months: Int, days: Int)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_frequency, null)

            // TODO: Refactor into new view type?
            val daysRecyclerView = view.findViewById<RecyclerView>(R.id.days_list)
            val daysAdapter = CustomRecyclerViewAdapter<NumberListItem>(null).apply {
                val items = NumberListItem.createSequentialList(0, 30).asReversed()
                items.add(0, NumberListItem())
                items.add(0, NumberListItem())
                items.add(NumberListItem())
                items.add(NumberListItem())
                setItems(items)
            }
            daysRecyclerView.adapter = daysAdapter
            daysRecyclerView.layoutManager = LinearLayoutManager(context)
            val daysSnapHelper = LinearSnapHelper()
            daysSnapHelper.attachToRecyclerView(daysRecyclerView)

            val monthsRecyclerView = view.findViewById<RecyclerView>(R.id.months_list)
            val monthsAdapter = CustomRecyclerViewAdapter<NumberListItem>(null).apply {
                val items = NumberListItem.createSequentialList(0, 24).asReversed()
                items.add(0, NumberListItem())
                items.add(0, NumberListItem())
                items.add(NumberListItem())
                items.add(NumberListItem())
                setItems(items)
            }
            monthsRecyclerView.adapter = monthsAdapter
            monthsRecyclerView.layoutManager = LinearLayoutManager(context)
            val monthsSnapHelper = LinearSnapHelper()
            monthsSnapHelper.attachToRecyclerView(monthsRecyclerView)

            builder.setView(view)
            builder.setPositiveButton("Confirm") { _, _ ->
                var position = daysSnapHelper.getSnapPosition(daysRecyclerView)
                val days = daysAdapter.getItem(position)?.num ?: 0
                position = monthsSnapHelper.getSnapPosition(monthsRecyclerView)
                val months = monthsAdapter.getItem(position)?.num ?: 0

                listener.onDialogConfirm(this, months, days)
                dialog?.dismiss()
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                dialog?.cancel()
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as RecurrenceSelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement ${RecurrenceSelectListener::class.simpleName}"
            )
        }
    }
}