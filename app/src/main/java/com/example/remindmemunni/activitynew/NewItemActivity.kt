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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.R
import com.example.remindmemunni.activitymain.ItemViewModel
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.Item
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NewItemActivity
    : AppCompatActivity()
    , TimePickerDialog.OnTimeSetListener
    , DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: ItemViewModel

    private lateinit var nameEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var costTypeSpinner: AutoCompleteTextView
    private lateinit var timeEditText: EditText
    private lateinit var seriesSpinner: AutoCompleteTextView

    private var time: PrimitiveDateTime? = null
    private var tempTime: PrimitiveDateTime?  = null // So that time isn't modified when date is set, but time cancelled
    private var costIsDebit: Boolean = true
    private var selectedSeries: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        setSupportActionBar(findViewById(R.id.toolbar_new))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[ItemViewModel::class.java]

        nameEditText = findViewById(R.id.name_input_field)
        costEditText = findViewById(R.id.cost_input_field)
        costTypeSpinner = findViewById(R.id.cost_type_dropdown)
        timeEditText = findViewById(R.id.time_input_field)
        seriesSpinner = findViewById(R.id.series_dropdown)

        val costTypeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cost_types_array, R.layout.dropdown_menu_popup_item)
        costTypeSpinner.setAdapter(costTypeSpinnerAdapter)
        costTypeSpinner.setOnItemClickListener { _, _, position, _ ->
            costIsDebit = costTypeSpinnerAdapter.getItem(position) == "Debit"
        }

        timeEditText.setOnClickListener {
            tempTime = PrimitiveDateTime()
            val dateDialog = DatePickerFragment()
            dateDialog.show(supportFragmentManager, "date_dialog")
        }

        val seriesSpinnerAdapter = ArrayAdapter<AggregatedSeries>(this, R.layout.dropdown_menu_popup_item, ArrayList())
        seriesSpinner.setAdapter(seriesSpinnerAdapter)
        seriesSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedSeries = seriesSpinnerAdapter.getItem(position)?.series?.id ?: 0
        }
        // Use observer to populate adapter since LiveData starts off empty for some reason...
        viewModel.allSeries.observe(this, Observer {series ->
            seriesSpinnerAdapter.clear()
            seriesSpinnerAdapter.addAll(series)
        })
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
                    viewModel.insert(item)

                    finish()
                    true
                }
            }

            else -> return super.onOptionsItemSelected(menuItem)
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

        return Item(name = name, seriesId = selectedSeries, cost = cost, time = epochTime)
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
