package com.example.remindmemunni.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.example.remindmemunni.R
import com.example.remindmemunni.adapters.ItemPagerAdapter
import com.example.remindmemunni.databinding.ActivityMainBinding
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.utils.toStringTrimmed
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
            munniEditText.setText(it.toStringTrimmed())
            viewModel.updateMunniCalc()
        })
        
        viewModel.allItems.observe(this, Observer { viewModel.updateMunniCalc() })
        viewModel.allSeries.observe(this, Observer { viewModel.updateMunniCalc() })
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
