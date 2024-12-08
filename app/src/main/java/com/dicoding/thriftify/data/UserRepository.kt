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
import okhttp3.RequestBody.Companion.toRequestBody
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

    private suspend fun refreshAccessToken(refreshToken: String): Result<String> {
        return try {
            val requestBody = "{\"refresh_token\": \"$refreshToken\"}"
                .toRequestBody("application/json".toMediaTypeOrNull())
            val response = apiService.refreshAccessToken(requestBody)
            val newAccessToken = response.data.accessToken
            val currentSession = userPreference.getSession().first()
            saveSession(
                currentSession.copy(
                    accessToken = newAccessToken
                )
            )
            Result.Success(newAccessToken)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val errorResponse = e.response()?.errorBody()?.string()
                    parseErrorMessage(errorResponse)
                }
                is IOException -> context.getString(R.string.error_connection_failed)
                else -> context.getString(R.string.error_unknown)
            }
            Result.Error(errorMessage)
        }
    }


//    fun getUserById(userId: String, accessToken: String): LiveData<Result<UserResponse>> = liveData(Dispatchers.IO) {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getUserById(userId, "Bearer $accessToken")
//            emit(Result.Success(response))
//        } catch (e: Exception) {
//            val errorMessage = when (e) {
//                is HttpException -> {
//                    val errorResponse = e.response()?.errorBody()?.string()
//                    parseErrorMessage(errorResponse)
//                }
//                is IOException -> context.getString(R.string.error_connection_failed)
//                else -> context.getString(R.string.error_unknown)
//            }
//            emit(Result.Error(errorMessage))
//        }
//    }

    fun getUserById(userId: String, accessToken: String): LiveData<Result<UserResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getUserById(userId, "Bearer $accessToken")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                val currentUser = userPreference.getSession().first()
                val newAccessToken = refreshAccessToken(currentUser.refreshToken)
                try {
                    val retryResponse = apiService.getUserById(userId, "Bearer $newAccessToken")
                    emit(Result.Success(retryResponse))
                } catch (retryException: Exception) {
                    emit(Result.Error("Failed after retry: ${retryException.message}"))
                }
            } else {
                emit(Result.Error("HTTP Error: ${e.message}"))
            }
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.message}"))
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
            val requestBody = "{\"refresh_token\": \"${user.refreshToken}\"}"
                .toRequestBody("application/json".toMediaTypeOrNull())

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