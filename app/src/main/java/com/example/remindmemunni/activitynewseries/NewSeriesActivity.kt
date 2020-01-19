package com.example.remindmemunni.activitynewseries

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.remindmemunni.R
import com.example.remindmemunni.activitymain.ItemViewModel
import com.example.remindmemunni.database.Series

class NewSeriesActivity : AppCompatActivity() {

    private val viewModel: ItemViewModel by lazy {
        ViewModelProvider(this)[ItemViewModel::class.java]
    }

    private val nameEditText by lazy { findViewById<EditText>(R.id.name_input_field) }
    private val costEditText by lazy { findViewById<EditText>(R.id.cost_input_field) }
    private val typeSpinner by lazy { findViewById<AutoCompleteTextView>(R.id.cost_type_dropdown) }

    private var costIsDebit: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_series)

        title = "New Series"

        setSupportActionBar(findViewById(R.id.toolbar_new))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val typeSpinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.cost_types_array, R.layout.dropdown_menu_popup_item
        )
        typeSpinner.setAdapter(typeSpinnerAdapter)
        typeSpinner.setOnItemClickListener { _, _, position, _ ->
            costIsDebit = typeSpinnerAdapter.getItem(position) == "Debit"
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
                    Toast.makeText(
                        applicationContext,
                        "Series needs a name!",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                } else {
                    val series = createSeries()
                    Log.d("Nice", "$series")
                    viewModel.insert(series)

                    finish()
                    true
                }
            }

            else -> return super.onOptionsItemSelected(menuItem)
        }
    }

    private fun createSeries(): Series {
        val name: String = nameEditText.text.toString()
        val costText = costEditText.text.toString()
        var cost = if (costText.isNotEmpty()) costText.toDouble() else 0.0
        if (costIsDebit) cost = -cost

        return Series(name = name, cost = cost)
    }
}
