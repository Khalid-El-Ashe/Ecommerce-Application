package com.example.ecommerceapp.Util

sealed class RegisterValidation() {

    object Success : RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

data class RegisterFieldsState(
    val email: RegisterValidation,
    val password: RegisterValidation
)