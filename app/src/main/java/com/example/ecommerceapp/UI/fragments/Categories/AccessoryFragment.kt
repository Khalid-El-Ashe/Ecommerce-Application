package com.example.ecommerceapp.UI.fragments.Categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceapp.Data.Category
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.CategoryViewModel
import com.example.ecommerceapp.ViewModel.Factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccessoryFragment: BaseCategoryFragment() {
    // i need to inject firestore to don't make inisialize
    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewmodel by viewModels<CategoryViewModel>() {
        BaseCategoryViewModelFactory(
            firestore,
            Category.Accessory
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewmodel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        // in here i need just update
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.bestProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showBestLoading()
                    }
                    is Resource.Success -> {
                        // in here i need just update
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideBestLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductPagingRequest() {

    }

    override fun onOfferPagingRequest() {

    }
}