package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.CartProduct
import com.example.ecommerceapp.Firebase.FirebaseCommon
import com.example.ecommerceapp.Helper.getProductPrice
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // this is for dagger hilt to inisalize the constructor
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProduct = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProduct.asStateFlow()
    private var cartProductDocument = emptyList<DocumentSnapshot>()

    // i need to update the total price
    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    // this function to calculate cart product price
    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    init {
        // in this block i need to call my function
        getCartProducts()
    }

    // i need to create function to get my cart product
    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProduct.emit(Resource.Loading())
        }
        // this call back to updated the data in ui
        firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
            // this is to refresh the ui when update the data from firestore database ,, that mean like listener changes collection
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cartProduct.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartProductDocument = value.documents
                    // i need to get the list of product
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProduct.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    // i need to create function to change my quantity
    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {

        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocument[index].id
            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch {
                        _cartProduct.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    // this is to if the quantity les of 1 don't more 0,-1...
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch {
                        _cartProduct.emit(Resource.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    // i need to create function to delete an item cart product
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocument[index].id
            firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }

}