package com.example.ecommerceapp.Data.order

sealed class OrderStatus(val status: String) {

    object Ordered : OrderStatus("Ordered")
    object Canceled : OrderStatus("Canceled")
    object Confirmed : OrderStatus("Confirmed")
    object Shipping : OrderStatus("Shipping")
    object Delivered : OrderStatus("Delivered")
    object Returned : OrderStatus("Returned")

}

// i need to create function to get the order status
fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> {
            OrderStatus.Ordered
        }
        "Canceled" -> {
            OrderStatus.Canceled
        }
        "Confirmed" -> {
            OrderStatus.Confirmed
        }
        "Shipping" -> {
            OrderStatus.Shipping
        }
        "Delivered" -> {
            OrderStatus.Delivered
        }
        else -> OrderStatus.Returned
    }
}