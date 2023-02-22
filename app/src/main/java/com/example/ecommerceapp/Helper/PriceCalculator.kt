package com.example.ecommerceapp.Helper

// this function to calculate the price
fun Float?.getProductPrice(price: Float): Float {
    // this reference to percentage
    if (this == null) {
        return price
    }
    val remainingPricePercentage = 1f - this
    val priceAfterDeffer = remainingPricePercentage * price

    return priceAfterDeffer
}