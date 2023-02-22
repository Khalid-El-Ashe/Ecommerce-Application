package com.example.ecommerceapp.Data

// i need to make sealed class to make some objects
sealed class Category(val category: String) {

    object Chair : Category("Chair")
    object Cupboard : Category("Cupboard")
    object Table : Category("Table")
    object Accessory : Category("Accessory")
    object Furniture : Category("Furniture")
}