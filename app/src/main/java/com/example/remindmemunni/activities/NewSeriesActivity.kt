package com.example.remindmemunni.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.remindmemunni.R
import com.example.remindmemunni.adapters.UnfilteredArrayAdapter
import com.example.remindmemunni.databinding.ActivityNewSeriesBinding
import com.example.remindmemunni.fragments.RecurrenceSelectFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.viewmodels.NewSeriesViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class NewSeriesActivity : AppCompatActivity(),
    RecurrenceSelectFragment.RecurrenceSelectListener {

    private lateinit var binding: ActivityNewSeriesBinding
    private val viewModel: NewSeriesViewModel by viewModels {
        InjectorUtils.provideNewSeriesViewModelFactory(this, seriesId)
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
            RecurrenceSelectFragment()
                .show(supportFragmentManager, "frequency_dialog")
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
            lifecycleScope.launch {
                val newSeriesId = viewModel.createSeries()
                if (newSeriesId == 0) {
                    Toast.makeText(applicationContext, viewModel.validateInput(), Toast.LENGTH_SHORT).show()
                } else {
                    val resultIntent = Intent()
                    resultIntent.putExtra(EXTRA_SERIES_ID, newSeriesId)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
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
