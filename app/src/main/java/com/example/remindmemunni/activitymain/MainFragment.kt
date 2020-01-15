package com.example.remindmemunni.activitymain

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.remindmemunni.R
import com.example.remindmemunni.activitynew.NewItemActivity
import com.example.remindmemunni.activityseries.SeriesActivity


class MainFragment : Fragment() {

    private lateinit var itemPagerAdapter: ItemPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        itemPagerAdapter = ItemPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = itemPagerAdapter
    }

    fun startNewActivityByPage() {
        val intent = when (viewPager.currentItem) {
            // TODO: Placeholder activities. Replace with new item/series activity when complete.
            ItemPagerAdapter.POS_ITEMS -> Intent(activity, NewItemActivity::class.java)
            ItemPagerAdapter.POS_SERIES -> Intent(activity, NewItemActivity::class.java)
            else -> null
        }
        startActivity(intent)
    }
}
