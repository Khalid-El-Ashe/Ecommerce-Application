package com.example.ecommerceapp.UI.fragments.Categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Adapters.BestProductAdapter
import com.example.ecommerceapp.databinding.FragmentBaseCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductAdapter by lazy { BestProductAdapter() } // by lazy : to don't make object of this class
    protected val bestProductsAdapter: BestProductAdapter by lazy { BestProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRecyclerView()
        setupBestProductsRecyclerView()

        // i need to click the item from adapter
        offerAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
        }

        // i need to click the item from adapter
        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // i need to check the scrolling
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                // i need to use the fun update products
                onBestProductPagingRequest()
            }
        })
    }

    fun showOfferLoading() {
        binding.offerProductsPagersBar.visibility = View.VISIBLE
    }

    fun hideOfferLoading() {
        binding.offerProductsPagersBar.visibility = View.GONE
    }

    fun showBestLoading() {
        binding.bestProductsPagersBar.visibility = View.VISIBLE
    }

    fun hideBestLoading() {
        binding.bestProductsPagersBar.visibility = View.GONE
    }

    open fun onOfferPagingRequest() {}
    open fun onBestProductPagingRequest() {}

    private fun setupBestProductsRecyclerView() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupOfferRecyclerView() {
        binding.rvOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }
}