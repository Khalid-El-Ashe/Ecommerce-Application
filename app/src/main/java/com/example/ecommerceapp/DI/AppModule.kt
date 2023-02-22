package com.example.ecommerceapp.DI

import android.app.Application
import android.content.Context
import com.example.ecommerceapp.Firebase.FirebaseCommon
import com.example.ecommerceapp.Util.Constants.INTRODUCTION_SP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// this anotation for dagger libs
@Module
@InstallIn(SingletonComponent::class)// this is to make all model is alive in the app
object AppModule {

    // how to use it: when you need to use the Firebase dagger find this function and call to make instance of FirebaseAuth
    @Provides
    @Singleton // to make one instance in the app for this depindanse
    fun providerFirebaseAuth() = FirebaseAuth.getInstance()

    // i need to make instance of database store lib the Auth
    @Provides
    @Singleton
    fun providerFirebaseFireStoreDatabase() = Firebase.firestore

    // i need to make instance of sharedPreferences to login one time and save the data login
    @Provides
    fun providerIntroductionSP(application: Application) =
        application.getSharedPreferences(INTRODUCTION_SP, Context.MODE_PRIVATE)

    // in here i need to make instance of class firebase common
    @Provides
    @Singleton
    fun providerFirebaseCommon(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) =
        FirebaseCommon(firestore, firebaseAuth)

    @Provides
    @Singleton
    fun providerStorage() = FirebaseStorage.getInstance().reference
}