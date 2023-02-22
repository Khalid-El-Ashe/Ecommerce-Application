package com.example.ecommerceapp.ViewModel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.Data.User
import com.example.ecommerceapp.KelinApplication
import com.example.ecommerceapp.Util.RegisterValidation
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.Util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUserInformation()
    }

    // i need to create function to get user information
    fun getUserInformation() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        firestore.collection("user").document(firebaseAuth.uid!!).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            user?.let {
                viewModelScope.launch {
                    _user.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _user.emit(Resource.Error(it.message.toString()))
            }
        }
    }


    // i need to create anther function edit user information
    fun updateUserInformation(user: User, imageUri: Uri?) {
        // i need to validate the input
        val areInputValidate = validateEmail(user.email) is RegisterValidation.Success &&
                user.firstName.trim().isNotEmpty() &&
                user.lastName.trim().isNotEmpty()

        if (!areInputValidate) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your input"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

        if (imageUri == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImage(user, imageUri)
        }
    }

    // in here i need to create function to save the user image and data information
    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                // i need to add the image user to bitmap image to upload it in cloud firebase
                val imageBitmap = MediaStore.Images.Media.getBitmap(getApplication<KelinApplication>().contentResolver,imageUri)
                val bytArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, bytArrayOutputStream) // i need to add the quality to image
                val imageBytArray = bytArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${firebaseAuth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageBytArray).await()
                val imageURl = result.storage.downloadUrl.await().toString()

                // i need to save the new image in the information
                saveUserInformation(user.copy(imagePath = imageURl), false)

            }catch (e: Exception){
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    // in here i need to create function to save the user data information without image
    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        firestore.runTransaction { transsaction ->
            val documentRef = firestore.collection("user").document(firebaseAuth.uid!!)
            if (shouldRetrieveOldImage) {
                val currentUser = transsaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transsaction.set(documentRef, newUser)
            } else {
                transsaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}