package com.dicoding.thriftify.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.thriftify.data.UserRepository

class RegisterViewModel (private val repository: UserRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Register Activity"
    }
    val text: LiveData<String> = _text
}