package com.example.remindmemunni.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        if (arguments?.getBoolean(EXTRA_HAS_DATE) == true) {
            year = requireArguments().getInt(EXTRA_YEAR, 0)
            month = requireArguments().getInt(EXTRA_MONTH, 0)
            day = requireArguments().getInt(EXTRA_DAY_OF_MONTH, 0)
        }

        val parentListener = parentFragment as? DatePickerDialog.OnDateSetListener
        return DatePickerDialog(requireContext(), parentListener, year, month, day)
    }

    companion object {
        const val EXTRA_HAS_DATE = "DATED"
        const val EXTRA_YEAR = "YEAR"
        const val EXTRA_MONTH = "MONTH"
        const val EXTRA_DAY_OF_MONTH = "DAY_OF_MONTH"
    }
}
