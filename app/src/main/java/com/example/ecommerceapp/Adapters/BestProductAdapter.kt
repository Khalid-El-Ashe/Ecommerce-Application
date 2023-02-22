package com.example.ecommerceapp.Adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Data.Product
import com.example.ecommerceapp.Helper.getProductPrice
import com.example.ecommerceapp.databinding.ProductRvItemBinding

class BestProductAdapter : RecyclerView.Adapter<BestProductAdapter.BastProductViewHolder>() {

    inner class BastProductViewHolder(private val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                // in here i need to get the old price and new price
                val priceAfterDeffer = product.offerPercentage.getProductPrice(product.price)
                // نريد ان نقرب السعر لاقرب قيمة صغيرة
                tvNewPrice.text = "$ ${String.format("%.2f", priceAfterDeffer)}"
                if (product.offerPercentage == null) tvNewPrice.visibility = View.GONE
                tvPrice.text = "$ ${product.price}"
                tvName.text = product.name
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // to make line in the text
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BastProductViewHolder {
        return BastProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BastProductViewHolder, position: Int) {
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

    var onClick: ((Product) -> Unit)? = null
}