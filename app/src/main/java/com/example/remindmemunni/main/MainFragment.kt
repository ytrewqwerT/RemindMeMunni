package com.example.remindmemunni.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.remindmemunni.R
import com.example.remindmemunni.common.ItemPagerAdapter
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.databinding.FragmentMainBinding
import com.example.remindmemunni.newseries.NewSeriesFragment
import com.example.remindmemunni.utils.InjectorUtils
import com.example.remindmemunni.utils.toStringTrimmed
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null
    private val viewModel: MainViewModel by viewModels {
        InjectorUtils.provideMainViewModelFactory(requireContext())
    }

    private lateinit var itemPagerAdapter: ItemPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForFragmentResults()
        itemPagerAdapter = ItemPagerAdapter(this)
        binding?.let { binding ->
            binding.pager.adapter = itemPagerAdapter
            TabLayoutMediator(binding.pagerTabs, binding.pager) { tab, position ->
                tab.text = when (position) {
                    ItemPagerAdapter.POS_PAST_ITEMS -> "Overdue"
                    ItemPagerAdapter.POS_FUTURE_ITEMS -> "Upcoming"
                    ItemPagerAdapter.POS_SERIES -> "Series"
                    else -> "???"
                }
            }.attach()

            binding.endpointSlider.addOnChangeListener { _, value, _ ->
                viewModel.monthsOffset = value.toInt()
            }

            binding.curMunniText.setOnKeyListener { _, keyCode, _ ->
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        viewModel.setMunni(binding.curMunniText.text.toString().toDoubleOrNull())

                        binding.curMunniText.clearFocus()
                        val imm = context?.getSystemService(
                            Context.INPUT_METHOD_SERVICE
                        ) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(binding.curMunniText.windowToken, 0)

                        true
                    }
                    else -> false
                }
            }

            viewModel.curMunni.observe(viewLifecycleOwner) {
                activity?.title = "Current: \$${it.toStringTrimmed()}"
                binding.curMunniText.setText(it.toStringTrimmed())
                viewModel.updateMunniCalc()
            }

            viewModel.allItems.observe(viewLifecycleOwner) { viewModel.updateMunniCalc() }
            viewModel.allSeries.observe(viewLifecycleOwner) { viewModel.updateMunniCalc() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
                viewModel.filterText.value = newText
                return true
            }

        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_button -> {
            when (binding?.pager?.currentItem) {
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
                    "MainActivity",
                    "No add action associated to PagerAdapter page ${binding?.pager?.currentItem}"
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