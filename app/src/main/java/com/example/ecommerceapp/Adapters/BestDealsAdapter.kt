package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Data.Product
import com.example.ecommerceapp.databinding.BestDealItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BastDealsViewHolder>() {

    inner class BastDealsViewHolder(private val binding: BestDealItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                tvDealProductName.text = product.name
                // i need to check the offer if is it null
                product.offerPercentage?.let {
                    // in here i need to get the old price and new price
                    val remainingPricePercentage = 1f - it
                    val priceAfterDeffer = remainingPricePercentage * product.price
                    // نريد ان نقرب السعر لاقرب قيمة صغيرة
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterDeffer)}"
                }
                tvOldPrice.text = "$ ${product.price}"
                tvDealProductName.text = product.name
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BastDealsViewHolder {
        return BastDealsViewHolder(
            BestDealItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BastDealsViewHolder, position: Int) {
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