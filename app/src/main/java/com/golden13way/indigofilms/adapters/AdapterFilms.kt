package com.golden13way.indigofilms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.databinding.FilmsItemBinding
import com.golden13way.indigofilms.dataclass.Auth


class AdapterFilms: RecyclerView.Adapter<FilmsHolder>() {
    var dataFilmsList = ArrayList<Films>()
    lateinit var userData : Auth
    var favlist = listOf<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.films_item, parent, false)
        try {
            userData = UserData.getUserData()!!
        } catch (e: Exception) {
            // обработка исключения
        }
        if(userData.data.favorite_film_ids != null){}
        var favlist = userData.data.favorite_film_ids
        return FilmsHolder(view)
    }

    override fun onBindViewHolder(holder: FilmsHolder, position: Int) {
        holder.binding(dataFilmsList[position])

    }

    override fun getItemCount(): Int {
        return dataFilmsList.size
    }

    override fun onBindViewHolder(holder: FilmsHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)

        Glide.with(holder.bind.im)
            .load(dataFilmsList[position].data.items[position].poster_small)
            .into(holder.bind.im)

        holder.bind.tvName.text = dataFilmsList[position].data.items[position].title.toString()

    }

    fun addContent(Films: Films){
        dataFilmsList.add(Films)
        notifyDataSetChanged()
    }
}

class FilmsHolder(item: View): RecyclerView.ViewHolder(item) {
        val bind = FilmsItemBinding.bind(item)

            fun binding(Films: Films) = with(bind){
                /*Picasso.get().load(Films.data.items[]).into(im)

                tvName.text =  Films.FilmName*/
            }
}



/*

getComments(id,page = 1): Observable<any> {
    return this.get("films/"+id+"/get_comments", {page:page} )
}

postComment(filmId:number,body,parentId=null): Observable<any> {
    return this.post("comments/store", {
            filmId:filmId, body:body, parentId:parentId} )
}

postLike(comment_id:number,is_like): Observable<any> {
    return this.post("comments/like", {
            comment_id:comment_id, is_like:is_like} )
}

postUnLike(comment_id:number): Observable<any> {
    return this.post("comments/unlike", {
            comment_id:comment_id} )
}

postFavorite(film_id): Observable<any> {
    return this.post("favorite-films/add", {
            film_id:film_id
    })
}
removeFavorite(film_id): Observable<any> {
    return this.post("favorite-films/remove", {
            film_id:film_id
    })
}

userGet(user_id): Observable<any> {
    return this.get("users/"+ user_id)
}

getFavoriteArray(): Observable<any> {
    return this.get("favorite-films/all-ids")
}
getFavoriteFilms(): Observable<any> {
    return this.get("favorite-films/all")
}



search(find, page): Observable<any> {
    return this.get("films/search", {page:page, find:find } )
}

getData(type = "", page = 1): Observable<any> {
    let params = {page:page}
    if(type) {
        params["type"] = type
    }
    return this.get("films", params )
}

getfind(id): Observable<any> {
    return this.get("films/"+ id)
}

getGenreFilms(id,page, type): Observable<any> {
    return this.get("films/genre/" + id, {page:page, type:type} )
}

getGenre(is_anime:number=0): Observable<any> {
    return this.get("genres", {is_anime:is_anime} )*/
