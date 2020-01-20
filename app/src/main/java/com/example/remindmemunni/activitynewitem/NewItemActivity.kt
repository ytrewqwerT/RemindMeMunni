package com.example.remindmemunni.activitynewitem

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.PrimitiveDateTime
import com.example.remindmemunni.R
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.databinding.ActivityNewItemBinding

class NewItemActivity
    : AppCompatActivity()
    , TimePickerDialog.OnTimeSetListener
    , DatePickerDialog.OnDateSetListener {

    lateinit var binding: ActivityNewItemBinding
    private val viewModel by lazy {
        ViewModelProvider(this)[NewItemViewModel::class.java]
    }

    private val timeEditText by lazy { findViewById<EditText>(R.id.time_input_field) }
    private val time = PrimitiveDateTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "New Item"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_item)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(findViewById(R.id.toolbar_new))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val typeSpinner = findViewById<AutoCompleteTextView>(R.id.cost_type_dropdown)
        val typeSpinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.cost_types_array, R.layout.dropdown_menu_popup_item
        )
        typeSpinner.setAdapter(typeSpinnerAdapter)
        typeSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setCostType(typeSpinnerAdapter.getItem(position))
        }

        timeEditText.setOnClickListener {
            val dateDialog = DatePickerFragment()
            dateDialog.show(supportFragmentManager, "date_dialog")
        }

        val seriesSpinner = findViewById<AutoCompleteTextView>(R.id.series_dropdown)
        val seriesSpinnerAdapter = ArrayAdapter<AggregatedSeries>(
            this, R.layout.dropdown_menu_popup_item, ArrayList()
        )
        seriesSpinner.setAdapter(seriesSpinnerAdapter)
        seriesSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSeries(seriesSpinnerAdapter.getItem(position)?.series)
        }
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
        return when (menuItem?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.done_button -> {
                val itemCreationResult = viewModel.createItem()
                if (itemCreationResult != null) {
                    Toast.makeText(
                        applicationContext,
                        itemCreationResult,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        time.mYear = year
        time.mMonth = month
        time.mDayOfMonth = dayOfMonth
        val timeDialog = TimePickerFragment()
        timeDialog.show(supportFragmentManager, "time_dialog")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        time.mHour = hourOfDay
        time.mMinute = minute
        timeEditText.setText(viewModel.setTime(time))
    }
}