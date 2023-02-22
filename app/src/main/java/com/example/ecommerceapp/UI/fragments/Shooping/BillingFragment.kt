package com.example.ecommerceapp.UI.fragments.Shooping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Adapters.AddressAdapter
import com.example.ecommerceapp.Adapters.BillingProductAdapter
import com.example.ecommerceapp.Data.Address
import com.example.ecommerceapp.Data.CartProduct
import com.example.ecommerceapp.Data.order.Order
import com.example.ecommerceapp.Data.order.OrderStatus
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Util.HorizontalItemDecoration
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.BillingViewModel
import com.example.ecommerceapp.ViewModel.OrderViewModel
import com.example.ecommerceapp.databinding.FragmentBillingBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment: Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val viewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBillingRecyclerView()
        setupAddressRecyclerView()

        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarAddresses.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddresses.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddresses.visibility = View.GONE
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.btnPlaceOlder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnPlaceOlder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Your order was placed", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        binding.btnPlaceOlder.revertAnimation()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.imgAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // i need to add the data list coming into my adapter
        billingProductAdapter.differ.submitList(products)
        binding.tvTotalprice.text = "$ $totalPrice"

        binding.imgCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        addressAdapter.onClick = {
            selectedAddress = it
        }
        binding.btnPlaceOlder.setOnClickListener {
            if (selectedAddress == null){
                Snackbar.make(requireView(),"Please select an address",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

    }

    private fun showOrderConfirmationDialog() {
        // in here i need to show dialog
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){dialog, _ ->
                // i need to call add order function
                val order = Order(OrderStatus.Ordered.status, totalPrice,products,selectedAddress!!)
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setupAddressRecyclerView() {
        binding.rvAdresses.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingRecyclerView() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL, false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}