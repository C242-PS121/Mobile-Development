package com.dicoding.thriftify.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CheckoutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Checkout Fragment"
    }
    val text: LiveData<String> = _text
}