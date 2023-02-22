package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.User
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    // TODO: create function to get the user data from firebase Auth
    fun getUser() {

        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        // TODO: i need to use the snapshot listener to retreve or refresh the user data when update
        firestore.collection("user").document(firebaseAuth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null){
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                }else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }
    }

    // TODO: i need to create function to logout the user account
    fun logout() {
        firebaseAuth.signOut()
    }
}