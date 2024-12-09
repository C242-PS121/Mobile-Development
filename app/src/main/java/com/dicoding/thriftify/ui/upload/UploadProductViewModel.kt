package com.dicoding.thriftify.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.pref.UserModel
import com.dicoding.thriftify.data.remote.response.UploadProductResponse
import com.dicoding.thriftify.data.Result
import okhttp3.MultipartBody

class UploadProductViewModel (private val repository: UserRepository) : ViewModel() {
    private val _uploadProductResponse = MediatorLiveData<Result<UploadProductResponse>>()
    val uploadProductResponse: LiveData<Result<UploadProductResponse>> = _uploadProductResponse

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadProduct(
        ownerId: String,
        image: MultipartBody.Part,
        name: String,
        price: Int?,
        description: String,
        clothingType: String
    ) {
        val liveData = repository.uploadProduct(ownerId, image, name, price, description, clothingType)
        _uploadProductResponse.addSource(liveData) { result ->
            _uploadProductResponse.value = result
        }
    }

}