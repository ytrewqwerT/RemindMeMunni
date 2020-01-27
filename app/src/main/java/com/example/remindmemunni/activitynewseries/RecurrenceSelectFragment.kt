package com.example.remindmemunni.activitynewseries

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.remindmemunni.R
import com.example.remindmemunni.ScrollSpinner
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

            val daysScrollSpinner = view.findViewById<ScrollSpinner<NumberListItem>>(R.id.days_list)
            daysScrollSpinner.setItems(NumberListItem.createSequentialList(0, 30).asReversed())

            val monthsScrollSpinner = view.findViewById<ScrollSpinner<NumberListItem>>(R.id.months_list)
            monthsScrollSpinner.setItems(NumberListItem.createSequentialList(0, 24).asReversed())

            builder.setView(view)
            builder.setPositiveButton("Confirm") { _, _ ->
                val days = daysScrollSpinner.getSelectedItem()?.num ?: 0
                val months = monthsScrollSpinner.getSelectedItem()?.num ?: 0
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