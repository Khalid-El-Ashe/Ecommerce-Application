package com.example.ecommerceapp.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // this is to use with dagger hilt
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }
}