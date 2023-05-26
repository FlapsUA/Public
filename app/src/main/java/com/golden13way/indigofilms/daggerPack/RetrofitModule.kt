package com.golden13way.indigofilms.daggerPack

import android.util.Log
import com.golden13way.indigofilms.API.AccessTokenProviderImpl
import com.golden13way.indigofilms.API.AuthInterceptor
import com.golden13way.indigofilms.AccessTokenProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {

    val baseURL = "https://api.indigofilms.online/api/"


    @Singleton
    @Provides
    fun getRetroFitServiceInterface(retrofit: Retrofit):RetroFitServiceInterface{
        Log.d("TAG", "getRetroFitServiceInterface")
        return retrofit.create(RetroFitServiceInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(accessTokenProvider: AccessTokenProvider): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(AuthInterceptor(accessTokenProvider))
        Log.d("Auth", "provideOkHttpClient")
        return builder.build()
    }

    @Singleton
    @Provides
    fun getRetroFitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }



}

@Module
class AccessTokenModule {
    @Provides
    @Singleton
    fun provideAccessTokenProvider(): AccessTokenProvider {
        return AccessTokenProviderImpl()
    }
}