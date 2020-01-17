package com.example.remindmemunni.activitynew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.example.remindmemunni.R
import java.time.LocalDateTime

class NewItemActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var nameEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var costTypeSpinner: Spinner
    private lateinit var timeEditText: EditText

    private val time = PrimitiveDateTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        setSupportActionBar(findViewById(R.id.toolbar_new))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cost_types_array, R.layout.support_simple_spinner_dropdown_item)
        costTypeSpinner = findViewById(R.id.cost_type_spinner)
        costTypeSpinner.adapter = spinnerAdapter

        nameEditText = findViewById(R.id.name_input_field)
        costEditText = findViewById(R.id.cost_input_field)

        timeEditText = findViewById(R.id.time_input_field)
        timeEditText.setOnClickListener {
            val timeDialog = TimePickerFragment()
            timeDialog.show(supportFragmentManager, "time_dialog")
            val dateDialog = DatePickerFragment()
            dateDialog.show(supportFragmentManager, "date_dialog")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }

        R.id.done_button -> {
            // TODO: Validate input. Create and finish activity if valid. Complain otherwise.
            finish()
            true
        }

        else -> {
            Log.d("Nice", "NewActivity unknown button press: ${item?.itemId}")
            super.onOptionsItemSelected(item)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        time.mHour = hourOfDay
        time.mMinute = minute
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        time.mYear = year
        time.mMonth = month
        time.mDayOfMonth = dayOfMonth
    }

    private class PrimitiveDateTime(
        var mYear: Int = 0,
        var mMonth: Int = 0,
        var mDayOfMonth: Int = 0,
        var mHour: Int = 0,
        var mMinute: Int = 0
    ) {
        fun toLocalDateTime(): LocalDateTime? {
            if (isZero()) {
                return null
            }
            return LocalDateTime.of(mYear, mMonth, mDayOfMonth, mHour, mMinute)
        }
        fun isZero(): Boolean = (mYear == 0 && mMonth == 0 && mDayOfMonth == 0 && mHour == 0 && mMinute == 0)
    }
}
