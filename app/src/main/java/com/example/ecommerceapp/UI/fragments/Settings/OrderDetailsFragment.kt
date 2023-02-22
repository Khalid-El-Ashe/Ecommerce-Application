package com.example.ecommerceapp.UI.fragments.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Adapters.BillingProductAdapter
import com.example.ecommerceapp.Data.order.OrderStatus
import com.example.ecommerceapp.Data.order.getOrderStatus
import com.example.ecommerceapp.Util.VerticalItemDecoration
import com.example.ecommerceapp.databinding.FragmentOrderDetailBinding

class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        setupOrderRecyclerView()

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            // TODO: setSteps => this is the library you must to implement it
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipping.status,
                    OrderStatus.Delivered.status,
                )
            )

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipping -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState,false)
            // TODO: i need to check to go the last step
            if (currentOrderState == 3){
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.state} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "$ ${order.totalPrice}"

        }

        // TODO: i need to update the list
        billingProductAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRecyclerView() {
        binding.rvProducts.apply {
            adapter = billingProductAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}