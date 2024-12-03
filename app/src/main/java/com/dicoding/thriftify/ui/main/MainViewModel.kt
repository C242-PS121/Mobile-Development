package com.dicoding.thriftify.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.pref.UserModel
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.Result
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
//    private val _logoutResponse = MutableLiveData<Result<LogoutResponse>>()
//    val logoutResponse: LiveData<Result<LogoutResponse>> = _logoutResponse
//
//    fun logout() {
//        viewModelScope.launch {
//            _logoutResponse.value = Result.Loading
//            try {
//                Log.d("MainViewModel", "Logout started")
//                val logoutResult = repository.logout()
//                _logoutResponse.postValue(logoutResult.value)
//            } catch (e: Exception) {
//                Log.e("MainViewModel", "Logout error: ${e.message}", e)
//                _logoutResponse.postValue(Result.Error(e.message.toString()))
//            }
//        }
//    }


    fun logout(): LiveData<Result<LogoutResponse>> {
        return repository.logout()
    }


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}