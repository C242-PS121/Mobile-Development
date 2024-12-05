package com.dicoding.thriftify.data


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.pref.UserModel
import com.dicoding.thriftify.data.pref.UserPreference
import com.dicoding.thriftify.data.remote.request.LoginRequest
import com.dicoding.thriftify.data.remote.request.RegisterRequest
import com.dicoding.thriftify.data.remote.response.LoginResponse
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.remote.response.RegisterResponse
import com.dicoding.thriftify.data.remote.response.UserResponse
import com.dicoding.thriftify.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class UserRepository private constructor(
    private val context: Context,
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    fun register(registerRequest: RegisterRequest): LiveData<Result<RegisterResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.register(registerRequest)
                emit(Result.Success(response))
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> {
                        val errorResponse = e.response()?.errorBody()?.string()
                        parseErrorMessage(errorResponse)
                    }

                    is IOException -> context.getString(R.string.error_connection_failed)
                    else -> context.getString(R.string.error_unknown)
                }
                emit(Result.Error(errorMessage))
            }
        }

    fun login(loginRequest: LoginRequest): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.login(loginRequest)
                val accessToken = response.data.accessToken
                val refreshToken = response.data.refreshToken
                val userId = response.data.userId
                saveSession(UserModel(loginRequest.email, userId, accessToken, refreshToken, true))
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                val errorMessage = when (e) {
                    is HttpException -> {
                        val errorResponse = e.response()?.errorBody()?.string()
                        parseErrorMessage(errorResponse)
                    }
                    is IOException -> context.getString(R.string.error_connection_failed)
                    else -> context.getString(R.string.error_unknown)
                }
                emit(Result.Error(errorMessage))
            }
        }

    fun getUserById(id: String): LiveData<Result<UserResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getUserById(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val errorResponse = e.response()?.errorBody()?.string()
                    parseErrorMessage(errorResponse)
                }
                is IOException -> context.getString(R.string.error_connection_failed)
                else -> context.getString(R.string.error_unknown)
            }
            emit(Result.Error(errorMessage))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun logout(): LiveData<Result<LogoutResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first()
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"refresh_token\": \"${user.refreshToken}\"}"
            )

            val response = apiService.logout(requestBody)
            if (response.message == "Successfully logged out") {
                userPreference.logout()
                emit(Result.Success(response))
            } else {
                response.message?.let { Result.Error(it) }?.let { emit(it) }
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val errorResponse = e.response()?.errorBody()?.string()
                    parseErrorMessage(errorResponse)
                }
                is IOException -> context.getString(R.string.error_connection_failed)
                else -> context.getString(R.string.error_unknown)
            }
            emit(Result.Error(errorMessage))
        }
    }



    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            context: Context,
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(context, userPreference, apiService)
            }.also { instance = it }
    }

    private fun parseErrorMessage(response: String?): String {
        return try {
            val jsonObject = JSONObject(response ?: "")
            jsonObject.getString("message")
        } catch (e: JSONException) {
            context.getString(R.string.error_unknown)
        }
    }
}