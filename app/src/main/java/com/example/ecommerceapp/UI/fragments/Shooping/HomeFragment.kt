package com.example.ecommerceapp.UI.fragments.Shooping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.R
import com.example.ecommerceapp.UI.fragments.Categories.*
import com.example.ecommerceapp.Adapters.HomeViewPagerAdapter
import com.example.ecommerceapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // i need to add the array of fragments to add in the viewpager
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        // i need to make instance of viewpage fragment adapter
        val viewpageAdapter =
            HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewpageAdapter

        binding.viewpagerHome.isUserInputEnabled = false // to cancel the swiping the viewPager

        // add the tabs in the Tab layout
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, posistion ->
            when (posistion) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }

}