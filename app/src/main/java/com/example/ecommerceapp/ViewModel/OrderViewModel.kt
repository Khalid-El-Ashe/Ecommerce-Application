package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.order.Order
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    // i need to create function to add order
    fun placeOrder(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }
        firestore.runBatch { batch ->
            // TODO: Add the order into user-orders collection
            firestore.collection("user").document(firebaseAuth.uid!!).collection("orders")
                .document().set(order)

            // TODO: Add the order into orders collection
            firestore.collection("orders").document().set(order)

            // TODO: Delete the orders from user-cart collection
            firestore.collection("user").document(firebaseAuth.uid!!).collection("cart").get()
                .addOnSuccessListener {
                    it.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}