package com.example.ecommerceapp.Util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // i need to make class when the operation is success
    class Success<T>(data: T) : Resource<T>(data)

    // i need to make class when the operation is error
    class Error<T>(message: String) : Resource<T>(message = message)

    // i need to make class when the operation is loading
    class Loading<T> : Resource<T>()

    class Unspecified<T>: Resource<T>()
}