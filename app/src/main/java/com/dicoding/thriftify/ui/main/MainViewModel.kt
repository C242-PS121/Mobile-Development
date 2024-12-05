package com.dicoding.thriftify.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.pref.UserModel
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.data.remote.response.UserResponse

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getUserById(userId: String): LiveData<Result<UserResponse>> {
        return repository.getUserById(userId)
    }

    fun logout(): LiveData<Result<LogoutResponse>> {
        return repository.logout()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}