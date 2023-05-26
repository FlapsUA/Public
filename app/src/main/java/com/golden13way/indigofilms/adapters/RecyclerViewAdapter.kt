package com.golden13way.indigofilms.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.golden13way.indigofilms.CellClickListener
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.API.Items
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.FilmsItemBinding
import com.golden13way.indigofilms.dataclass.Auth
import com.golden13way.indigofilms.dataclass.AuthData
import com.golden13way.indigofilms.dataclass.FavId
import com.golden13way.indigofilms.dataclass.FavList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecyclerViewAdapter(val cellClickListener: CellClickListener, private val context: Context, private val mService: RetroFitServiceInterface) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private var listData: MutableList<Films?> = mutableListOf()
    private var films: Films? = null
    private var pageCount = 0






    fun setUpdateData(newData: MutableList<Films?>) {

        if (this.listData.size == 0) {


            this.listData.addAll(newData)
            pageCount++
            notifyDataSetChanged()
            Log.d("clearData", "fun clearData no Hentai!!!")
        } else {
            if (this.listData[0]?.data?.pagination?.total_pages!! > pageCount) {
                this.listData.addAll(newData)
                pageCount++
                notifyDataSetChanged()
            }
        }
    }


    fun clearData(){
        listData.clear()
        pageCount = 0
        notifyDataSetChanged()
        Log.d("clearData" ,"fun clearData do Hentai!!!")
    }


    fun callBackData(): MutableList<Films?> {
        return listData
    }

    class MyViewHolder(view: View, val cellClickListener: CellClickListener, private val mService: RetroFitServiceInterface): RecyclerView.ViewHolder(view) {
        private val bind =  FilmsItemBinding.bind(itemView)
        val poster = bind.im
        val filmName = bind.tvName
        val year = bind.tvYear
        val genre = bind.tvGenre
        val rating = bind.tvRating
        val button = bind.button
        val acellClickListener = cellClickListener
        val context = itemView.context
        val buttonFav = bind.biHeart
        var favlist = UserData.getUserData()?.data?.favorite_film_ids


        fun bind(item: Items?, position: Int, data: List<Items>) {
            if (item == null) return
            if (item.genres.isNotEmpty()) {
                if (!item.genres[0].title.isNullOrEmpty() && item.genres[0].title.isNotBlank()) {
                    val text = item.genres[0].title
                    genre.text = (text + ", ")
                }
            }

            val shikiRating = item.shiki_rating
            val imdbRating = item.imdb_rating

            if (shikiRating != null && imdbRating != null) {
                val builder = StringBuilder()
                builder.append("SHIKIMORI ")
                builder.append(shikiRating)
                builder.append("\nIMDB ")
                builder.append(imdbRating)

                val spannableString = SpannableString(builder.toString())

                // Установить цвет для SHIKIMORI
                val SHIKIsize = 10 + shikiRating.length
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.shiki)),
                    0,
                    SHIKIsize,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Установить цвет для IMDB
                val IMDBsize = 5 + imdbRating.length

                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.gold)),
                    builder.indexOf("IMDB"),
                    builder.indexOf("IMDB") + IMDBsize,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                rating.text = spannableString
            } else if (shikiRating != null) {
                val spannableString = SpannableString("SHIKIMORI $shikiRating")
                val size = spannableString.length
                // Установить цвет для SHIKIMORI
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.shiki)),
                    0,
                    size,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                rating.text = spannableString
            } else if (imdbRating != null) {
                val spannableString = SpannableString("IMDB $imdbRating")
                val size= spannableString.length
                // Установить цвет для IMDB
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.gold)),
                    0,
                    size,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                rating.text = spannableString
            } else {
                rating.text = ""
            }

            filmName.text = item.title
            year.setTextColor(ContextCompat.getColor(context, R.color.year_color))
            year.text = (item.year.toString())


            Glide.with(poster)
                .load(item.poster_small)
                .placeholder(R.drawable.loaderpic)
                .error(R.drawable.error404)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(poster)

            button.setOnClickListener() {
                acellClickListener.idToMain(position, data)
            }
           /* buttonFav.setOnClickListener {
                acellClickListener.idToMainFav(position, data)
            }
*/

           if (favlist?.contains(item.id) == true) {
                buttonFav.setImageResource(R.drawable.heart)
                buttonFav.setOnClickListener {
                    if (favlist?.contains(item.id) == true) {
                        //удалить из избраного
                        val call: Call<FavList>? = mService.postRemFavorites(FavId(item.id))
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
                                    buttonFav.setImageResource(R.drawable.heartw)
                                    var fav = UserData.getUserData()
                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                    val about: String = fav?.data?.about ?: ""
                                    val email: String = fav?.data?.email ?: ""
                                    val id: Int = fav?.data?.id ?: 0
                                    val name: String = fav?.data?.name ?: ""
                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                    val user_name: String = fav?.data?.user_name ?: ""
                                    val favids =
                                        fav?.data?.favorite_film_ids?.filter { it != item.id }
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
                                    favlist = UserData.getUserData()?.data?.favorite_film_ids

                                } else {
                                    Log.d("Fav", "Del favelse")


                                }
                            }

                        })
                    }
                    else{
                        //добавить в избраное
                        val call: Call<FavList>? = mService.postFavorites(FavId(item.id))
                        call?.enqueue(object : Callback<FavList> {
                            override fun onFailure(call: Call<FavList>, t: Throwable) {
                                Log.d("Fav", "onFailure favDel")


                            }

                            override fun onResponse(call: Call<FavList?>, response: Response<FavList?>) {
                                Log.d("Fav", "onResponse favDel")

                                if (response.isSuccessful) {
                                    Log.d("Fav", "Del favisSuccessful")
                                    buttonFav.setImageResource(R.drawable.heart)
                                    var fav = UserData.getUserData()
                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                    val about: String  = fav?.data?.about ?: ""
                                    val email: String  = fav?.data?.email ?: ""
                                    val id: Int  = fav?.data?.id?: 0
                                    val name:String = fav?.data?.name ?: ""
                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                    val user_name: String = fav?.data?.user_name ?: ""
                                    val favids = fav?.data?.favorite_film_ids?.plus(item.id) ?: listOf(0)
                                    UserData.setUserData(Auth(state = true, AuthData(birth_date,about,email,id,name,poster_small,poster_medium,poster_large,user_name,favids)))
                                    favlist = UserData.getUserData()?.data?.favorite_film_ids

                                }
                                else {
                                    Log.d("Fav", "Del favelse")


                                }
                            }

                        })

                    }
                }

            } else {
                buttonFav.setImageResource(R.drawable.heartw)
                buttonFav.setOnClickListener {
                    if (favlist?.contains(item.id) == true) {
                        //удалить из избраного
                        val call: Call<FavList>? = mService.postRemFavorites(FavId(item.id))
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
                                    buttonFav.setImageResource(R.drawable.heartw)
                                    var fav = UserData.getUserData()
                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                    val about: String = fav?.data?.about ?: ""
                                    val email: String = fav?.data?.email ?: ""
                                    val id: Int = fav?.data?.id ?: 0
                                    val name: String = fav?.data?.name ?: ""
                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                    val user_name: String = fav?.data?.user_name ?: ""
                                    val favids =
                                        fav?.data?.favorite_film_ids?.filter { it != item.id }
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
                                    favlist = UserData.getUserData()?.data?.favorite_film_ids

                                } else {
                                    Log.d("Fav", "Del favelse")


                                }
                            }

                        })
                    }
                    else{
                        //добавить в избраное
                        val call: Call<FavList>? = mService.postFavorites(FavId(item.id))
                        call?.enqueue(object : Callback<FavList> {
                            override fun onFailure(call: Call<FavList>, t: Throwable) {
                                Log.d("Fav", "onFailure favDel")


                            }

                            override fun onResponse(call: Call<FavList?>, response: Response<FavList?>) {
                                Log.d("Fav", "onResponse favDel")

                                if (response.isSuccessful) {
                                    Log.d("Fav", "Del favisSuccessful")
                                    buttonFav.setImageResource(R.drawable.heart)
                                    var fav = UserData.getUserData()
                                    val birth_date: String = fav?.data?.birth_date ?: ""
                                    val about: String  = fav?.data?.about ?: ""
                                    val email: String  = fav?.data?.email ?: ""
                                    val id: Int  = fav?.data?.id?: 0
                                    val name:String = fav?.data?.name ?: ""
                                    val poster_small: String = fav?.data?.poster_small ?: ""
                                    val poster_medium: String = fav?.data?.poster_medium ?: ""
                                    val poster_large: String = fav?.data?.poster_large ?: ""
                                    val user_name: String = fav?.data?.user_name ?: ""
                                    val favids = fav?.data?.favorite_film_ids?.plus(item.id) ?: listOf(0)
                                    UserData.setUserData(Auth(state = true, AuthData(birth_date,about,email,id,name,poster_small,poster_medium,poster_large,user_name,favids)))
                                    favlist = UserData.getUserData()?.data?.favorite_film_ids

                                }
                                else {
                                    Log.d("Fav", "Del favelse")


                                }
                            }

                        })

                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.films_item, parent, false)

        return MyViewHolder(view, cellClickListener, mService)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, ) {
        val items = listData.flatMap { it?.data?.items ?: emptyList() }

            val item = items?.get(position)

        if (item != null) {

                holder.bind(item, position, items)
            }

    }

    override fun getItemCount(): Int {
        return listData.sumBy { it?.data?.items?.size ?: 0 }
    }
}