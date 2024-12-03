package com.dicoding.thriftify.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.thriftify.data.remote.request.RegisterRequest
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.remote.response.RegisterResponse
import com.dicoding.thriftify.data.Result

class RegisterViewModel (private val repository: UserRepository) : ViewModel() {

    private val _registerResponse = MediatorLiveData<Result<RegisterResponse>>()
    val registerResponse: LiveData<Result<RegisterResponse>> = _registerResponse

    fun register(registerRequest: RegisterRequest) {
        val liveData = repository.register(registerRequest)
        _registerResponse.addSource(liveData) { result ->
            _registerResponse.value = result
        }
    }
}