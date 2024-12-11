package com.dicoding.thriftify.ui.detail

import ProductDetail
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.data.remote.response.UserData
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = "Detail Produk"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productId = arguments?.getString("PRODUCT_ID") ?: return

        mainViewModel.getProductById(productId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val product = result.data
                    mainViewModel.getSession().observe(viewLifecycleOwner) { userSession ->
                        val token = userSession.accessToken
                        mainViewModel.getUserById(product.ownerId, token)
                            .observe(viewLifecycleOwner) { userResult ->
                                when (userResult) {
                                    is Result.Loading -> showLoading(true)
                                    is Result.Success -> {
                                        showLoading(false)
                                        val userData = userResult.data.data
                                        displayProductDetails(product, userData)
                                    }
                                    is Result.Error -> {
                                        showLoading(false)
                                        showError(userResult.error)
                                    }
                                }
                            }
                    }
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

    private fun displayProductDetails(product: ProductDetail, user: UserData) {
        val purchaseMessage = getString(R.string.purchase_message, user.fullname, product.name, product.price)
        binding.apply {
            eventName.text = product.name
            ownerName.text = "${user.fullname}"
            price.text = "Rp. ${product.price}"
            clothingType.text = product.clothingType
            description.text = product.description

            emailButton.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(user.email))
                    putExtra(Intent.EXTRA_SUBJECT, "Inquiry about ${product.name}")
                    putExtra(Intent.EXTRA_TEXT, purchaseMessage)
                }
                startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"))
            }

            whatsappButton.setOnClickListener {
                val whatsappUri = Uri.parse("https://wa.me/${user.phone}?text=${Uri.encode(purchaseMessage)}")
                val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri)
                startActivity(whatsappIntent)
            }
        }
        Glide.with(this)
            .load(product.mainImageUrl)
            .into(binding.imageLogo)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
