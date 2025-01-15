package com.example.ecomapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecomapp.R
import com.example.ecomapp.databinding.CategoryItemLayoutBinding
import com.example.ecomapp.model.CategoryModel

class CategoryAdapter(
    private val context: Context,
    private val list: ArrayList<CategoryModel>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = CategoryItemLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(context).inflate(R.layout.category_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]

        // Set category name
        holder.binding.textView.text = category.categoryName ?: "No Name"

        // Load category image from Firebase using Glide
        Glide.with(context)
            .load(category.imageUrl) // Firebase image URL
            .placeholder(R.drawable.img) // Placeholder while loading
            .error(R.drawable.img) // Error image if URL is invalid
            .into(holder.binding.catImageView)
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: ArrayList<CategoryModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
