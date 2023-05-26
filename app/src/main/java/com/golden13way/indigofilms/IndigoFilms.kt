package com.golden13way.indigofilms

import android.app.Application
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IndigoFilms : Application() {

lateinit var iF : IndigoFilms


    override fun onCreate() {
        super.onCreate()
        configureRetrofit()

    }


    private fun configureRetrofit(){
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            //.baseUrl(R.string.BASE_URL.toString())
            .baseUrl("http://www.indigo-films.herokuapp.com/api/")

            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        iF = retrofit.create(IndigoFilms::class.java)
    }

}