package com.example.ecommerceapp.UI.fragments.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.UI.ShoppingActivity
import com.example.ecommerceapp.databinding.FragmentLoginBinding
import com.example.ecommerceapp.Dialog.setupBottomSheetDialog
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

// this is to use the Dagger Hielt in this activity and to make instance of class view model
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }

            tvDontHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            tvForgotPasswordLogin.setOnClickListener {
                // this function i created it
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
        }

        // i need to use the flow to work the reset password in the background thread
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Reset link was sent to your email",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        // i need to use coroutine scope to listen the shared flow
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // this is to finish the activity when done to work
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        binding.buttonLoginLogin.revertAnimation()
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> Unit
                }
            }
        }
    }
}