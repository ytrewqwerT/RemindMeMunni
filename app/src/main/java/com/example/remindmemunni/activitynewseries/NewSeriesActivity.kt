package com.example.remindmemunni.activitynewseries

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.R
import com.example.remindmemunni.UnfilteredArrayAdapter
import com.example.remindmemunni.databinding.ActivityNewSeriesBinding
import com.google.android.material.textfield.TextInputEditText

class NewSeriesActivity : AppCompatActivity(), RecurrenceSelectFragment.RecurrenceSelectListener {

    lateinit var binding: ActivityNewSeriesBinding
    private val viewModel: NewSeriesViewModel by lazy {
        ViewModelProvider(this)[NewSeriesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_series)

        title = "New Series"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_series)
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

        val recurrenceEditText = findViewById<TextInputEditText>(R.id.repeat)
        recurrenceEditText.setOnClickListener {
            RecurrenceSelectFragment().show(supportFragmentManager, "frequency_dialog")
        }
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
                val seriesCreationResult = viewModel.createSeries()
                if (seriesCreationResult != null) {
                    Toast.makeText(
                        applicationContext,
                        seriesCreationResult,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    finish()
                }
                true
            }
            else -> return super.onOptionsItemSelected(menuItem)
        }
    }

    override fun onDialogConfirm(dialog: DialogFragment, months: Int, days: Int) {
        viewModel.setRecurrence(months, days)
    }
}
