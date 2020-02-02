package com.example.remindmemunni.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.remindmemunni.R
import com.example.remindmemunni.adapters.UnfilteredArrayAdapter
import com.example.remindmemunni.database.AggregatedSeries
import com.example.remindmemunni.database.Series
import com.example.remindmemunni.databinding.ActivityNewItemBinding
import com.example.remindmemunni.fragments.DatePickerFragment
import com.example.remindmemunni.fragments.TimePickerFragment
import com.example.remindmemunni.utils.PrimitiveDateTime
import com.example.remindmemunni.viewmodels.NewItemViewModel

class NewItemActivity
    : AppCompatActivity()
    , TimePickerDialog.OnTimeSetListener
    , DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityNewItemBinding
    private val viewModel: NewItemViewModel by viewModels {
        NewItemViewModel.NewItemViewModelFactory(
            application,
            itemId
        )
    }

    private val time = PrimitiveDateTime()

    private var itemId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "New Item"

        itemId = intent.getIntExtra(EXTRA_ITEM_ID, 0)
        val seriesId = intent.getIntExtra(EXTRA_SERIES_ID, 0)
        val itemName = intent.getStringExtra(EXTRA_NAME)
        val itemCost = intent.getDoubleExtra(EXTRA_COST, 0.0)
        val itemTime = intent.getLongExtra(EXTRA_TIME, 0)

        // TODO: Pass item information bundle to viewModel
        // (to fix bug: Time overwritten by setSeries due to its usage of coroutines)
        Log.d("Nice", "$itemTime")
        if (itemId != 0) title = "Edit Item"
        if (seriesId != 0) viewModel.setSeries(seriesId)
        if (!itemName.isNullOrEmpty()) viewModel.name.value = itemName
        if (itemCost != 0.0) viewModel.setCost(itemCost)
        if (itemTime != 0L) viewModel.setTime(PrimitiveDateTime.fromEpoch(itemTime))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_item)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val typeSpinner = findViewById<AutoCompleteTextView>(R.id.cost_type_dropdown)
        val typeSpinnerAdapter = UnfilteredArrayAdapter.createFromResource(
            this, R.array.cost_types_array, R.layout.dropdown_menu_popup_item
        )
        typeSpinner.setAdapter(typeSpinnerAdapter)
        typeSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setCostType(typeSpinnerAdapter.getItem(position))
        }

        val timeEditText = findViewById<EditText>(R.id.time_input_field)
        timeEditText.setOnClickListener {
            DatePickerFragment()
                .show(supportFragmentManager, "date_dialog")
        }

        val seriesSpinner = findViewById<AutoCompleteTextView>(R.id.series_dropdown)
        val seriesSpinnerAdapter =
            UnfilteredArrayAdapter<AggregatedSeries>(
                this, R.layout.dropdown_menu_popup_item, ArrayList()
            )
        val dummySeries = AggregatedSeries(Series(), emptyList()) // For no series selected option
        seriesSpinner.setAdapter(seriesSpinnerAdapter)
        seriesSpinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSeries(seriesSpinnerAdapter.getItem(position))
        }
        viewModel.allSeries.observe(this, Observer {series ->
            seriesSpinnerAdapter.clear()
            seriesSpinnerAdapter.add(dummySeries)
            seriesSpinnerAdapter.addAll(series)
        })

        val checkBox = findViewById<CheckBox>(R.id.series_increment)
        viewModel.series.observe(this, Observer {
            checkBox.isEnabled = it.isNotEmpty()
            checkBox.isChecked = it.isNotEmpty()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean  = when (menuItem?.itemId) {
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        time.mYear = year
        time.mMonth = month + 1 // DatePicker returns month as 0-11 instead of 1-12
        time.mDayOfMonth = dayOfMonth
        val timeDialog =
            TimePickerFragment()
        timeDialog.show(supportFragmentManager, "time_dialog")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        time.mHour = hourOfDay
        time.mMinute = minute
        viewModel.setTime(time)
    }

    companion object {
        const val EXTRA_ITEM_ID = "ITEM_ID"
        const val EXTRA_SERIES_ID = "SERIES_ID"
        const val EXTRA_NAME = "NAME"
        const val EXTRA_COST = "COST"
        const val EXTRA_TIME = "TIME"
    }
}
