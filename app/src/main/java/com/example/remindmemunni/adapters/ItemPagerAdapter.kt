package com.example.remindmemunni.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.remindmemunni.fragments.ItemsFragment
import com.example.remindmemunni.fragments.SeriesFragment

class ItemPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val itemsFragment by lazy { ItemsFragment() }
    private val seriesFragment by lazy { SeriesFragment() }

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            POS_ITEMS -> itemsFragment
            POS_SERIES -> seriesFragment
            else -> itemsFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        POS_ITEMS -> "Items"
        POS_SERIES -> "Series"
        else -> "???"
    }

    companion object {
        const val POS_ITEMS = 0
        const val POS_SERIES = 1
    }
}