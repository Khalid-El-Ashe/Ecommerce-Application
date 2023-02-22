package com.example.ecommerceapp.ViewModel

import androidx.lifecycle.ViewModel
import com.example.ecommerceapp.Data.User
import com.example.ecommerceapp.Util.*
import com.example.ecommerceapp.Util.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    // i need to make value to tell my what the condition of operation
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    // i need to make function to create new account
    fun createAccountWithEmailAndPassword(user: User, password: String) {

        // this function to validation
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
                // i need to check the user is the null or not
                it.user?.let {

                    // i need to save the data of users in the firestore db
                    saveUserInfo(it.uid, user)
                }
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString()) // the user created is error
            }
        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    // this function to save the user data in database store
    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION).document(userUid).set(user).addOnSuccessListener {
            _register.value = Resource.Success(user) // the user is created is success
        }.addOnFailureListener {
            _register.value = Resource.Error(it.message.toString()) // the user created is error

        }
    }

    // i need to create validation function
    private fun checkValidation(user: User, password: String): Boolean {
        // i need to make the validation of email and password
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        // if the validation is true
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}