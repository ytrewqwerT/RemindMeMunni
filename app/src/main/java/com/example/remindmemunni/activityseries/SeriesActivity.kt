package com.example.remindmemunni.activityseries

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmemunni.R
import com.example.remindmemunni.CustomRecyclerViewAdapter
import com.example.remindmemunni.activitynewitem.NewItemActivity
import com.example.remindmemunni.database.Item

class SeriesActivity : AppCompatActivity() {

    private val viewModel: SeriesViewModel by lazy {
        ViewModelProvider(
            this, SeriesViewModel.SeriesViewModelFactory(application, seriesId)
        )[SeriesViewModel::class.java]
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        seriesId = intent.getIntExtra(EXTRA_SERIES_ID, 0)
        Log.d("Nice", "$seriesId")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = CustomRecyclerViewAdapter<Item>(null)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        Log.d("Nice", "${viewModel.series.value}")
        viewModel.series.observe(this, Observer { series ->
            title = series?.series?.name
            series?.items?.let { adapter.setItems(it) }
        })
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
            // TODO: Start NewItemActivity with series auto-set to current series
            val intent = Intent(this, NewItemActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    companion object {
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}
