package com.example.remindmemunni.common

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minute = c.get(Calendar.MINUTE)

        if (arguments?.getBoolean(EXTRA_HAS_TIME) == true) {
            hour = requireArguments().getInt(EXTRA_HOUR, 0)
            minute = requireArguments().getInt(EXTRA_MINUTE, 0)
        }

        val parentListener = parentFragment as? TimePickerDialog.OnTimeSetListener
        return TimePickerDialog(context, parentListener, hour, minute, false)
    }

    companion object {
        const val EXTRA_HAS_TIME= "DATED"
        const val EXTRA_HOUR = "YEAR"
        const val EXTRA_MINUTE = "MONTH"
    }
}
