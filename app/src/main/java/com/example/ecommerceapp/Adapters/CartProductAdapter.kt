package com.example.ecommerceapp.Adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Data.CartProduct
import com.example.ecommerceapp.Helper.getProductPrice
import com.example.ecommerceapp.databinding.CartProductItemBinding

class CartProductAdapter :
    RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {

    inner class CartProductViewHolder(val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                // i need to get just the first image
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                val priceAfterPercentage = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                // ?: that is mean {else}
                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        cartProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = cartProduct.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    // i need to make DiffUtil to make list of Products item
    private val diffCallBack = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    // i need to make value to update the list
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        // i need to click the item
        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }
        holder.binding.imageMinus.setOnClickListener {
            onMinesClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((CartProduct) -> Unit)? = null // to click the item
    var onPlusClick: ((CartProduct) -> Unit)? = null // to click the plus button
    var onMinesClick: ((CartProduct) -> Unit)? = null // to click the mines button

}