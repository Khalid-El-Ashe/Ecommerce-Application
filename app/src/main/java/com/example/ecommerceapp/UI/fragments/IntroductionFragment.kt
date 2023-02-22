package com.example.ecommerceapp.UI.fragments

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
import com.example.ecommerceapp.databinding.FragmentIntroductionBinding
import com.example.ecommerceapp.ViewModel.IntroductionViewModel
import com.example.ecommerceapp.ViewModel.IntroductionViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.example.ecommerceapp.ViewModel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // this is for dagger hilt lib to don't do init
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // in here i need to use coroutine scoop
        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                // i need to check the it value
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // this is to finish the activity when done to work
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTION_FRAGMENT -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit // else i need to do anything
                }
            }
        }

        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}