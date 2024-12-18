package com.dicoding.thriftify.ui.main

import ProductDetail
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.pref.UserModel
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.data.remote.response.Product
import com.dicoding.thriftify.data.remote.response.UserResponse

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getUserById(userId: String, accessToken: String): LiveData<Result<UserResponse>> {
        return repository.getUserById(userId, accessToken)
    }

    fun getAllProducts(): LiveData<Result<List<Product>>> {
        return repository.getAllProducts()
    }

    fun getProductById(productId: String): LiveData<Result<ProductDetail>> {
        return repository.getProductById(productId)
    }

    fun logout(): LiveData<Result<LogoutResponse>> {
        return repository.logout()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}