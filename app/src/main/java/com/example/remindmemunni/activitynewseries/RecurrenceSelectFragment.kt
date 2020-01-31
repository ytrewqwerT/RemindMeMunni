package com.example.remindmemunni.activitynewseries

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.remindmemunni.R
import com.example.remindmemunni.ScrollSpinner
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecurrenceSelectFragment: DialogFragment() {

    private lateinit var listener: RecurrenceSelectListener

    interface RecurrenceSelectListener {
        fun onDialogConfirm(dialog: DialogFragment, months: Int, days: Int)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_frequency, null)

            val daysScrollSpinner =
                view.findViewById<ScrollSpinner<NumberListItem>>(R.id.days_list)
            daysScrollSpinner.setItems(
                NumberListItem.createSequentialList(0, 30).asReversed()
            )
            val monthsScrollSpinner =
                view.findViewById<ScrollSpinner<NumberListItem>>(R.id.months_list)
            monthsScrollSpinner.setItems(
                NumberListItem.createSequentialList(0, 24).asReversed()
            )

            MaterialAlertDialogBuilder(it)
                .setTitle("Set Frequency")
                .setView(view)
                .setPositiveButton("Confirm") { _, _ ->
                    val days = daysScrollSpinner.getSelectedItem()?.num ?: 0
                    val months = monthsScrollSpinner.getSelectedItem()?.num ?: 0
                    listener.onDialogConfirm(this, months, days)
                    dialog?.dismiss()
                }.setNegativeButton("Cancel") { _, _ ->
                    dialog?.cancel()
                }.create()
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