package com.example.ecommerceapp.UI.fragments.Categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Adapters.BestDealsAdapter
import com.example.ecommerceapp.Adapters.BestProductAdapter
import com.example.ecommerceapp.Adapters.SpecialProductAdapter
import com.example.ecommerceapp.databinding.FragmentMainCategoryBinding
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.MainCategoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint // this for dagger hilt
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductAdapter: SpecialProductAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val mainCategoryViewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpecialProductRecyclerView()
        setupBestDealsRecyclerView()
        setupBestProductRecyclerView()

        // i need to click the item from adapter
        specialProductAdapter.onClick = {
            // i need to send the data when click
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
        }
        // i need to click the item from adapter
        bestDealsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
        }
        // i need to click the item from adapter
        bestProductAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
        }


        // i need to get the data, must use coroutine fun
        lifecycleScope.launchWhenStarted {
            mainCategoryViewModel.specialProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        // when success add the new data into adapter
                        specialProductAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.d("Error", it.message.toString())
                        Snackbar.make(requireView(), "something is wrong", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mainCategoryViewModel.bestDeals.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.d("Error", it.message.toString())
                        Snackbar.make(requireView(), "something is wrong", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mainCategoryViewModel.bestProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.bestProductProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                        binding.bestProductProgressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Log.d("Error", it.message.toString())
                        Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT)
                            .show()
                        binding.bestProductProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // i need to check the scrolling
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                // i need to use the fun update products
                mainCategoryViewModel.fetchBestProducts()
            }
        })

    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun setupBestProductRecyclerView() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    private fun setupBestDealsRecyclerView() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDeals.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setSpecialProductRecyclerView() {
        specialProductAdapter = SpecialProductAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
        }
    }
}
