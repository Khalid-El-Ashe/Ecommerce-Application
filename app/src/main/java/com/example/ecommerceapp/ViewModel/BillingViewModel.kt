package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.Address
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddress() // to make inisialize this function
    }

    // i need to create function to get all addresses the user add it
    fun getUserAddress(){
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }
        firestore.collection("user").document(firebaseAuth.uid!!).collection("address")
            // i need to use snapshot listener to live update the data
            .addSnapshotListener { value, error ->
                if (error != null){
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                // if the get is completed i need to get it
                val address = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(address!!)) }
            }
    }

}