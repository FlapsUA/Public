package com.golden13way.indigofilms.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.golden13way.indigofilms.API.Answers
import com.golden13way.indigofilms.API.Data
import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.databinding.RecyclerViewComentsListItemBinding


class RVAComments() : RecyclerView.Adapter<RVAComments.MyViewHolder>() {


    private var listData: Films? = null
    private var films: Films? = null


    fun setUpdateData(listData: Films?) {
        this.listData = listData
        Log.d("TAGc", "RecyclerViewAdapter setUpdateData")
    }

    fun callBackData(): Films? {
        val data = listData
        return data
    }


    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val bind =  RecyclerViewComentsListItemBinding.bind(itemView)
        val avatar = bind.ivAvatar
        val nickName = bind.tvNickName
        val comment = bind.tvComment
        val disLike = bind.tvDisLike
        val Like = bind.tvLike
        val subComentsRcvieAdapter = bind.rcSubComments




        fun bind(data: Data, position: Int) {
            Log.d("TAGc", "bind ${data.items.size}")

           // val id = data.items[position].id
            //val genreSize = data.items[position].genres.size
            //var arrayList = arrayListOf<String>()


            /* var i = 0
                 while (i < genreSize){
                     arrayList.add(data.items[position].genres[i].title)
                     i++
            }*/

            nickName.text = data.items[position].user.user_name
            comment.text = (data.items[position].body)
            disLike.text = (data.items[position].dislikes_count.toString())
            Like.text = (data.items[position].likes_count.toString())




            /* genre.text = (

                         arrayList.joinToString(
                     prefix = "",
                     separator = ", ",
                     postfix = ".",
                     truncated = "...",
                     transform = { it.uppercase() })
                 )*/


            Glide.with(avatar)
                .load(data.items[position].user.poster_url)
                .apply(RequestOptions().override(300, 445))
                .into(avatar)


            //button.setOnClickListener(){
            //  acellClickListener.idToMain(position, data)


            /*val main : MainActivity
                    main = MainActivity()
                    main.idToMain(position, data)
                    Log.d("TAG1", "id = ${id}")*/
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVAComments.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_coments_list_item, parent, false)
        Log.d("TAGc", "onCreateViewHolder")

        return RVAComments.MyViewHolder(view) //(view, cellClickListener)

    }

    override fun onBindViewHolder(holder: RVAComments.MyViewHolder, position: Int) {
        Log.d("TAGc", "onBindViewHolder")
        var pos = listData?.data
        if (pos != null) {
            holder.bind(pos, position)
            //var subComents = RVASubComments(pos.items[position].answers)
            //holder.subComentsRcvieAdapter.adapter = subComents
            val subCommentsAdapter = RVASubComments(pos.items[position].answers)
            holder.subComentsRcvieAdapter.adapter = subCommentsAdapter
            holder.subComentsRcvieAdapter.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        }
        //z 2x get(position)!!)

    }

    override fun getItemCount(): Int {
        if (listData?.data?.items?.size == null) {
            Log.d("TAGc", "getItemCount listData null")
            Log.d("TAGc", "${listData?.data?.items?.get(1)?.title.toString()}")
            return 0
        } else {
            Log.d("TAGc", "getItemCount return listData")
            Log.d("TAGc", "${listData!!.data.items.size} size)")

            return listData?.data?.items?.size!!
        }
    }

}

class RVASubComments(private val reply: List<Answers>) : RecyclerView.Adapter<RVASubComments.MyViewHolder>() {


    private var answerData: List<Answers> = reply





    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val bind =  RecyclerViewComentsListItemBinding.bind(itemView)
        val avatar = bind.ivAvatar
        val nickName = bind.tvNickName
        val comment = bind.tvComment
        val disLike = bind.tvDisLike
        val Like = bind.tvLike




        fun bind(answers: List<Answers>, position: Int) {


            // val id = data.items[position].id
            //val genreSize = data.items[position].genres.size
            //var arrayList = arrayListOf<String>()


            /* var i = 0
                 while (i < genreSize){
                     arrayList.add(data.items[position].genres[i].title)
                     i++
                 }*/

            nickName.text = answers[position].user.user_name
            comment.text = (answers[position].body)
            disLike.text = (answers[position].dislikes_count.toString())
            Like.text = (answers[position].likes_count.toString())


            /* genre.text = (

                         arrayList.joinToString(
                     prefix = "",
                     separator = ", ",
                     postfix = ".",
                     truncated = "...",
                     transform = { it.uppercase() })
                 )*/


            /*Glide.with(avatar)
                .load(answers[position].user.poster_small)
                .apply(RequestOptions().override(300, 445))
                .into(avatar)*/

            Glide.with(avatar)
                .load(answers[position].user.poster_small)
                .placeholder(R.drawable.loaderpic)
                .error(R.drawable.error404)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avatar)


            //button.setOnClickListener(){
            //  acellClickListener.idToMain(position, data)


            /*val main : MainActivity
                    main = MainActivity()
                    main.idToMain(position, data)
                    Log.d("TAG1", "id = ${id}")*/
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVASubComments.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_coments_list_item, parent, false)
        Log.d("TAGc", "onCreateViewHolder")
        return RVASubComments.MyViewHolder(view) //(view, cellClickListener)
    }

    override fun onBindViewHolder(holder: RVASubComments.MyViewHolder, position: Int) {
        Log.d("TAGc", "onBindViewHolder")
        var pos = answerData
        if (pos != null) {
            holder.bind(pos, position)


        }
        //get(position)!!)

    }

    override fun getItemCount(): Int {
        if (answerData.size == null) {
            Log.d("TAGc", "getItemCount listData null")

            return 0
        } else {
            Log.d("TAGc", "getItemCount return listData")


            return answerData.size
        }
    }

}

/*
class RVASubComments(private val reply: List<Answers>) : RecyclerView.Adapter<RVASubComments.MyViewHolder>() {


    private var answerData: List<Answers> = reply





    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val avatar = view.ivAvatar
        val nickName = view.tvNickName
        val comment = view.tvComment
        val disLike = view.tvDisLike
        val Like = view.tvLike




        fun bind(answers: List<Answers>, position: Int) {


            // val id = data.items[position].id
            //val genreSize = data.items[position].genres.size
            //var arrayList = arrayListOf<String>()


            */
/* var i = 0
                 while (i < genreSize){
                     arrayList.add(data.items[position].genres[i].title)
                     i++
                 }*//*


            nickName.text = answers[position].user.user_name
            comment.text = (answers[position].body)
            disLike.text = (answers[position].dislikes_count.toString())
            Like.text = (answers[position].likes_count.toString())


            */
/* genre.text = (

                         arrayList.joinToString(
                     prefix = "",
                     separator = ", ",
                     postfix = ".",
                     truncated = "...",
                     transform = { it.uppercase() })
                 )*//*



            Glide.with(avatar)
                .load(answers[position].user.poster_url)
                .apply(RequestOptions().override(300, 445))
                .into(avatar)


            //button.setOnClickListener(){
            //  acellClickListener.idToMain(position, data)


            */
/*val main : MainActivity
                    main = MainActivity()
                    main.idToMain(position, data)
                    Log.d("TAG1", "id = ${id}")*//*

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVASubComments.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_coments_list_item, parent, false)
        Log.d("TAGc", "onCreateViewHolder")
        return RVASubComments.MyViewHolder(view) //(view, cellClickListener)
    }

    override fun onBindViewHolder(holder: RVASubComments.MyViewHolder, position: Int) {
        Log.d("TAGc", "onBindViewHolder")
        var pos = answerData
        if (pos != null) {
            holder.bind(pos, position)
        }
        //get(position)!!)

    }

    override fun getItemCount(): Int {
        if (answerData.size == null) {
            Log.d("TAGc", "getItemCount listData null")

            return 0
        } else {
            Log.d("TAGc", "getItemCount return listData")


            return answerData.size
        }
    }

}

*/

