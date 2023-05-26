package com.golden13way.indigofilms.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.CellClickListener
import com.golden13way.indigofilms.PlayerActivity
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.adapters.RVAComments
import com.golden13way.indigofilms.adapters.RVASubComments
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.FragmentFilmPageBinding
import com.golden13way.indigofilms.dataclass.Auth
import com.golden13way.indigofilms.dataclass.AuthData
import com.golden13way.indigofilms.dataclass.FavId
import com.golden13way.indigofilms.dataclass.FavList

import com.golden13way.indigofilms.viewModels.MainActivityViewModel
import com.google.gson.Gson

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FilmPageFragment : Fragment(){

    private var menuInterface: RetroFitServiceInterface? = null
    lateinit var bind: FragmentFilmPageBinding // ладно не важно чего оно не изменилось
    var pref: SharedPreferences? = null
    lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var rvaComments: RVAComments
    private lateinit var rvaSubComments: RVASubComments
    private lateinit var LiveDataCommentsObserver: Observer<String>
    private var listData: Films? = null
    private var sharedPreferences : SharedPreferences? = null
    lateinit var cellClickListener: CellClickListener
    var pageComments = 1
    var favStatus = false
    var face = ""
    @Inject
    lateinit var mService: RetroFitServiceInterface
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var id : Int? = null
    var favlist : List<Int>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAGFP", "onCreate start")
        super.onCreate(savedInstanceState)
        Log.d("TAGFP", "onCreate")
        arguments?.let {
            param1 = it.getString("id")
            id = param1?.toInt()
            /* param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/

        }
        sharedPreferences = requireActivity()
            .getSharedPreferences("Settings", Context.MODE_PRIVATE)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAGFP", "onCreateView")
        bind = FragmentFilmPageBinding.inflate(inflater)
        (DaggerRetroFitComponent.create()).inject(this)
        Log.d("TAGFP", "onCreateView")
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favCheck()
        initViewModel(id.toString())
        initCommentsViewModel(id.toString(), pageComments.toString())
        Log.d("initCommentsViewModel", "${id.toString()}, ${pageComments.toString()}")
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        favCheck()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilmsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
            fun newInstance(idString : String) =
            FilmPageFragment().apply {
                Log.d("TAGFP", "arguments")
                arguments = Bundle().apply {
                    Log.d("TAGFP", "putString")
                    putString("id", idString)
                    Log.d("TAGFP", "putString +")
                }
            }
    }
    private fun favCheck(){
        face = sharedPreferences?.getString ("face", "").toString()
        favlist = listOf(0)
        favlist = UserData.getUserData()?.data?.favorite_film_ids
        if (favlist?.contains(id) == true) favStatus = true
        if (favlist?.contains(id) == false) favStatus = false
    }
    private fun initViewModel(idString: String) {
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        Log.d("TAGFP", "mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]")
        mainActivityViewModel.getLiveDataObserver()
            .observe(
                viewLifecycleOwner,
                object : Observer<Films?> {
                    override fun onChanged(t: Films?) {
                        if (t != null) {
                            Log.d("TAGFP", "t != null")
                            setUpdateData(t)


                            /*val genreSize = listData?.data?.genres?.size
                            var arrayList = arrayListOf<String>()


                            var i = 0
                            while (i < genreSize!!) {
                                listData?.data?.genres?.get(i)?.let { arrayList.add(it.title) }
                                i++
                            }


                            if (listData?.data?.genres?.isNotEmpty() == true) {

                                val nonNullList = arrayList.filterNotNull()
                                bind.tvFilmGenges.text = (


                                        nonNullList.joinToString(
                                            prefix = "",
                                            separator = ", ",
                                            postfix = ".",
                                            truncated = "...",
                                            transform = { it.uppercase() })
                                        )
                            }*/

                            val shikiRating = listData?.data?.shiki_rating
                            val imdbRating = listData?.data?.imdb_rating

                            if (shikiRating != null && imdbRating != null) {
                                val builder = StringBuilder()
                                builder.append("SHIKIMORI ")
                                builder.append(shikiRating)
                                builder.append("      IMDB ")
                                builder.append(imdbRating)

                                val spannableString = SpannableString(builder.toString())
                                val SHIKIsize = 10 + shikiRating.length
                                // Установить цвет для SHIKIMORI
                                spannableString.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.shiki
                                        )
                                    ),
                                    0,
                                    SHIKIsize,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                // Установить цвет для IMDB
                                val IMDBsize = 5 + imdbRating.length
                                spannableString.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.gold
                                        )
                                    ),

                                    builder.indexOf("IMDB"),
                                    builder.indexOf("IMDB") + IMDBsize,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                bind.tvRaiting.text = spannableString
                            } else if (shikiRating != null) {
                                val spannableString = SpannableString("SHIKIMORI $shikiRating")
                                val size = spannableString.length
                                // Установить цвет для SHIKIMORI
                                spannableString.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.shiki
                                        )
                                    ),
                                    0,
                                    size,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                bind.tvRaiting.text = spannableString
                            } else if (imdbRating != null) {
                                val spannableString = SpannableString("IMDB $imdbRating")
                                val size = spannableString.length
                                // Установить цвет для IMDB
                                spannableString.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.gold
                                        )
                                    ),
                                    0,
                                    size,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                bind.tvRaiting.text = spannableString
                            } else {
                                bind.tvRaiting.text = ""
                            }



                            bind.tvOrigTitle.text = listData?.data?.original_title
                            bind.tvRusTitle.text = listData?.data?.title
                            /*bind.tvFilmYear.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.year_color
                                )
                            )*/
                            if((listData?.data?.year ?: 0) > 0)bind.tvFilmYear.text = (listData?.data?.year.toString())
                            else bind.tvFilmYear.visibility = View.INVISIBLE
                            //bind.tvFilmTime.text = (listData?.data?.runtime.toString() + " минут.")

                            val layoutCountries = bind.LlHCountries // ваш контейнер для TextView
                            if ((listData?.data?.countries?.size ?: 0) > 0) {
                                for (country in listData?.data?.countries!!) {
                                    val textView = TextView(context)
                                    textView.text = country.title

                                    // Задание свойств TextView
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, // ширина
                                        LinearLayout.LayoutParams.WRAP_CONTENT  // высота
                                    )
                                    val dp10 = (10 * resources.displayMetrics.density + 0.5f).toInt()
                                    val dp5 = (5 * resources.displayMetrics.density + 0.5f).toInt()
                                    params.setMargins(dp10, 0, 0, 0)  // Установка отступов
                                    textView.layoutParams = params
                                    textView.background = ContextCompat.getDrawable(requireContext(),
                                        R.drawable.button_def2
                                    )
                                    textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.tt_norms_pro_bold)
                                    textView.setPadding(dp5, dp5, dp5, dp5)
                                    textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                    textView.textSize = 14f

                                    layoutCountries.addView(textView)
                                }
                            }

                            val layoutGenres = bind.LlHGenres // ваш контейнер для TextView
                            if ((listData?.data?.genres?.size ?: 0) > 0) {
                                for (genres in listData?.data?.genres!!) {
                                    val textView = TextView(context)
                                    textView.text = genres.title

                                    // Задание свойств TextView
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, // ширина
                                        LinearLayout.LayoutParams.WRAP_CONTENT  // высота
                                    )
                                    val dp10 = (10 * resources.displayMetrics.density + 0.5f).toInt()
                                    val dp5 = (5 * resources.displayMetrics.density + 0.5f).toInt()
                                    params.setMargins(dp10, 0, 0, 0)  // Установка отступов
                                    textView.layoutParams = params
                                    textView.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_def)
                                    textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.tt_norms_pro_bold)
                                    textView.setPadding(dp5, dp5, dp5, dp5)
                                    textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                    textView.textSize = 14f

                                    layoutGenres.addView(textView)
                                }
                            }
                            //bind.tvFilmCountry.text = (listData?.data?.runtime.toString())
                            bind.tvOverview.text = listData?.data?.overview
                            favCheck()
                            Log.d("favFilmPage", "${id}, ${favStatus}, ${favlist}")
                            if (favStatus) {
                                /*bind.bFav.setText(R.string.remove_fav)
                                bind.bFav.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.heart,
                                    0
                                )*/
                                bind.biHeart.setImageResource(R.drawable.heart)
                            }

                            if (favlist?.contains(id) == true) {
                                bind.biHeart.setImageResource(R.drawable.heart)
                                bind.biHeart.setOnClickListener {
                                    if (favlist?.contains(id) == true) {
                                        //удалить из избраного
                                        val call: Call<FavList>? = mService.postRemFavorites(FavId(id ?: 0))
                                        call?.enqueue(object : Callback<FavList> {
                                            override fun onFailure(call: Call<FavList>, t: Throwable) {
                                                Log.d("Fav", "onFailure favDel")


                                            }

                                            override fun onResponse(
                                                call: Call<FavList?>,
                                                response: Response<FavList?>
                                            ) {
                                                Log.d("Fav", "onResponse favDel")

                                                if (response.isSuccessful) {
                                                    Log.d("Fav", "Del favisSuccessful")
                                                    bind.biHeart.setImageResource(R.drawable.heartw)
                                                    var fav = UserData.getUserData()
                                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                                    val about: String = fav?.data?.about ?: ""
                                                    val email: String = fav?.data?.email ?: ""
                                                    val idUser: Int = fav?.data?.id ?: 0
                                                    val name: String = fav?.data?.name ?: ""
                                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                                    val user_name: String = fav?.data?.user_name ?: ""
                                                    val favids =
                                                        fav?.data?.favorite_film_ids?.filter { it != id }
                                                            ?: listOf(0)
                                                    UserData.setUserData(
                                                        Auth(
                                                            state = true,
                                                            AuthData(
                                                                birth_date,
                                                                about,
                                                                email,
                                                                idUser,
                                                                name,
                                                                poster_small,
                                                                poster_medium,
                                                                poster_large,
                                                                user_name,
                                                                favids
                                                            )
                                                        )
                                                    )
                                                    favlist = UserData.getUserData()?.data?.favorite_film_ids
                                                    favCheck()
                                                    Log.d("favFilmPage", "${id}, ${favStatus}, ${favlist}")
                                                } else {
                                                    Log.d("Fav", "Del favelse")


                                                }
                                            }

                                        })
                                    }
                                    else{
                                        //добавить в избраное
                                        val call: Call<FavList>? = mService.postFavorites(FavId(id ?: 0))
                                        call?.enqueue(object : Callback<FavList> {
                                            override fun onFailure(call: Call<FavList>, t: Throwable) {
                                                Log.d("Fav", "onFailure favDel")


                                            }

                                            override fun onResponse(call: Call<FavList?>, response: Response<FavList?>) {
                                                Log.d("Fav", "onResponse favDel")

                                                if (response.isSuccessful) {
                                                    Log.d("Fav", "Del favisSuccessful")
                                                    bind.biHeart.setImageResource(R.drawable.heart)
                                                    var fav = UserData.getUserData()
                                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                                    val about: String  = fav?.data?.about ?: ""
                                                    val email: String  = fav?.data?.email ?: ""
                                                    val idUser: Int  = fav?.data?.id?: 0
                                                    val name:String = fav?.data?.name ?: ""
                                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                                    val user_name: String = fav?.data?.user_name ?: ""
                                                    val favids = fav?.data?.favorite_film_ids?.plus(id)?.filterNotNull() ?: listOf(0)

                                                    UserData.setUserData(Auth(state = true, AuthData(birth_date,about,email,idUser,name,poster_small,poster_medium,poster_large,user_name,favids)))
                                                    favlist = UserData.getUserData()?.data?.favorite_film_ids
                                                    favCheck()
                                                    Log.d("favFilmPage", "${id}, ${favStatus}, ${favlist}")
                                                }
                                                else {
                                                    Log.d("Fav", "Del favelse")


                                                }
                                            }

                                        })

                                    }
                                }

                            } else {
                                bind.biHeart.setImageResource(R.drawable.heartw)
                                bind.biHeart.setOnClickListener {
                                    if (favlist?.contains(id) == true) {
                                        //удалить из избраного
                                        val call: Call<FavList>? = mService.postRemFavorites(FavId(id ?: 0))
                                        call?.enqueue(object : Callback<FavList> {
                                            override fun onFailure(call: Call<FavList>, t: Throwable) {
                                                Log.d("Fav", "onFailure favDel")


                                            }

                                            override fun onResponse(
                                                call: Call<FavList?>,
                                                response: Response<FavList?>
                                            ) {
                                                Log.d("Fav", "onResponse favDel")

                                                if (response.isSuccessful) {
                                                    Log.d("Fav", "Del favisSuccessful")
                                                    bind.biHeart.setImageResource(R.drawable.heartw)
                                                    var fav = UserData.getUserData()
                                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                                    val about: String = fav?.data?.about ?: ""
                                                    val email: String = fav?.data?.email ?: ""
                                                    val idUser: Int = fav?.data?.id ?: 0
                                                    val name: String = fav?.data?.name ?: ""
                                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                                    val user_name: String = fav?.data?.user_name ?: ""
                                                    val favids =
                                                        fav?.data?.favorite_film_ids?.filter { it != id }
                                                            ?: listOf(0)
                                                    UserData.setUserData(
                                                        Auth(
                                                            state = true,
                                                            AuthData(
                                                                birth_date,
                                                                about,
                                                                email,
                                                                idUser,
                                                                name,
                                                                poster_small,
                                                                poster_medium,
                                                                poster_large,
                                                                user_name,
                                                                favids
                                                            )
                                                        )
                                                    )
                                                    favlist = UserData.getUserData()?.data?.favorite_film_ids
                                                    favCheck()
                                                    Log.d("favFilmPage", "${id}, ${favStatus}, ${favlist}")
                                                } else {
                                                    Log.d("Fav", "Del favelse")


                                                }
                                            }

                                        })
                                    }
                                    else{
                                        //добавить в избраное
                                        val call: Call<FavList>? = mService.postFavorites(FavId(id ?: 0))
                                        call?.enqueue(object : Callback<FavList> {
                                            override fun onFailure(call: Call<FavList>, t: Throwable) {
                                                Log.d("Fav", "onFailure favDel")


                                            }

                                            override fun onResponse(call: Call<FavList?>, response: Response<FavList?>) {
                                                Log.d("Fav", "onResponse favDel")

                                                if (response.isSuccessful) {
                                                    Log.d("Fav", "Del favisSuccessful")
                                                    bind.biHeart.setImageResource(R.drawable.heart)
                                                    var fav = UserData.getUserData()
                                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                                    val about: String  = fav?.data?.about ?: ""
                                                    val email: String  = fav?.data?.email ?: ""
                                                    val idUser: Int  = fav?.data?.id?: 0
                                                    val name:String = fav?.data?.name ?: ""
                                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                                    val user_name: String = fav?.data?.user_name ?: ""
                                                    val favids = fav?.data?.favorite_film_ids?.plus(id)?.filterNotNull() ?: listOf(0)
                                                    UserData.setUserData(Auth(state = true, AuthData(birth_date,about,email,idUser,name,poster_small,poster_medium,poster_large,user_name,favids)))
                                                    favlist = UserData.getUserData()?.data?.favorite_film_ids
                                                    favCheck()
                                                    Log.d("favFilmPage", "${id}, ${favStatus}, ${favlist}")
                                                }
                                                else {
                                                    Log.d("Fav", "Del favelse")


                                                }
                                            }

                                        })

                                    }
                                }

                            }

                            /*if (!favStatus) {
                                *//*bind.bFav.setText(R.string.add_fav)
                                bind.bFav.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.heartw,
                                    0
                                )*//*
                                bind.biHeart.setImageResource(R.drawable.heartw)
                            }
                            //bind.bFav.compoundDrawablePadding = 8 // Значение отступа в пикселях
                            if (face != ""){
                            //bind.bFav.setOnClickListener {
                                bind.biHeart.setOnClickListener {
                                // Обновляем текст, иконку и отступ в зависимости от значения favStatus
                                if (favStatus) {
                                    //удалить из избраного
                                    val call: Call<FavList>? =
                                        mService.postRemFavorites(FavId(id ?: 0))
                                    call?.enqueue(object : Callback<FavList> {
                                        override fun onFailure(call: Call<FavList>, t: Throwable) {
                                            Log.d("Fav", "onFailure favDel")


                                        }

                                        override fun onResponse(
                                            call: Call<FavList?>,
                                            response: Response<FavList?>
                                        ) {
                                            Log.d("Fav", "onResponse favDel")

                                            if (response.isSuccessful) {
                                                *//*Log.d("Fav", "Del favisSuccessful")
                                                bind.bFav.setText(R.string.add_fav)
                                                bind.bFav.setCompoundDrawablesWithIntrinsicBounds(
                                                    0,
                                                    0,
                                                    R.drawable.heartw,
                                                    0
                                                )*//*
                                                bind.biHeart.setImageResource(R.drawable.heartw)
                                                favStatus = false
                                                //bind.bFav.compoundDrawablePadding = 8
                                                var fav = UserData.getUserData()
                                                val birth_date: String = fav?.data?.birth_date ?: ""
                                                val about: String = fav?.data?.about ?: ""
                                                val email: String = fav?.data?.email ?: ""
                                                val id: Int = fav?.data?.id ?: 0
                                                val name: String = fav?.data?.name ?: ""
                                                val poster_small: String =
                                                    fav?.data?.poster_small ?: ""
                                                val poster_medium: String =
                                                    fav?.data?.poster_medium ?: ""
                                                val poster_large: String =
                                                    fav?.data?.poster_large ?: ""
                                                val user_name: String = fav?.data?.user_name ?: ""
                                                val favids =
                                                    fav?.data?.favorite_film_ids?.filter { it != id }
                                                        ?: listOf(0)
                                                UserData.setUserData(
                                                    Auth(
                                                        state = true,
                                                        AuthData(
                                                            birth_date,
                                                            about,
                                                            email,
                                                            id,
                                                            name,
                                                            poster_small,
                                                            poster_medium,
                                                            poster_large,
                                                            user_name,
                                                            favids
                                                        )
                                                    )
                                                )
                                                favCheck()

                                            } else {
                                                Log.d("Fav", "Del favelse")


                                            }
                                        }

                                    })
                                }
                                if (!favStatus) {

                                    val call: Call<FavList>? =
                                        mService.postFavorites(FavId(id ?: 0))
                                    call?.enqueue(object : Callback<FavList> {
                                        override fun onFailure(call: Call<FavList>, t: Throwable) {
                                            Log.d("Fav", "onFailure favDel")


                                        }

                                        override fun onResponse(
                                            call: Call<FavList?>,
                                            response: Response<FavList?>
                                        ) {
                                            Log.d("Fav", "onResponse favDel")

                                            if (response.isSuccessful) {
                                                Log.d("Fav", "Del favisSuccessful")
                                                *//*bind.bFav.setText(R.string.remove_fav)
                                                bind.bFav.setCompoundDrawablesWithIntrinsicBounds(
                                                    0,
                                                    0,
                                                    R.drawable.heart,
                                                    0

                                                )*//*
                                                bind.biHeart.setImageResource(R.drawable.heart)
                                                favStatus = true
                                                //bind.bFav.compoundDrawablePadding = 8
                                                var fav = UserData.getUserData()
                                                val birth_date: String = fav?.data?.birth_date ?: ""
                                                val about: String = fav?.data?.about ?: ""
                                                val email: String = fav?.data?.email ?: ""
                                                val id: Int = fav?.data?.id ?: 0
                                                val name: String = fav?.data?.name ?: ""
                                                val poster_small: String =
                                                    fav?.data?.poster_small ?: ""
                                                val poster_medium: String =
                                                    fav?.data?.poster_medium ?: ""
                                                val poster_large: String =
                                                    fav?.data?.poster_large ?: ""
                                                val user_name: String = fav?.data?.user_name ?: ""
                                                val favids = fav?.data?.favorite_film_ids?.plus(id)
                                                    ?: listOf(0)
                                                UserData.setUserData(
                                                    Auth(
                                                        state = true,
                                                        AuthData(
                                                            birth_date,
                                                            about,
                                                            email,
                                                            id,
                                                            name,
                                                            poster_small,
                                                            poster_medium,
                                                            poster_large,
                                                            user_name,
                                                            favids
                                                        )
                                                    )
                                                )
                                                favCheck()

                                            } else {
                                                Log.d("Fav", "Del favelse")


                                            }
                                        }

                                    })
                                }

                            }
                        }
*/
                            /*CoroutineScope(Dispatchers.IO).launch {
                                if (listData?.data?.imdb_id != null) {
                                    var category = "movie/"
                                    if(listData?.data?.is_anime == true)  { category = "anime/" }
                                        if(listData?.data?.is_serial == true){  category = "tv-series/" }
                                            if(listData?.data?.is_serial == true
                                                &&  listData?.data?.is_anime == true){ category = "anime-tv-series/" }
                                    val  check = ("https://videocdn.tv/api/movies?api_token=mkCYL7WFzktgIqXJ8UTgVr2lZ5ZJknFX&field=imdb_id&query="
                                            + listData?.data?.imdb_id)

                                    val client = OkHttpClient()
                                    val request = Request.Builder().url(check).build()
                                    client.newCall(request).execute().use { response ->

                                        if (response.code == 200) {
                                            var bytes: ByteArray? = response.body?.bytes()
                                            val jsonString: String = bytes?.let { String(it) } ?: ""
                                            val gson = Gson()
                                            val res: VideoCDN =
                                                gson.fromJson(jsonString, VideoCDN::class.java)
                                            if (res.data.size > 0) {val closedID = res.data[0].id


                                            val src =
                                                ("https://12.svetacdn.in/XW3VQUDeE6yi/"+ category + closedID + "?domain=indigofilms.online")
                                            //("https://12.svetacdn.in/XW3VQUDeE6yi?imdb_id=" + listData?.data?.imdb_id)
                                            val client2 = OkHttpClient()
                                            val request2 = Request.Builder().url(src).build()
                                            client2.newCall(request2).execute().use { response ->
                                                if (response.code == 404) {
                                                    // Действия, если ресурс не найден
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        bind.bPlayCdn.visibility = View.VISIBLE
                                                        bind.bPlayCdn.setOnClickListener {
                                                            if (src != null) {
                                                                val myvalue = src
                                                                val myActivity = Intent(
                                                                    context,
                                                                    PlayerActivity::class.java
                                                                ) //name of activity to launch
                                                                myActivity.putExtra("srcTag", myvalue)
                                                                startActivity(myActivity) //to launch another activity
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            }
                                        }

                                    }

                                }
                            }*/

                            CoroutineScope(Dispatchers.IO).launch {
                                if (listData?.data?.imdb_id != null) {
                                    val src = ("https://12.svetacdn.in/XW3VQUDeE6yi?imdb_id="
                                            + listData?.data?.imdb_id
                                            )
                                    val client = OkHttpClient()
                                    val request = Request.Builder()
                                        .url(src)
                                        .header("Referer", "https://indigofilms.online")
                                        .build()
                                    client.newCall(request).execute().use { response ->
                                        if (response.code == 404) {
                                            // Действия, если ресурс не найден
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                bind.llCdn.visibility = View.VISIBLE
                                                bind.llCdn.setOnClickListener {
                                                    if (src != null) {
                                                        val myvalue = src
                                                        val myActivity = Intent(
                                                            context,
                                                            PlayerActivity::class.java
                                                        ) //name of activity to launch
                                                        myActivity.putExtra("srcTag", myvalue)
                                                        startActivity(myActivity) //to launch another activity
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                            CoroutineScope(Dispatchers.IO).launch {
                                if (listData?.data?.is_anime == true) {
                                    if (listData?.data?.shiki_id != null) {
                                        val src =
                                            ("https://kodik.cc/find-player?token=c93d194dd1a2f6cc95b3095a9940dfb2&shikimoriID=" + listData?.data?.shiki_id  + "&uid=RzwvXa")
                                        val check =  ("https://kodikapi.com/search?token=c93d194dd1a2f6cc95b3095a9940dfb2&shikimori_id="
                                                + listData?.data?.shiki_id)
                                        val client = OkHttpClient()
                                        val request = Request.Builder().url(check).build()
                                        client.newCall(request).execute().use { response ->

                                            if (response.code == 200) {
                                                var bytes: ByteArray? = response.body?.bytes()
                                                val jsonString: String = bytes?.let { String(it) } ?: ""
                                                val gson = Gson()
                                                val res: Films = gson.fromJson(jsonString, Films::class.java)
                                                val total = res.total

                                                if (total > 0){
                                                withContext(Dispatchers.Main) {
                                                    bind.llKodic.visibility = View.VISIBLE
                                                    bind.llKodic.setOnClickListener {
                                                        if (src != null) {
                                                            val myvalue = src
                                                            val myActivity = Intent(
                                                                context,
                                                                PlayerActivity::class.java
                                                            ) //name of activity to launch
                                                            myActivity.putExtra("srcTag", myvalue)
                                                            startActivity(myActivity) //to launch another activity
                                                        }
                                                    }
                                                }
                                            }
                                            }
                                        }
                                    }
                                }
                                if (listData?.data?.is_anime == false) {
                                    if (listData?.data?.imdb_id != null) {
                                        val src =
                                            ("https://kodik.cc/find-player?token=c93d194dd1a2f6cc95b3095a9940dfb2&imdbID=" + listData?.data?.imdb_id  + "&uid=RzwvXa")
                                        val check =  ("https://kodikapi.com/search?token=c93d194dd1a2f6cc95b3095a9940dfb2&imdb_id="
                                                        + listData?.data?.imdb_id)
                                        val client = OkHttpClient()
                                        val request = Request.Builder().url(check).build()
                                        client.newCall(request).execute().use { response ->

                                            if (response.code == 200) {
                                                var bytes: ByteArray? = response.body?.bytes()
                                                val jsonString: String = bytes?.let { String(it) } ?: ""
                                                val gson = Gson()
                                                val res: Films = gson.fromJson(jsonString, Films::class.java)
                                                val total = res.total

                                                if (total > 0){
                                                    withContext(Dispatchers.Main) {
                                                        bind.llKodic.visibility = View.VISIBLE
                                                        bind.llKodic.setOnClickListener {
                                                            if (src != null) {
                                                                val myvalue = src
                                                                val myActivity = Intent(
                                                                    context,
                                                                    PlayerActivity::class.java
                                                                ) //name of activity to launch
                                                                myActivity.putExtra("srcTag", myvalue)
                                                                startActivity(myActivity) //to launch another activity
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                                CoroutineScope(Dispatchers.IO).launch {
                                if (listData?.data?.imdb_id != null) {
                                    var src = ("https://base.ashdi.vip/api/product/read_one.php?imdb="
                                            + listData?.data?.imdb_id
                                            + "&api_key=99a660-8378e4-4adb0a-eeeb92-86b677")
                                    val client = OkHttpClient()
                                    val request = Request.Builder().url(src).build()
                                    client.newCall(request).execute().use { response ->
                                        if (response.code == 404) {
                                            // Действия, если ресурс не найден
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                var bytes: ByteArray? = response.body?.bytes()
                                                val jsonString: String = bytes?.let { String(it) } ?: ""
                                                val gson = Gson()
                                                val res: Films = gson.fromJson(jsonString, Films::class.java)
                                                val url = res.url
                                                src = url
                                                bind.llAshvid.visibility = View.VISIBLE
                                                bind.llAshvid.setOnClickListener{
                                                    if (src != null) {
                                                        val myvalue = src
                                                        val myActivity = Intent(
                                                            context,
                                                            PlayerActivity::class.java
                                                        ) //name of activity to launch
                                                        myActivity.putExtra("srcTag", url)
                                                        startActivity(myActivity) //to launch another activity
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            if (listData?.data?.poster != null) {
                                //bind.ivPoster.scaleType = ImageView.ScaleType.FIT_START
                                Glide.with(bind.ivPoster)
                                    .load(listData?.data?.poster)
                                    .placeholder(R.drawable.loaderpic)
                                    .error(R.drawable.error404)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .addListener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                            return false
                                        }

                                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                            // Изображение загружено, прокрутите ScrollView
                                            val middle = bind.ivPoster.height / 2
                                            bind.scrollView.post { bind.scrollView.customSmoothScrollTo(middle,2500) }
                                            return false
                                        }
                                    })
                                    .into(bind.ivPoster)
                            }




                            Log.d("TAGFP", "setUpdateData(t)")
                        } else {
                            Log.d("TAGFP", "error in getting data log")
                            Toast.makeText(context, "error in getting data", Toast.LENGTH_SHORT)
                                .show()


                        }

                    }
                })
        mainActivityViewModel.makeApiCallFilmPage(idString)
        Log.d("TAGFP", "mainActivityViewModel.makeApiCallFilmPage(idString)")
        //bind.bPlay.setOnClickListener{
           /* bind.wvPlayer.apply {
                settings.javaScriptEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.pluginState = WebSettings.PluginState.ON
                settings.builtInZoomControls = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                setBackgroundColor(getResources().getColor(R.color.black))
                bind.wvPlayer.loadData("<iframe src=" + src +
                        " width=\"100%\" height=\"480\" scrolling=\"no\" frameborder=\"0\" allowfullscreen></iframe>", "text/html", "utf-8")
                bind.wvPlayer.visibility = View.VISIBLE

                webViewClient = object: WebViewClient(){
                    override fun doUpdateVisitedHistory(
                        view: WebView?,
                        url: String?,
                        isReload: Boolean
                    ) {
                        super.doUpdateVisitedHistory(view, url, isReload)
                    }
                }
                webChromeClient = object: WebChromeClient(){
                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        super.onShowCustomView(view, callback)
                        bind.wvPlayer.visibility = View.GONE
                        bind.customView.visibility = View.VISIBLE
                        bind.customView.addView(view)
                    }

                    override fun onHideCustomView() {
                        super.onHideCustomView()
                        bind.wvPlayer.visibility = View.VISIBLE
                        bind.customView.visibility = View.GONE
                    }
                }

            }
            //val wvPlayerSetting = bind.wvPlayer.settings
            bind.scrollView.visibility = View.GONE

            cellClickListener.decorView()
            cellClickListener.visibilityBackAndMenu(false)
            //bind.wvPlayer.visibility = View.VISIBLE*/


        //}


    }


    fun closePlayer(){
        bind.wvPlayer.visibility = View.GONE
        bind.wvPlayer.loadUrl("about:blank")
        bind.scrollView.visibility = View.VISIBLE
    }

    fun setUpdateData(listData: Films?){
        this.listData = listData
        Log.d("TAGFP", "FilmPage setUpdateData")
    }


    fun initCommentsViewModel(id: String, page: String) {
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        Log.d("initCommentsViewModel", "2")
        mainActivityViewModel.getLiveDataCommentsObserver()
            .observe(
                viewLifecycleOwner
            ) { t ->
                if (t != null) {
                    Log.d("initCommentsViewModel", "3")
                    rvaComments.setUpdateData(t)
                    rvaComments.notifyDataSetChanged()
                    Log.d("initCommentsViewModel", "4")
                } else {
                    Log.d("initCommentsViewModel", "5")
                    Toast.makeText(
                        context, "error in getting data", Toast.LENGTH_SHORT)
                        .show()


                }
            }
        mainActivityViewModel.makeApiCallComment(id.toString(), page.toString())
        Log.d("TAG", "6")

    }

    fun initRecyclerView() {
        //rcView.layoutManager = GridLayoutManager(this, 3)
        bind.rvComents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //rcView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvaComments = RVAComments()
        bind.rvComents.adapter = rvaComments



           //rvaSubComments = RVASubComments()
        //rvaSubComments*/


    }

}




