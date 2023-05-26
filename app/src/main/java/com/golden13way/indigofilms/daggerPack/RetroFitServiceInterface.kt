package com.golden13way.indigofilms.daggerPack


import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.dataclass.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface RetroFitServiceInterface {
    @GET("films")
    fun getDataFromAPI(
        @Query("category")type: String,
        @Query("page")page: String,
        @Query("sort_field")sort_field: String,
        @Query("sort_direction")sort_direction: String
    ): Call<Films>?
    @GET("films/{id}")
    fun getFlimsByID(@Path("id") searchById: String): Call<Films>?

    @GET("films/{id}/get_comments")
    fun getComments(@Path("id") searchById: String, @Query("page")page: String) : Call<Films>?

    @POST("auth/register")
    fun postRegister(@Body data: Register) : Call<ResponseRegisterLogin>

    @POST("auth/login")
    fun postLogin(@Body data: Login) : Call<ResponseRegisterLogin>


    @GET("auth/me")
    fun getAuth(): Call<Auth>

    @POST("auth/logout")
    fun postLogout()

    @POST("users/change-pass")
    fun postChangePass(@Body data: Password) : Call<ResponseRegisterLogin>

    @POST("users/change-info")
    fun postChangeUserProfile(@Body data: ChangeProfile) : Call<ResponseRegisterLogin>

    @GET("favorite-films/all")
    fun getFavorites(@Query("page")page: String): Call<Favorites>

    @POST("favorite-films/add")
    fun postFavorites(@Body data: FavId) : Call<FavList>

    @POST("favorite-films/remove")
    fun postRemFavorites(@Body data: FavId) : Call<FavList>

    @Multipart
    @POST("users/change-picture")
    fun uploadPicture(@Part picture: MultipartBody.Part): Call<Auth>

    @POST("auth/telegram")
    fun postAuthTelegram(@Body data: TelegramAuth) : Call<ResponseRegisterLogin>

    @GET("films/search")
    fun getSearch(@Query("page")page: String, @Query("find")find: String): Call<Films>?

    @GET("films/main")
    fun getNew() : Call<Films>?

}