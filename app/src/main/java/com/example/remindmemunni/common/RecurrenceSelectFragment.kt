package com.example.remindmemunni.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.remindmemunni.R
import com.example.remindmemunni.utils.NumberListItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecurrenceSelectFragment(
    private val listener: RecurrenceSelectListener
): DialogFragment() {

    interface RecurrenceSelectListener {
        fun onDialogConfirm(dialog: DialogFragment, months: Int, days: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val view = activity.layoutInflater.inflate(R.layout.dialog_frequency, null)

            val daysScrollSpinner =
                view.findViewById<ScrollSpinner<NumberListItem>>(R.id.days_list)
            daysScrollSpinner.setItems(
                NumberListItem.createSequentialList(0, DAYS_MAX).asReversed()
            )

            val monthsScrollSpinner =
                view.findViewById<ScrollSpinner<NumberListItem>>(R.id.months_list)
            monthsScrollSpinner.setItems(
                NumberListItem.createSequentialList(0, MONTHS_MAX).asReversed()
            )

            MaterialAlertDialogBuilder(activity)
                .setTitle(getString(R.string.set_frequency))
                .setView(view)
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    val days = daysScrollSpinner.getSelectedItem()?.num ?: 0
                    val months = monthsScrollSpinner.getSelectedItem()?.num ?: 0
                    listener.onDialogConfirm(this, months, days)
                    dialog?.dismiss()
                }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    dialog?.cancel()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        private const val DAYS_MAX = 31
        private const val MONTHS_MAX = 24
    }
}