package com.golden13way.indigofilms.API


data class Films(
    var `data`: Data,
    var state: Boolean,
    var url: String,
    var total: Int
)

data class VideoCDN(
    var result: Boolean,
    var data: List<DataForCDN>,

)

data class DataForCDN(
    var id: Int
)


data class Data(
    var genres: List<Genre>,
    var items: List<Items>,
    var pagination: Pagination,
    var id: Int,
    var original_title : String,
    var original_language: String,
    var poster: String,
    var poster_small: String,
    var poster_medium: String,
    var runtime: Int,
    var release_date : String,
    var year: Int,
    var imdb_id : String,
    var imdb_rating : String,
    var shiki_id : String,
    var shiki_rating : String,
    var is_anime : Boolean,
    var is_serial : Boolean,
    var title : String,
    var overview : String,
    var stars : Int,
    var slug : String,
    var category : String,
    var new : List<Items>,
    var countries : List<Countries>
)

data class Countries(
    var id:Int,
    var name: String,
    var title: String
)

data class Items(
    var genres: List<Genre>,
    var id: Int,
    var film_id: Int,
    var body: String,
    var imdb_rating: String,
    var shiki_rating: String,
    var poster: String,
    var poster_small: String,
    var poster_medium: String,
    var title: String,
    var year: Int,
    var user: User,
    var answers: List<Answers>,
    var like: Int,
    var likes_count: Int,
    var dislikes_count: Int,
    var created_at: String
)
data class Answers(
    var id: Int,
    var user: User,
    var film_id: Int,
    var parent_id: Int,
    var body: String,
    var like: Int,
    var likes_count: Int,
    var dislikes_count: Int,
    var created_at: String,



)
data class User(
    var id: Int,
    var name: String,
    var user_name: String,
    var film_id: Int,
    var parent_id: Int,
    var body: String,
    var like: Int,
    var likes_count: Int,
    var dislikes_count: Int,
    var created_at: Int,
    var poster_url: String,
    var poster_small: String

)
data class Pagination(
    var count: Int,
    var current_page: Int,
    var per_page: Int,
    var total: Int,
    var total_pages: Int
)

data class Genre(
    var id: Int,
    var name: String,
    var slug: String,
    var title: String
)

