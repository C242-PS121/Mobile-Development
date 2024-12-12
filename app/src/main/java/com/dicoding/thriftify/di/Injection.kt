package com.dicoding.thriftify.di

import android.content.Context
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.pref.UserPreference
import com.dicoding.thriftify.data.pref.dataStore
import com.dicoding.thriftify.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val mlApiService = ApiConfig.getMlApiService()
        return UserRepository.getInstance(context, pref, apiService, mlApiService)
    }
}