package com.example.remindmemunni.destinations.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.example.remindmemunni.R
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun hideWhenEmpty(view: View, source: LiveData<String>) {
        source.observe(viewLifecycleOwner) {
            view.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_ITEM_ID = "ITEM_ID"
    }
}