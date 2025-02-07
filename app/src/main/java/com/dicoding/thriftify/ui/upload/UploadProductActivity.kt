package com.dicoding.thriftify.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.thriftify.R
import com.dicoding.thriftify.databinding.ActivityUploadProductBinding
import com.dicoding.thriftify.utils.getImageUri
import com.dicoding.thriftify.utils.reduceFileImage
import com.dicoding.thriftify.utils.uriToFile
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.data.remote.response.MlResponse
import com.dicoding.thriftify.data.remote.retrofit.ApiConfig
import com.dicoding.thriftify.ui.main.MainActivity
import com.dicoding.thriftify.utils.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class UploadProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadProductBinding
    private var currentImageUri: Uri? = null
    private lateinit var uploadProductViewModel: UploadProductViewModel
    private val mlApiService = ApiConfig.getMlApiService()

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.CAMERA, false) -> {
                }
                else -> {
                    showToast(getString(R.string.permission_denied))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(arrayOf(REQUIRED_PERMISSION))
        }

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        uploadProductViewModel = ViewModelProvider(this, factory)[UploadProductViewModel::class.java]

        supportActionBar?.apply {
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        setupUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupUI() {
        binding.apply {
            cameraButton.setOnClickListener { launchCamera() }
            galleryButton.setOnClickListener { launchGallery() }
            buttonAdd.setOnClickListener { uploadProduct() }

        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(packageManager)
        }
        val imageUri = getImageUri(this)
        currentImageUri = imageUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        launcherCamera.launch(intent)
    }


    private val launcherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            currentImageUri?.let { uri ->
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherGallery.launch(intent)
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectedImageUri = it.data?.data
            selectedImageUri?.let { uri ->
                currentImageUri = uri
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun uploadProduct() {
        currentImageUri?.let { uri ->
            val file = uriToFile(uri, this).reduceFileImage()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipartForMl: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestImageFile
            )

            val imageMultipartForProduct: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestImageFile
            )

            lifecycleScope.launch {
                uploadProductViewModel.getSession().observe(this@UploadProductActivity) { user ->
                    if (user != null) {
                        lifecycleScope.launch {
                            val result = uploadProductViewModel.classifyImage("Bearer ${user.accessToken}", imageMultipartForMl)
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    val mlResponse = result.data
                                    val clothingType = mlResponse.data.type
                                    val clothingUsage = mlResponse.data.usage

                                    uploadProductViewModel.uploadProduct(
                                        user.userId,
                                        imageMultipartForProduct,
                                        binding.edName.text.toString(),
                                        binding.edPrice.text.toString().toIntOrNull(),
                                        binding.edAddDescription.text.toString(),
                                        clothingType,
                                        clothingUsage
                                    )
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    showToast(result.error)
                                }
                            }
                        }
                    }
                }
            }

            uploadProductViewModel.uploadProductResponse.observe(this) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Yeah!")
                            setMessage(getString(R.string.product_upload_success))
                            setCancelable(false)
                            setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                                navigateToMainPage()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(it.error)
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}