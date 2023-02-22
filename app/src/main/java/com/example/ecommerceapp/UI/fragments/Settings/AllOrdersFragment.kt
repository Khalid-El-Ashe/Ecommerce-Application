package com.example.ecommerceapp.UI.fragments.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Adapters.AllOrdersAdapter
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.AllOrdersViewModel
import com.example.ecommerceapp.databinding.FragmentOrdersBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllOrdersFragment: Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val viewModel by viewModels<AllOrdersViewModel>()
    private val allOrdersAdapter by lazy { AllOrdersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrdersRecyclerView()
        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        allOrdersAdapter.differ.submitList(it.data) // TODO: put the list in your adapter
                        // TODO: i need to check the data if is it null show text empty
                        if (it.data!!.isNullOrEmpty()){
                            binding.tvEmptyOrder.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        // TODO: in here i need to overRide the onClick the item and share the data
        allOrdersAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(it)
            findNavController().navigate(action)
        }

    }

    private fun setupOrdersRecyclerView() {
        binding.rvAllOrders.apply {
            adapter = allOrdersAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }
    }
}