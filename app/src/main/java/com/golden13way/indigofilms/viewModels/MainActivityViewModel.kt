package com.golden13way.indigofilms.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.MyApplication
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.dataclass.FavId
import com.golden13way.indigofilms.dataclass.FavList
import com.golden13way.indigofilms.dataclass.Favorites


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var mService: RetroFitServiceInterface

    private lateinit var liveDataList: MutableLiveData<Films?>
    private lateinit var liveComments: MutableLiveData<Films?>
    private lateinit var liveFavorites: MutableLiveData<Favorites?>
    private lateinit var liveSearch: MutableLiveData<Films?>
    private lateinit var liveNew: MutableLiveData<Films?>


    init {
        Log.d("TAG", "MainActivityViewModel init")
        //здесь нужно инициализировать апликешин клас
        getApplication<MyApplication>().getRetroFitComponent().inject(this)
        // (application as MyApplication).getRetroFitComponent().inject(this)
        Log.d("TAG", "as")
        liveDataList = MutableLiveData()
        liveComments = MutableLiveData()
        liveFavorites = MutableLiveData()
        liveSearch = MutableLiveData()
        liveNew = MutableLiveData()
    }
    fun forceSetLiveFavorites(liveDataFavorites: Favorites, filmId: FavId){
        Log.d("Fav", "forceSetLiveFavorites Start")
        Log.d("Fav", "liveDataFavorites: $liveDataFavorites")

        val call: Call<FavList>? = mService.postRemFavorites(filmId)
        call?.enqueue(object : Callback<FavList> {
            override fun onFailure(call: Call<FavList>, t: Throwable) {
                Log.d("Fav", "onFailure Del")
                liveDataList.postValue(null)

            }

            override fun onResponse(call: Call<FavList?>, response: Response<FavList?>) {
                Log.d("Fav", "onResponse Del")

                if (response.isSuccessful) {
                    Log.d("Fav", "Del isSuccessful")


                }
                else {
                    Log.d("Fav", "Del else")


                }
            }

        })




        try {
            liveFavorites.value = liveDataFavorites
            Log.d("Fav", "liveFavorites.value has been set")
        } catch (e: Exception) {
            Log.e("Fav", "Error while setting liveFavorites.value: ${e.message}")
        }
        Log.d("TAG", "makeApiCallFavorites End")

    }



    fun getLiveDataObserver(): MutableLiveData<Films?> {
        Log.d("TAG", "getLiveDataObserver")
        return liveDataList
    }
    fun getLiveDataNewObserver(): MutableLiveData<Films?> {
        Log.d("new", "getLiveDataObserver")
        return liveNew
    }
    fun getLiveDataCommentsObserver(): MutableLiveData<Films?> {
        Log.d("TAG", "getLiveDataObserver")
        return liveComments
    }
    fun getLiveDataFavoritesObserver(): MutableLiveData<Favorites?> {
        Log.d("TAG", "getLiveDataObserver")
        return liveFavorites
    }
    fun getLiveDataSearchObserver(): MutableLiveData<Films?> {
        Log.d("TAG", "getLiveDataObserver")
        return liveSearch
    }


    fun makeApiCallNew() {
        Log.d("new", "makeApiCallNew")
        val call: Call<Films>? = mService.getNew()
        call?.enqueue(object : Callback<Films> {
            override fun onFailure(call: Call<Films>, t: Throwable) {
                liveNew.postValue(null)
            }

            override fun onResponse(call: Call<Films?>, response: Response<Films?>) {
                Log.d("new", "onResponseNew")

                if (response.isSuccessful) {
                    Log.d("new", "onResponse isSuccessfulNew")
                    liveNew.postValue(response.body())

                }
                else {
                    Log.d("new", "onResponse else new")
                    liveNew.postValue(null)

                }
            }

        })
    }

    fun makeApiCallSearch(page:String, find:String) {
        Log.d("TAG", "makeApiCall")
        val call: Call<Films>? = mService.getSearch(page, find)
        call?.enqueue(object : Callback<Films> {
            override fun onFailure(call: Call<Films>, t: Throwable) {
                liveSearch.postValue(null)
            }

            override fun onResponse(call: Call<Films?>, response: Response<Films?>) {
                Log.d("TAG", "onResponse")

                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse isSuccessful")
                    liveSearch.postValue(response.body())

                }
                else {
                    Log.d("TAG", "onResponse else")
                    liveSearch.postValue(null)

                }
            }

        })
    }


    fun makeApiCallFavorites(page:String) {
        Log.d("TAG", "makeApiCallFavorites")
        val call: Call<Favorites>? = mService.getFavorites(page)
        call?.enqueue(object : Callback<Favorites> {
            override fun onFailure(call: Call<Favorites>, t: Throwable) {
                liveDataList.postValue(null)

            }

            override fun onResponse(call: Call<Favorites?>, response: Response<Favorites?>) {
                Log.d("TAG", "onResponse")

                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse isSuccessful")
                    liveFavorites.postValue(response.body())

                }
                else {
                    Log.d("TAG", "onResponse else")
                    liveFavorites.postValue(null)

                }
            }

        })
    }


    fun makeApiCall(page:String, type:String, sort_field:String, sort_direction:String) {
        Log.d("TAG", "makeApiCall")
        val call: Call<Films>? = mService.getDataFromAPI(type, page, sort_field, sort_direction)
        call?.enqueue(object : Callback<Films> {
            override fun onFailure(call: Call<Films>, t: Throwable) {
                liveDataList.postValue(null)
            }

            override fun onResponse(call: Call<Films?>, response: Response<Films?>) {
                Log.d("TAG", "onResponse")

                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse isSuccessful")
                    liveDataList.postValue(response.body())

                }
                else {
                    Log.d("TAG", "onResponse else")
                    liveDataList.postValue(null)

                }
            }

        })
    }

    /*fun makeCommentsApiCall(page:String, type:String) {
        Log.d("TAG", "makeApiCall")
        val call: Call<Films>? = mService.getDataFromAPI(page, type)
        call?.enqueue(object : Callback<Films> {
            override fun onFailure(call: Call<Films>, t: Throwable) {
                liveDataList.postValue(null)
            }

            override fun onResponse(call: Call<Films?>, response: Response<Films?>) {
                Log.d("TAG", "onResponse")

                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse isSuccessful")
                    liveDataList.postValue(response.body())

                }
                else {
                    Log.d("TAG", "onResponse else")
                    liveDataList.postValue(null)

                }
            }

        })
    }*/

    fun makeApiCallFilmPage(id: String) {
        Log.d("TAG", "makeApiCall")
        val call: Call<Films>? = mService.getFlimsByID(id)
        call?.enqueue(object : Callback<Films> {
            override fun onFailure(call: Call<Films>, t: Throwable) {
                liveDataList.postValue(null)
            }

            override fun onResponse(call: Call<Films?>, response: Response<Films?>) {
                Log.d("TAG", "onResponse")

                if (response.isSuccessful) {
                    Log.d("TAG", "onResponse isSuccessful")
                    liveDataList.postValue(response.body())

                }
                else {
                    Log.d("TAG", "onResponse else")
                    liveDataList.postValue(null)


                }
            }

        })
    }

    fun makeApiCallComment(id: String, page: String) {
        Log.d("makeApiCallComment", "makeApiCommentsCall")
        val call: Call<Films>? = mService.getComments(id.toString(), page.toString())
        Log.d("makeApiCallComment", "mService.getComments(${id.toString()}, ${page.toString()})")
        call?.enqueue(object : Callback<Films> {
            override fun onFailure(call: Call<Films>, t: Throwable) {
                Log.e("makeApiCallComment", "onFailure: ${t.message}")
                liveComments.postValue(null)
                Log.d("makeApiCallComment", "liveComments.postValue(null)")
            }

            override fun onResponse(call: Call<Films?>, response: Response<Films?>) {
                Log.d("makeApiCallComment", "onResponse")

                if (response.isSuccessful) {
                    Log.d("makeApiCallComment", "onResponse isSuccessful")
                    liveComments.postValue(response.body())


                }
                else {
                    Log.d("makeApiCallComment", "onResponse else")
                    liveComments.postValue(null)

                }
            }

        })
    }

}