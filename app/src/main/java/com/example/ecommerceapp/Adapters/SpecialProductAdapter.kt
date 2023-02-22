package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Data.Product
import com.example.ecommerceapp.databinding.SpecialRvItemBinding

class SpecialProductAdapter :
    RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder>() {

    inner class SpecialProductViewHolder(private val binding: SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                // i need to get just the first image
                Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                tvSpecialProductName.text = product.name
                tvSpecialProductPrise.text = product.price.toString()

            }
        }
    }

    // i need to make DiffUtil to make list of Products item
    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    // i need to make value to update the list
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder {
        return SpecialProductViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        // i need to click the item
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick:((Product) -> Unit)?= null
}