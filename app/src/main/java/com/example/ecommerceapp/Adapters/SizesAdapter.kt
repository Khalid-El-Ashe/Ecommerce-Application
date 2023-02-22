package com.example.ecommerceapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {
    private var selectedPosition = -1

    inner class SizesViewHolder(val binding: SizeRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.tvSize.text = size
            if (selectedPosition == position) { // size is selected
                binding.imageShadow.visibility = View.VISIBLE
            } else { // size is not selected
                binding.imageShadow.visibility = View.INVISIBLE
            }
        }
    }

    private val diffUtilCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtilCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) notifyItemChanged(holder.adapterPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemSelected?.invoke(size)
        }
    }
    var onItemSelected: ((String) -> Unit)?= null
}