package com.example.ecommerceapp.UI.fragments.Shooping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Adapters.CartProductAdapter
import com.example.ecommerceapp.Firebase.FirebaseCommon
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.Util.VerticalItemDecoration
import com.example.ecommerceapp.ViewModel.CartViewModel
import com.example.ecommerceapp.databinding.FragmentCartBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        var totalPrice = 0f

        // TODO: in here i need to update the total price
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }

        cartAdapter.onProductClick = {
            val bundle = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(
                R.id.action_cartFragment_to_productsDetailsFragment,
                bundle
            )
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinesClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        // TODO: this is to ask if you need to delete product
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                // in here i need to show dialog
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from Cart")
                    setMessage("Do you want to delete this item from your Cart?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){dialog, _ ->
                        // TODO: i need to call delete function
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        // this is when run this fragment run this function
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        // TODO: i need to check the product if is empty or not
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        binding.buttonCheckOut.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckOut.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckOut.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration()) // this is to make spacing between items
        }
    }
}