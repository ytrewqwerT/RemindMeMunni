package com.example.remindmemunni.activitynewseries

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.remindmemunni.R
import com.example.remindmemunni.UnfilteredArrayAdapter
import com.example.remindmemunni.databinding.ActivityNewSeriesBinding
import com.google.android.material.textfield.TextInputEditText

class NewSeriesActivity : AppCompatActivity(), RecurrenceSelectFragment.RecurrenceSelectListener {

    lateinit var binding: ActivityNewSeriesBinding
    private val viewModel: NewSeriesViewModel by viewModels {
        NewSeriesViewModel.NewSeriesViewModelFactory(application, seriesId)
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "New Series"

        seriesId = intent.getIntExtra(EXTRA_SERIES_ID, 0)
        if (seriesId != 0) title = "Edit Series"

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

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.done_button -> {
            val seriesCreationResult = viewModel.createSeries()
            if (seriesCreationResult != null) {
                Toast.makeText(applicationContext, seriesCreationResult, Toast.LENGTH_SHORT).show()
            } else {
                finish()
            }
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    override fun onDialogConfirm(dialog: DialogFragment, months: Int, days: Int) {
        viewModel.setRecurrence(months, days)
    }

    companion object {
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}
