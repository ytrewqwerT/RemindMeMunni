package com.example.remindmemunni.activitynew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.R
import com.example.remindmemunni.activitymain.ItemViewModel
import com.example.remindmemunni.database.Item
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NewItemActivity
    : AppCompatActivity()
    , TimePickerDialog.OnTimeSetListener
    , DatePickerDialog.OnDateSetListener
    , AdapterView.OnItemSelectedListener {

    private lateinit var nameEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var costTypeSpinner: Spinner
    private lateinit var timeEditText: EditText

    private var costIsDebit: Boolean = false
    private var time: PrimitiveDateTime? = null
    private var tempTime: PrimitiveDateTime?  = null // So that time isn't modified when date is set, but time cancelled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        setSupportActionBar(findViewById(R.id.toolbar_new))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cost_types_array, R.layout.support_simple_spinner_dropdown_item)
        costTypeSpinner = findViewById(R.id.cost_type_spinner)
        costTypeSpinner.adapter = spinnerAdapter
        costTypeSpinner.onItemSelectedListener = this

        nameEditText = findViewById(R.id.name_input_field)
        costEditText = findViewById(R.id.cost_input_field)

        timeEditText = findViewById(R.id.time_input_field)
        timeEditText.setOnClickListener {
            tempTime = PrimitiveDateTime()
            val dateDialog = DatePickerFragment()
            dateDialog.show(supportFragmentManager, "date_dialog")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean  {
        when (menuItem?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.done_button -> {
                return if (nameEditText.text.isNullOrEmpty()) {
                    val toast = Toast.makeText(applicationContext, "Item needs a name!", Toast.LENGTH_SHORT)
                    toast.show()
                    false
                } else {
                    val item = createItem()
                    Log.d("Nice", "$item")
                    val viewModel = ViewModelProvider(this)[ItemViewModel::class.java]
                    viewModel.insert(item)

                    finish()
                    true
                }
            }

            else -> {
                Log.d("Nice", "NewActivity unknown button press: ${menuItem?.itemId}")
                return super.onOptionsItemSelected(menuItem)
            }
        }
    }

    private fun createItem(): Item {
        val name: String = nameEditText.text.toString()
        val costText = costEditText.text.toString()
        var cost = if (costText.isNotEmpty()) costText.toDouble() else 0.0
        if (costIsDebit) cost = -cost
        val localDateTime: LocalDateTime? = time?.toLocalDateTime()
        val epochTime =
            localDateTime?.atZone(ZoneId.systemDefault())?.toEpochSecond() ?: 0

        return Item(name = name, cost = cost, time = epochTime)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        tempTime!!.mYear = year
        tempTime!!.mMonth = month
        tempTime!!.mDayOfMonth = dayOfMonth

        val timeDialog = TimePickerFragment()
        timeDialog.show(supportFragmentManager, "time_dialog")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        tempTime!!.mHour = hourOfDay
        tempTime!!.mMinute = minute

        time = tempTime
        val retrievedTime = time?.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yy")
        timeEditText.setText(retrievedTime?.format(formatter))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        costIsDebit = when (parent?.getItemAtPosition(position)?.equals("Debit")) {
            true -> true
            else -> false

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        costIsDebit = false
    }

    private class PrimitiveDateTime {
        var mYear: Int = 0
        var mMonth: Int = 0
        var mDayOfMonth: Int = 0
        var mHour: Int = 0
        var mMinute: Int = 0

        fun toLocalDateTime(): LocalDateTime?  = when (mYear) {
            0 -> null
            else -> LocalDateTime.of(mYear, mMonth+1, mDayOfMonth, mHour, mMinute)
        }
    }
}
