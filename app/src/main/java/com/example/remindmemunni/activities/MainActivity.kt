package com.example.remindmemunni.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.example.remindmemunni.R
import com.example.remindmemunni.adapters.ItemPagerAdapter
import com.example.remindmemunni.databinding.ActivityMainBinding
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.viewmodels.MainViewModel
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

    private var endPointSliderValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.pager)
        viewPager.adapter = itemPagerAdapter

        val endPointSlider = findViewById<Slider>(R.id.endpoint_slider)
        endPointSlider.addOnChangeListener { _, value, _ ->
            endPointSliderValue = value.toInt()
            viewModel.updateMunniCalc(endPointSliderValue)
        }

        viewModel.allItems.observe(this, Observer {
            viewModel.updateMunniCalc(endPointSliderValue)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.add_button -> {
            val intent = when (viewPager.currentItem) {
                ItemPagerAdapter.POS_ITEMS -> Intent(this, NewItemActivity::class.java)
                ItemPagerAdapter.POS_SERIES -> Intent(this, NewSeriesActivity::class.java)
                else -> null
            }
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
