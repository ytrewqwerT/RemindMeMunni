package com.example.remindmemunni.main

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ItemPagerAdapter
import com.example.remindmemunni.databinding.ActivityMainBinding
import com.example.remindmemunni.newitem.NewItemActivity
import com.example.remindmemunni.newseries.NewSeriesActivity
import com.example.remindmemunni.series.SeriesActivity
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.utils.toStringTrimmed
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        InjectorUtils.provideMainViewModelFactory(applicationContext)
    }
    private lateinit var binding: ActivityMainBinding
    private val itemPagerAdapter by lazy {
        ItemPagerAdapter(
            supportFragmentManager
        )
    }
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = itemPagerAdapter
        // Force-attaches all pages to activity to allow all-page search functionality
        viewPager.offscreenPageLimit = ItemPagerAdapter.NUM_PAGES

        val endPointSlider = findViewById<Slider>(R.id.endpoint_slider)
        endPointSlider.addOnChangeListener { _, value, _ ->
            viewModel.monthsOffset = value.toInt()
        }

        val munniEditText = findViewById<EditText>(R.id.cur_munni_text)
        munniEditText.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KEYCODE_ENTER -> {
                    viewModel.curMunni.value = munniEditText.text.toString().toDoubleOrNull()
                    munniEditText.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(munniEditText.windowToken, 0)

                    true
                }
                else -> false
            }
        }

        viewModel.curMunni.observe(this, Observer {
            title = getString(R.string.app_name) + ": \$${it.toStringTrimmed()}"
            munniEditText.setText(it.toStringTrimmed())
            viewModel.updateMunniCalc()
        })
        
        viewModel.allItems.observe(this, Observer { viewModel.updateMunniCalc() })
        viewModel.allSeries.observe(this, Observer { viewModel.updateMunniCalc() })

        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.search_button).actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.queryHint = getString(R.string.filter_hint)
        searchView.maxWidth = 900

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemPagerAdapter.setFilter(newText)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.add_button -> {
            when (viewPager.currentItem) {
                ItemPagerAdapter.POS_PAST_ITEMS -> {
                    val intent = Intent(this, NewItemActivity::class.java)
                    startActivity(intent)
                }
                ItemPagerAdapter.POS_FUTURE_ITEMS -> {
                    val intent = Intent(this, NewItemActivity::class.java)
                    startActivity(intent)
                }
                ItemPagerAdapter.POS_SERIES -> {
                    val intent = Intent(this, NewSeriesActivity::class.java)
                    startActivityForResult(intent,
                        NEW_SERIES_REQUEST
                    )
                }
                else -> Log.w(
                    "MainActivity",
                    "No add action associated to PagerAdapter page ${viewPager.currentItem}"
                )
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            NEW_SERIES_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val newSeriesId = data?.getIntExtra(NewSeriesActivity.EXTRA_SERIES_ID, 0) ?: 0
                    if (newSeriesId != 0) {
                        val intent = Intent(this, SeriesActivity::class.java)
                        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, newSeriesId)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val id = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val NEW_SERIES_REQUEST = 1
    }
}