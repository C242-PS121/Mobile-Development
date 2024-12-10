package com.dicoding.thriftify.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.thriftify.R
import com.dicoding.thriftify.model.Product

class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.image_product)
        val textPrice: TextView = itemView.findViewById(R.id.text_price)
        val textName: TextView = itemView.findViewById(R.id.text_name)
        val textLocation: TextView = itemView.findViewById(R.id.text_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.textPrice.text = product.price
        holder.textName.text = product.name
        holder.textLocation.text = product.location
         Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.imageProduct)
    }

    override fun getItemCount() = products.size
}
