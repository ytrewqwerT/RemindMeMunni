package com.example.remindmemunni.series

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.remindmemunni.R
import com.example.remindmemunni.itemslist.ItemsFragment
import com.example.remindmemunni.newitem.NewItemActivity
import com.example.remindmemunni.utils.InjectorUtils

class SeriesFragment : Fragment() {

    private val viewModel: SeriesViewModel by viewModels {
        InjectorUtils.provideSeriesViewModelFactory(requireContext(), seriesId)
    }

    private var seriesId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seriesId = arguments?.getInt(EXTRA_SERIES_ID, 0) ?: 0

        viewModel.series.observe(viewLifecycleOwner) { activity?.title = it?.series?.name }

        val recurrenceTextView = view.findViewById<TextView>(R.id.subtitle)
        viewModel.series.observe(viewLifecycleOwner) {
            val series = it?.series
            var text = series?.getCostString()
            text += if (text?.isNotEmpty() == true) " repeating " else "Repeats "

            val recurrenceText = series?.getRecurrenceString()
            text += if (recurrenceText?.isNotEmpty() == true) "every $recurrenceText" else "never"
            recurrenceTextView.text = text
        }

        childFragmentManager.commit { add(R.id.series_list_fragment, ItemsFragment(seriesId)) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.search_button)?.isVisible = false
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId){
        R.id.add_button -> {
            val intent = Intent(context, NewItemActivity::class.java)
            intent.putExtra(NewItemActivity.EXTRA_SERIES_ID, seriesId)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    companion object {
        const val EXTRA_SERIES_ID = "SERIES_ID"
    }
}