package com.dicoding.thriftify.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.remote.response.Product


class ProductAdapter(val products: MutableList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.image_product)
        val textPrice: TextView = itemView.findViewById(R.id.text_price)
        val textName: TextView = itemView.findViewById(R.id.text_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.textPrice.text = "Rp ${product.price}"
        holder.textName.text = product.name
        Glide.with(holder.itemView.context)
            .load(product.img)
            .into(holder.imageProduct)
    }


    override fun getItemCount() = products.size
}
