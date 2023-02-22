package com.example.ecommerceapp.Util

import android.util.Patterns

// in here i need to make some functions to registration

// this function to validate the email address
fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty())
        return RegisterValidation.Failed("Email Can't be empty!")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong Email format")

    return RegisterValidation.Success
}

// this function to validate the password
fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty())
        return RegisterValidation.Failed("Password Can't be empty!")
    if (password.length < 6)
        return RegisterValidation.Failed("Password is to short")

    return RegisterValidation.Success
}