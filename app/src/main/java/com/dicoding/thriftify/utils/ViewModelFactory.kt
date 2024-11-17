package com.dicoding.thriftify.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.di.Injection
import com.dicoding.thriftify.ui.account.AccountViewModel
import com.dicoding.thriftify.ui.checkout.CheckoutViewModel
import com.dicoding.thriftify.ui.favorite.FavoriteViewModel
import com.dicoding.thriftify.ui.home.HomeViewModel
import com.dicoding.thriftify.ui.login.LoginViewModel
import com.dicoding.thriftify.ui.main.MainViewModel
import com.dicoding.thriftify.ui.register.RegisterViewModel
import com.dicoding.thriftify.ui.search.SearchViewModel


class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}