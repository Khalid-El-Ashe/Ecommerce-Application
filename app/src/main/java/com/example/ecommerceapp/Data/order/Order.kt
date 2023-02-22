package com.example.ecommerceapp.Data.order

import android.os.Parcelable
import com.example.ecommerceapp.Data.Address
import com.example.ecommerceapp.Data.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    // TODO: you must to make empty constructor because you call the firebase or add empty value
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0,100_000_000_000) + totalPrice.toLong()
) : Parcelable