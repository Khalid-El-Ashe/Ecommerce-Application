package com.example.ecommerceapp.UI.fragments.LoginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.Data.User
import com.example.ecommerceapp.databinding.FragmentRegisterBinding
import com.example.ecommerceapp.Util.RegisterValidation
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint // this is to use the Dagger Hielt in this activity
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>() // i need to make instance of my Register ViewModel
    private val TAG = "RegisterFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            buttonRegisterRegister.setOnClickListener {

                // i need to make object of User class to save in the firebase
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(
                    user,
                    password
                ) // i need to create new account
            }

            tvDoYouHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                // i need to check the operation
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    is Resource.Error -> {
                        Log.d(TAG, it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        // in here i need to validation the email and password because this is the important to create new account
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }

    }
}