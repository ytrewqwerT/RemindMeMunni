package com.example.remindmemunni.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.remindmemunni.fragments.ItemsFragment
import com.example.remindmemunni.fragments.SeriesFragment
import com.example.remindmemunni.utils.PrimitiveDateTime
import java.time.LocalDateTime

class ItemPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val overdueItemsFragment by lazy {
        ItemsFragment().apply {
            val nowEpoch = PrimitiveDateTime.fromLocalDateTime(LocalDateTime.now())
                .toEpoch()
            val bundle = Bundle()
            bundle.putLong(ItemsFragment.EXTRA_UPPER_TIME_BOUND, nowEpoch)
            arguments = bundle
        }
    }
    private val upcomingItemsFragment by lazy {
        ItemsFragment().apply {
            val nowEpoch = PrimitiveDateTime.fromLocalDateTime(LocalDateTime.now())
                .toEpoch()
            val bundle = Bundle()
            bundle.putLong(ItemsFragment.EXTRA_LOWER_TIME_BOUND, nowEpoch)
            arguments = bundle
        }
    }
    private val seriesFragment by lazy { SeriesFragment() }

    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): Fragment {
        return when (position) {
            POS_PAST_ITEMS -> overdueItemsFragment
            POS_FUTURE_ITEMS -> upcomingItemsFragment
            POS_SERIES -> seriesFragment
            else -> upcomingItemsFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        POS_PAST_ITEMS -> "Overdue"
        POS_FUTURE_ITEMS -> "Upcoming"
        POS_SERIES -> "Series"
        else -> "???"
    }

    fun setFilter(filterText: String?) {
        overdueItemsFragment.setFilter(filterText)
        upcomingItemsFragment.setFilter(filterText)
        seriesFragment.setFilter(filterText)
    }

    companion object {
        const val NUM_PAGES = 3

        const val POS_PAST_ITEMS = 0
        const val POS_FUTURE_ITEMS = 1
        const val POS_SERIES = 2
    }
}