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


class ProductAdapter(
    val products: MutableList<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.image_product)
        val textPrice: TextView = itemView.findViewById(R.id.text_price)
        val textName: TextView = itemView.findViewById(R.id.text_name)

        fun bind(product: Product) {
            textPrice.text = "Rp ${product.price}"
            textName.text = product.name
            Glide.with(itemView.context)
                .load(product.img)
                .into(imageProduct)

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}

