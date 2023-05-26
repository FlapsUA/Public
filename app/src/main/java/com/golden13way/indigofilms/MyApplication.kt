package com.golden13way.indigofilms

import android.app.Application
import android.content.Context
import android.util.Log
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitComponent

import com.golden13way.indigofilms.daggerPack.RetrofitModule

class MyApplication : Application() {

    private lateinit var retroFitComponent: RetroFitComponent

    lateinit var appComponent: RetroFitComponent
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", "MyApplication onCreate")
        retroFitComponent = DaggerRetroFitComponent.builder()
            .retrofitModule(RetrofitModule())
            .build()
        Log.d("TAG", "retroFitComponent onCreate")

    }

    fun getRetroFitComponent() : RetroFitComponent{
        Log.d("TAG", "getRetroFitComponent")
        return retroFitComponent
    }


}
val Context.retroFitComponent: RetroFitComponent
    get() = when (this) {
        is MyApplication -> appComponent
        else -> applicationContext.retroFitComponent
    }


