package com.golden13way.indigofilms.API

import android.util.Log
import com.golden13way.indigofilms.AccessTokenProvider
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface

import com.golden13way.indigofilms.dataclass.Auth
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Authorization {

    @Inject
    lateinit var mService: RetroFitServiceInterface

    init {
        val retrofitComponent = DaggerRetroFitComponent.create()
        retrofitComponent.inject(this)
    }

    // измененный метод authorization, который возвращает Unit
    fun authorization(callback: (Auth?) -> Unit) {

        val call: Call<Auth>? = mService.getAuth()
        call?.enqueue(object : Callback<Auth> {
            override fun onFailure(call: Call<Auth>, t: Throwable) {
                Log.e("RegisterFragment", "Ошибка запроса: ${t.message}")
                t.printStackTrace()
                callback(null) // вызываем callback, передавая null в качестве параметра
            }

            override fun onResponse(
                call: Call<Auth>,
                response: Response<Auth>
            ) {
                Log.d("TAG", "onResponse")

                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse isSuccessful")
                    val user = response.body()
                    callback(user) // вызываем callback, передавая объект Auth в качестве параметра
                } else {
                    Log.d("TAG", "onResponse else")
                    callback(null) // вызываем callback, передавая null в качестве параметра
                }
            }
        })
    }
}

class AuthInterceptor(private val accessTokenProvider: AccessTokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val accessToken = AccessTokenProviderImpl.getAccessToken()
        Log.d("Auth", "intercept ${accessToken}")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}