package com.golden13way.indigofilms.API

import android.util.Log
import com.golden13way.indigofilms.AccessTokenProvider
import javax.inject.Inject

class AccessTokenProviderImpl @Inject constructor() : AccessTokenProvider {

    private var accessToken: String? = null

    override fun getAccessToken(): String {
        Log.d("Auth", "get accessToken = token $accessToken")
        return accessToken ?: ""
    }

    fun setAccessToken(token: String) {
        accessToken = token
        Log.d("Auth", "set accessToken = token $accessToken")
    }

    companion object {
        private var instance: AccessTokenProviderImpl? = null

        fun getInstance(): AccessTokenProviderImpl {
            if (instance == null) {
                instance = AccessTokenProviderImpl()
            }
            return instance!!
        }

        fun getAccessToken(): String {
            return getInstance().getAccessToken()
        }

        fun setAccessToken(token: String) {
            getInstance().setAccessToken(token)
        }
    }
}