package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.databinding.ViewpagerIamgeItemBinding

class ViewPager2Images : RecyclerView.Adapter<ViewPager2Images.ViewPage2ImagesViewHolder>() {

    inner class ViewPage2ImagesViewHolder(val binding: ViewpagerIamgeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPage2ImagesViewHolder {
        return ViewPage2ImagesViewHolder(
            ViewpagerIamgeItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewPage2ImagesViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}