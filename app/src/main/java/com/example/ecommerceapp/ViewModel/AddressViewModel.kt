package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.Address
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    // i need to create function to add address
    fun addAddress(address: Address) {
        // i need to validate the input of address
        val validateInput = validateInput(address)

        if (validateInput) {
            viewModelScope.launch {
                _addNewAddress.emit(Resource.Loading())
            }
            // i need to add new row in the user column database
            firestore.collection("user").document(firebaseAuth.uid!!).collection("address")
                .document()
                .set(address).addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch { _error.emit("All fields are required") }
        }
    }

    // this function to validate the input address
    private fun validateInput(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
    }
}