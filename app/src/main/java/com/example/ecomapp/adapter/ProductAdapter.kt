package com.example.ecommerceapp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomapp.R
import com.example.ecomapp.activity.ProductDetailsActivity
import com.example.ecomapp.databinding.ProductItemLayoutBinding
import com.example.ecomapp.model.ProductModel
import com.squareup.picasso.Picasso

class ProductAdapter(
    private val context: Context,
    private var list: ArrayList<ProductModel>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var fullList = ArrayList<ProductModel>()

    init {
        fullList.addAll(list)
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ProductItemLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = list[position]
        holder.binding.pName.text = product.name ?: "No Name"
        holder.binding.pDesc.text = product.description ?: "No Description"
        holder.binding.pSellingPrice.text = "₹${product.sellingPrice ?: "N/A"}"
        holder.binding.pMRP.text = "₹${product.mrp ?: "N/A"}"
        holder.binding.pMRP.paintFlags = holder.binding.pMRP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        Picasso.get()
            .load(product.coverImageUrl)
            .placeholder(R.drawable.img)
            .error(R.drawable.img)
            .into(holder.binding.productImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("productName", product.name)
                putExtra("productDesc", product.description)
                putExtra("pMrp", product.mrp)
                putExtra("productPrice", product.sellingPrice)
                putExtra("coverImageUrl", product.coverImageUrl)
                putStringArrayListExtra("productImages", ArrayList(product.images ?: emptyList()))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: ArrayList<ProductModel>) {
        list.clear()
        list.addAll(newList)
        fullList.clear()
        fullList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name?.contains(query, ignoreCase = true) == true } as ArrayList<ProductModel>
        }
        list.clear()
        list.addAll(filteredList)
        notifyDataSetChanged()
    }
}
