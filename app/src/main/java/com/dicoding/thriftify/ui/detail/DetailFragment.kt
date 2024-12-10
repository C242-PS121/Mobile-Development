package com.dicoding.thriftify.ui.detail

import ProductDetail
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.databinding.FragmentDetailBinding
import com.dicoding.thriftify.ui.main.MainViewModel
import com.dicoding.thriftify.utils.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getString("PRODUCT_ID") ?: return

        mainViewModel.getProductById(productId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    displayProductDetails(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun displayProductDetails(product: ProductDetail) {
        binding.apply {
            eventName.text = product.name
            price.text = "Rp. ${product.price}"
            clothingType.text = product.clothingType
            description.text = product.description

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
        Glide.with(this)
            .load(product.mainImageUrl)
            .into(binding.imageLogo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}