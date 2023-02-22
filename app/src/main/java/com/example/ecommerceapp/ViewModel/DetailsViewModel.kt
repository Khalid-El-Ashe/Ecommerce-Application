package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.CartProduct
import com.example.ecommerceapp.Firebase.FirebaseCommon
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get().addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) { // if is empty add new product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) { // increase the quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else { // add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addProduct, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToCart.emit(Resource.Success(addProduct!!))
                } else {
                    _addToCart.emit(Resource.Error(exception.toString()))
                }
            }
        }
    }

    // this function to increase the quantity
    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToCart.emit(Resource.Success(cartProduct))
                } else {
                    _addToCart.emit(Resource.Error(exception.toString()))
                }
            }
        }
    }
}