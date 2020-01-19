package com.example.remindmemunni.activitymain

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.remindmemunni.R
import com.example.remindmemunni.activitynewitem.NewItemActivity
import com.example.remindmemunni.activitynewseries.NewSeriesActivity

class MainFragment : Fragment() {

    private val itemPagerAdapter: ItemPagerAdapter by lazy { ItemPagerAdapter(childFragmentManager) }
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = itemPagerAdapter
    }

    fun startNewActivityByPage() {
        val intent = when (viewPager.currentItem) {
            ItemPagerAdapter.POS_ITEMS -> Intent(activity, NewItemActivity::class.java)
            ItemPagerAdapter.POS_SERIES -> Intent(activity, NewSeriesActivity::class.java)
            else -> null
        }
        startActivity(intent)
    }
}
