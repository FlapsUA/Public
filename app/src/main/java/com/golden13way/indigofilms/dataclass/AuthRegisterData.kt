package com.golden13way.indigofilms.dataclass

import com.golden13way.indigofilms.API.Items
import com.golden13way.indigofilms.API.Pagination

data class Register(
    val email : String,
    val name : String,
    val password : String,
    val confirmPassword : String
    )

data class ResponseRegisterLogin(
    val state : Boolean,
    val data : ResponseRegisterData
)

data class ResponseRegisterData(
    val access_token : String
)

data class Login(
    val email: String,
    val password: String
)

data class Auth(
    val state : Boolean,
    val data : AuthData
    )

data class AuthData(
    val birth_date: String,
    val about: String,
    val email: String,
    val id: Int,
    val name:String,
    val poster_small: String,
    val poster_medium: String,
    val poster_large: String,
    val user_name: String,
    val favorite_film_ids: List<Int>
)

data class Password(
    val password: String
)

data class ChangeProfile(
    val name : String,
    val user_name: String,
    val about: String
)

data class Favorites(
    val data: FavDATA,
    val state: Boolean
)

data class FavDATA(
    val items : List<Items>,
    val pagination : Pagination
)
data class FavId(
    val film_id: Int
)
data class FavList(
    val data: FavListData,
    val state: Boolean
)

data class FavListData(
    val favorite_ids: List<Int>
)

data class TelegramAuth(
    val data : TG
)
data class TG(
   val auth_date: Int,
   val first_name: String,
   val hash:String,
   val id: Int,
   val photo_url: String,
   val username: String
)