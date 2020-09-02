package com.example.remindmemunni.destinations.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.remindmemunni.MainViewModel
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ItemPagerAdapter
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.destinations.newseries.NewSeriesFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels {
        InjectorUtils.provideMainViewModelFactory(requireContext())
    }

    private lateinit var itemPagerAdapter: ItemPagerAdapter
    private var pager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForFragmentResults()

        itemPagerAdapter = ItemPagerAdapter(this)
        val pagerTabs = view.findViewById<TabLayout>(R.id.pager_tabs)
        pager = view.findViewById(R.id.pager)
        pager!!.adapter = itemPagerAdapter
        TabLayoutMediator(pagerTabs, pager!!) { tab, position ->
            tab.text = when (position) {
                ItemPagerAdapter.POS_PAST_ITEMS -> "Overdue"
                ItemPagerAdapter.POS_FUTURE_ITEMS -> "Upcoming"
                ItemPagerAdapter.POS_SERIES -> "Series"
                else -> "???"
            }
        }.apply { attach() }

        mainViewModel.categoryFilter.observe(viewLifecycleOwner) {
            activity?.title = when(it) {
                null -> MainViewModel.CATEGORY_ALL
                "" -> MainViewModel.CATEGORY_NONE
                else -> it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pager = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.search_button).actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.queryHint = getString(R.string.filter_hint)
        searchView.maxWidth = 900

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.searchFilter.value = newText
                return true
            }

        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_button -> {
            when (pager?.currentItem) {
                ItemPagerAdapter.POS_PAST_ITEMS, ItemPagerAdapter.POS_FUTURE_ITEMS -> {
                    val action = MainFragmentDirections
                        .actionMainFragmentToNewItemFragment(Item())
                    view?.findNavController()?.navigate(action)

                }
                ItemPagerAdapter.POS_SERIES -> {
                    val action = MainFragmentDirections
                        .actionMainFragmentToNewSeriesFragment()
                    view?.findNavController()?.navigate(action)
                }
                else -> Log.w(
                    "MainFragment",
                    "No add action associated to PagerAdapter page ${pager?.currentItem}"
                )
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun listenForFragmentResults() {
        setFragmentResultListener(NewSeriesFragment.REQUEST_RESULT) { _, result ->
            val newSeriesId = result.getInt(NewSeriesFragment.EXTRA_SERIES_ID, 0)
            if (newSeriesId != 0) {
                val action = MainFragmentDirections
                    .actionMainFragmentToSeriesFragment(newSeriesId)
                view?.findNavController()?.navigate(action)
            }
        }
    }
}