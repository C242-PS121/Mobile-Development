package com.dicoding.thriftify.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.thriftify.data.UserRepository

class AccountViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Account Fragment"
    }
    val text: LiveData<String> = _text
}