package com.example.remindmemunni.destinations.item

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.example.remindmemunni.R
import com.example.remindmemunni.data.Item
import com.example.remindmemunni.databinding.FragmentItemBinding
import com.example.remindmemunni.utils.InjectorUtils

class ItemFragment : Fragment() {
    private val viewModel: ItemViewModel by viewModels {
        InjectorUtils.provideItemViewModelFactory(requireContext(), itemId)
    }

    private var itemId: Int = 0
    private var binding: FragmentItemBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        itemId = arguments?.getInt(EXTRA_ITEM_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemBinding.inflate(inflater, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.name.observe(viewLifecycleOwner) { activity?.title = it }
        hideWhenEmpty(view.findViewById(R.id.series), viewModel.series)
        hideWhenEmpty(view.findViewById(R.id.category), viewModel.category)
        hideWhenEmpty(view.findViewById(R.id.cost), viewModel.cost)
        hideWhenEmpty(view.findViewById(R.id.time), viewModel.time)
        hideWhenEmpty(view.findViewById(R.id.notify), viewModel.time)

        view.findViewById<View>(R.id.series).setOnClickListener {
            viewModel.item?.seriesId?.let {
                view.findNavController().navigate(
                    ItemFragmentDirections.actionItemFragmentToSeriesFragment(it)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.item_finish -> {
            val finishItem = viewModel.item ?: Item()
            setFragmentResult(REQUEST_RESULT, bundleOf(RESULT_FINISH to finishItem))
            view?.findNavController()?.popBackStack()
            true
        }
        R.id.item_edit -> {
            val editItem = viewModel.item ?: Item()
            view?.findNavController()?.navigate(
                ItemFragmentDirections.actionItemFragmentToNewItemFragment(editItem)
            )
            true
        }
        R.id.item_delete -> {
            val deleteItem = viewModel.item ?: Item()
            setFragmentResult(REQUEST_RESULT, bundleOf(RESULT_DELETE to deleteItem))
            view?.findNavController()?.popBackStack()
            true
        }
        else -> false
    }

    private fun hideWhenEmpty(view: View, source: LiveData<String>) {
        source.observe(viewLifecycleOwner) {
            view.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    companion object {
        const val REQUEST_RESULT = "ITEM_FRAGMENT_RESULT_KEY"
        const val RESULT_FINISH = "RESULT_FINISH"
        const val RESULT_DELETE = "RESULT_DELETE"
        const val EXTRA_ITEM_ID = "ITEM_ID"
    }
}