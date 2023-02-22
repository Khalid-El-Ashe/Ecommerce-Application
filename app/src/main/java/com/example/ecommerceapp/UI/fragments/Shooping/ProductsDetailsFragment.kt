package com.example.ecommerceapp.UI.fragments.Shooping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Adapters.ColorsAdapter
import com.example.ecommerceapp.Adapters.SizesAdapter
import com.example.ecommerceapp.Adapters.ViewPager2Images
import com.example.ecommerceapp.Data.CartProduct
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.DetailsViewModel
import com.example.ecommerceapp.databinding.FragmentProductDetailsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint // this for dagger hilt
class ProductsDetailsFragment : Fragment() {
    private val args by navArgs<ProductsDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRecyclerView()
        setupColorsRecyclerView()
        setupViewpager()

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty()) {
                tvProductColor.visibility = View.INVISIBLE
            } else if (product.sizes.isNullOrEmpty()) {
                tvProductSize.visibility = View.INVISIBLE
            }
        }

        sizesAdapter.onItemSelected = {
            selectedSize = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        Snackbar.make(requireView(), "success add to cart", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is Resource.Error -> {
                        binding.buttonAddToCart.stopAnimation()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        viewPagerAdapter.differ.submitList(product.images)// add the images into view pager
        // the product colors if is not null
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }
        product.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }

        binding.imgClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViewpager() {
        binding.apply {
            viewpagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRecyclerView() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRecyclerView() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}