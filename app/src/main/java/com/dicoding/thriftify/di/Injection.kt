package com.dicoding.thriftify.di

import android.content.Context
import com.dicoding.thriftify.data.UserRepository
import com.dicoding.thriftify.data.pref.UserPreference
import com.dicoding.thriftify.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}