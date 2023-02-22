package com.example.ecommerceapp.UI.fragments.Shooping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecommerceapp.BuildConfig
import com.example.ecommerceapp.R
import com.example.ecommerceapp.UI.LoginRegisterActivity
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.ProfileViewModel
import com.example.ecommerceapp.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
        binding.linearBilling.setOnClickListener {
            // TODO: in here i need to share some data
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
                emptyArray())
            findNavController().navigate(action)
        }
        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(),LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        // TODO: in here i need to add the version code for this application
        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE

                        // TODO: in here i need to show the use image
                        Glide.with(requireView()).load(it.data!!.imagePath).error(R.drawable.ic_user).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }
    }
}