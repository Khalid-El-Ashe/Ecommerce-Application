package com.example.ecommerceapp.UI.fragments.Settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Data.User
import com.example.ecommerceapp.Dialog.setupBottomSheetDialog
import com.example.ecommerceapp.Util.Resource
import com.example.ecommerceapp.ViewModel.UserAccountViewModel
import com.example.ecommerceapp.databinding.FragmentUserAccountBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment: Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private var imageUri: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            imageUri = it.data?.data
            Glide.with(requireContext()).load(imageUri).into(binding.imageUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // in the first i need to show the data information
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                         showUserLoading()
                    }
                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // in the second i need to update the data information
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()

                val user = User(firstName,lastName,email)
                viewModel.updateUserInformation(user, imageUri)

            }
        }

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            activityResultLauncher.launch(intent)
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {  }
        }

    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(requireContext()).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}