package com.dicoding.thriftify.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.data.remote.response.DetailProductResponse
import com.dicoding.thriftify.ui.main.MainViewModel
import com.dicoding.thriftify.utils.ViewModelFactory
import com.google.android.material.button.MaterialButton

class DetailFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var clothingTypeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var emailButton: MaterialButton
    private lateinit var whatsappButton: MaterialButton

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        imageView = view.findViewById(R.id.imageLogo)
        nameTextView = view.findViewById(R.id.eventName)
        priceTextView = view.findViewById(R.id.price)
        clothingTypeTextView = view.findViewById(R.id.clothing_type)
        descriptionTextView = view.findViewById(R.id.description)
        emailButton = view.findViewById(R.id.emailButton)
        whatsappButton = view.findViewById(R.id.whatsappButton)

        val factory = ViewModelFactory.getInstance(requireContext())
        mainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        val productId = arguments?.getString("productId") ?: ""
        observeProductDetails(productId)

        return view
    }

    private fun observeProductDetails(productId: String) {
        mainViewModel.getProductDetails(productId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    displayProductDetails(result.data)
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayProductDetails(product: DetailProductResponse) {
        Glide.with(this)
            .load(product.mainImgUrl)
            .into(imageView)

        nameTextView.text = product.name
        priceTextView.text = product.price.toString()
        clothingTypeTextView.text = product.clothingType
        descriptionTextView.text = product.description

        emailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
                putExtra(Intent.EXTRA_SUBJECT, "Inquiry about ${product.name}")
            }
            startActivity(emailIntent)
        }

        whatsappButton.setOnClickListener {
            val whatsappUri = Uri.parse("https://wa.me/")
            val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri)
            startActivity(whatsappIntent)
        }
    }
}
