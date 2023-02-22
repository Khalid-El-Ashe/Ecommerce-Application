package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    // i need to make function to login
    fun login(email: String, password: String) {
        // this is to loading btn login
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            viewModelScope.launch {
                // i need to check the user is null or not
                it.user?.let {
                    _login.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _login.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    // i need to create reset password function
    fun resetPassword(email: String) {
        // i need to open coroutine scoop
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Success(email))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}