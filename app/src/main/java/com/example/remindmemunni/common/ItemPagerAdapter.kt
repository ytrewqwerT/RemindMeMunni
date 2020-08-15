package com.example.remindmemunni.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.remindmemunni.itemslist.ItemsFragment
import com.example.remindmemunni.serieslist.SeriesFragment
import com.example.remindmemunni.utils.PrimitiveDateTime
import java.time.LocalDateTime

class ItemPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            POS_PAST_ITEMS -> overdueItemsFragment()
            POS_FUTURE_ITEMS -> upcomingItemsFragment()
            POS_SERIES -> seriesFragment()
            else -> upcomingItemsFragment()
        }
    }

    private fun overdueItemsFragment() = ItemsFragment().apply {
        val nowEpoch = PrimitiveDateTime.fromLocalDateTime(LocalDateTime.now())
            .toEpoch()
        val bundle = Bundle()
        bundle.putLong(ItemsFragment.EXTRA_UPPER_TIME_BOUND, nowEpoch)
        arguments = bundle
    }

    private fun upcomingItemsFragment() = ItemsFragment().apply {
        val nowEpoch = PrimitiveDateTime.fromLocalDateTime(LocalDateTime.now())
            .toEpoch()
        val bundle = Bundle()
        bundle.putLong(ItemsFragment.EXTRA_LOWER_TIME_BOUND, nowEpoch)
        arguments = bundle
    }

    private fun seriesFragment() = SeriesFragment()

//    fun setFilter(filterText: String?) {
//        overdueItemsFragment.setFilter(filterText)
//        upcomingItemsFragment.setFilter(filterText)
//        seriesFragment.setFilter(filterText)
//    }

    companion object {
        const val NUM_PAGES = 3

        const val POS_PAST_ITEMS = 0
        const val POS_FUTURE_ITEMS = 1
        const val POS_SERIES = 2
    }
}