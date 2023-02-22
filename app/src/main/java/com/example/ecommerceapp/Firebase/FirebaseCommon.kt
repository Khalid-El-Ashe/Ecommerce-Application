package com.example.ecommerceapp.Firebase

import com.example.ecommerceapp.Data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val catCollection = firestore.collection("user").document(auth.uid!!).collection("cart")

    // add product to cart
    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        catCollection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    // this fun to complicated the products
    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        // i need to increase and save the updated product
        firestore.runTransaction { transaction -> // this to read and write
            val documentRed = catCollection.document(documentId)
            val document = transaction.get(documentRed)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject =
                    cartProduct.copy(quantity = newQuantity) // to create new product
                transaction.set(documentRed, newProductObject) // i need to update the product
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    // this function to decrease quantity function
    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        // i need to increase and save the updated product
        firestore.runTransaction { transaction -> // this to read and write
            val documentRed = catCollection.document(documentId)
            val document = transaction.get(documentRed)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject =
                    cartProduct.copy(quantity = newQuantity) // to create new product
                transaction.set(documentRed, newProductObject) // i need to update the product
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    // i need to create helper class
    enum class QuantityChanging{
        INCREASE, DECREASE
    }
}