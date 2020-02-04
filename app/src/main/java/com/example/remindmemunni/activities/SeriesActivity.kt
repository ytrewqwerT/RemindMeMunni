package com.example.remindmemunni.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.remindmemunni.R
import com.example.remindmemunni.fragments.ItemsFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.viewmodels.SeriesViewModel

class SeriesActivity : AppCompatActivity() {

    private val viewModel: SeriesViewModel by viewModels {
        InjectorUtils.provideSeriesViewModelFactory(this, seriesId)
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        seriesId = intent.getIntExtra(EXTRA_SERIES_ID, 0)

        viewModel.series.observe(this, Observer { series ->
            title = series?.series?.name
        })

        val recurrenceTextView = findViewById<TextView>(R.id.subtitle)
        viewModel.series.observe(this, Observer {
            val series = it.series
            var text = "\$"
            text += if (series.cost < 0) "${-series.cost}" else "${series.cost} cr"
            text += " repeating every ${series.recurMonths} month"
            if (series.recurMonths != 1) text += "s"
            text += " and ${series.recurDays} day"
            if (series.recurDays != 1) text += "s"
            recurrenceTextView.text = text
        })

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.series_list_fragment,
            ItemsFragment(seriesId)
        )
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean = when (menuItem?.itemId){
        android.R.id.home -> {
            finish()
            true
        }
        R.id.add_button -> {
            val intent = Intent(this, NewItemActivity::class.java)
            intent.putExtra(NewItemActivity.EXTRA_SERIES_ID, seriesId)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    companion object {
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}
