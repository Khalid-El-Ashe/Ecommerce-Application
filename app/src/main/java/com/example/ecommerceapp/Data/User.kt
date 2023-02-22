package com.example.ecommerceapp.Data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath: String = ""
) {
    // i need to make empty constructor because the firebase need that
    constructor(): this("","","","")
}
