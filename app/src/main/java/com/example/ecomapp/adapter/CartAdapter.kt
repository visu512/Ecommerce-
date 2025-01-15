package com.example.ecomapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecomapp.databinding.CartproductItemBinding
import com.example.ecomapp.roomdb.AppDatabase
import com.example.ecomapp.roomdb.ProductModelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartAdapter(
    private val context: Context,
    private val list: MutableList<ProductModelEntity>,
    private val onQuantityChanged: (Double) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: CartproductItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartproductItemBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = list[position]

        Glide.with(context).load(item.productImage).into(holder.binding.cartImage)
        holder.binding.cartText1.text = item.productName

        val unitPrice = item.productPrice.toDoubleOrNull() ?: 0.0
        holder.binding.cartText2.text = "₹${unitPrice * item.quantity}"  // Update displayed price

        // Quantity Selector (Spinner Dropdown)
        val quantityOptions = (1..5).map { it.toString() }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, quantityOptions)
        holder.binding.spinnerQuantity.adapter = adapter
        holder.binding.spinnerQuantity.setSelection(item.quantity - 1)

        holder.binding.spinnerQuantity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newQuantity = pos + 1
                if (newQuantity != item.quantity) {
                    item.quantity = newQuantity
                    updateItemInDatabase(item) {
                        holder.binding.cartText2.text = "₹${unitPrice * item.quantity}" // Update price display
                        onQuantityChanged(getTotalPrice()) // Update total price
                        notifyItemChanged(position)  // Refresh UI for the item
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Remove Button
        holder.binding.btnRemove.setOnClickListener {
            removeItem(position, item)
        }
    }

    override fun getItemCount(): Int = list.size

    private fun updateItemInDatabase(item: ProductModelEntity, onComplete: () -> Unit) {
        val dao = AppDatabase.getInstance(context).productDao()
        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch(Dispatchers.IO) {
            dao.updateProduct(item)

            withContext(Dispatchers.Main) {
                onComplete()  // Execute UI updates after database update
            }
        }
    }

    private fun removeItem(position: Int, item: ProductModelEntity) {
        val dao = AppDatabase.getInstance(context).productDao()
        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch(Dispatchers.IO) {
            dao.deleteProduct(item)

            withContext(Dispatchers.Main) {
                list.removeAt(position)
                notifyItemRemoved(position)
                onQuantityChanged(getTotalPrice()) // Update total price after removal
//                Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getTotalPrice(): Double {
        return list.sumOf { (it.productPrice.toDoubleOrNull() ?: 0.0) * it.quantity }
    }
}
