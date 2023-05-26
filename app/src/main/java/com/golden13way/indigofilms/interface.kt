package com.golden13way.indigofilms

import com.golden13way.indigofilms.API.Items


interface CellClickListener {
    fun idToMain(pos: Int, data: List<Items>)
    fun visibilityBackAndMenu(state: Boolean)
    fun decorView()
    fun idToMainFav(pos: Int, data: List<Items>)
}


interface AccessTokenProvider {
    fun getAccessToken(): String
}