package com.golden13way.indigofilms.API

import android.util.Log
import com.golden13way.indigofilms.dataclass.Auth

class UserData {

    private var userData: Auth? = null

    fun getUserData(): Auth? {
        Log.d("Auth", "get accessToken = token $userData")
        return userData
    }

    fun setUserData(data: Auth) {
        userData = data
        Log.d("Auth", "set accessToken = token $userData")
    }

    companion object {
        private var instance: UserData? = null

        fun getInstance(): UserData {
            if (instance == null) {
                instance = UserData()
            }
            return instance!!
        }

        fun getUserData(): Auth? {
            return getInstance().getUserData()
        }

        fun setUserData(data: Auth) {
            getInstance().setUserData(data)
        }
    }
}