package com.example.ecommerceapp.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
) : Parcelable {
    // i need to create empty constructor to don't make crash if is it null
    constructor() : this("", "", "", "", "", "")
}