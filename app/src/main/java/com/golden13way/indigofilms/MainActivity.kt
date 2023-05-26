package com.golden13way.indigofilms

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.golden13way.indigofilms.API.*
import com.golden13way.indigofilms.adapters.AdapterNew
import com.golden13way.indigofilms.adapters.AdapterSearch
import com.golden13way.indigofilms.adapters.RecyclerViewAdapter
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.ActivityMainBinding

import com.golden13way.indigofilms.fragments.FavoritesFragment
import com.golden13way.indigofilms.fragments.FilmPageFragment
import com.golden13way.indigofilms.fragments.ProfileFragment
import com.golden13way.indigofilms.fragments.SearchFragment

import com.golden13way.indigofilms.viewModels.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity(), CellClickListener{
    var pref : SharedPreferences? = null
    var faceStatus = "NULL"
    var page = "1"
    var type = "film"
    var id : Int = 0
    lateinit var mainActivityViewModel: MainActivityViewModel
    lateinit var BASE_URL : String
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var adapterNew: AdapterNew
    private var currentPage = 1
    val authorization = Authorization()
    lateinit var bind : ActivityMainBinding
    @Inject
    lateinit var mService: RetroFitServiceInterface
    var sort_field = "release_date"
    var sort_direction = "desc"
    var shiki = false

//z 2x





    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        (DaggerRetroFitComponent.create()).inject(this)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(bind.root)
        val fragmentManager: FragmentManager = supportFragmentManager
        SharedPreferences()
        giveToken()
        toolBarSetUp()
        botNavMenuStarter()
        //supportFragmentManager.beginTransaction().replace(R.id.FragmentMain, FilmsFragment.newInstance()).commit() // main fragment
        //supportFragmentManager.beginTransaction().replace(R.id.FragmentMain, AuthFragment.newInstance(faceStatus)).commit() // Auth fragment



            BASE_URL = getString(R.string.BASE_URL)
        //getFilmsList()
        tryAuth()
        initRecyclerView()
        initRecyclerViewNew()
        initViewModel(page, type)
        //botNavMenu()

        //buttonOnClickListener()

        categoriesClickListeners()
        visibilityFilmPageFragment(false)
        visibilityRecyclerView(false)
        visibilityMainPage(true)
        //startFragmentFilmPage()


        /*foo by Delegates.observable(id){ property, oldValue, newValue ->
            Log.d("TAG1", "foo")
            visibilityMainPage(false)
            visibilityRecyclerView(false)
            visibilityFilmPageFragment(true)
        }*/

        
    }

    fun toolBarSetUp(){
        bind.toolbarMain.backButton.visibility  = View.INVISIBLE
        bind.toolbarMain.filterButton.visibility  = View.INVISIBLE
        bind.toolbarMain.sortButton.visibility = View.INVISIBLE
        bind.toolbarMain.searchButton.visibility = View.VISIBLE
        bind.toolbarMain.centerTitle.text = (getString(R.string.app_name))
        bind.toolbarMain.centerTitle.letterSpacing = 0.1f
        val typeface = ResourcesCompat.getFont(this, R.font.tt_norms_pro_bold)
        bind.toolbarMain.centerTitle.setTypeface(typeface, Typeface.BOLD)

    }

    fun giveToken(){

        val face = pref?.getString("face", "").toString()
        Log.d("Auth", "GiveToken ${face}")
        AccessTokenProviderImpl.setAccessToken(face)
    }

    override fun onStop() {
        super.onStop()

    }

     override fun idToMain(pos: Int, data: List<Items>){
        if (data != null) {
            id = data[pos].id.toInt()

            startFragmentFilmPage(id)
        }
    }


fun startFragmentFilmPage(id: Int){
  // works!
                visibilityMainPage(false)
                visibilityRecyclerView(false)
                visibilityFilmPageFragment(true) // example accessing the newValue
    Log.d("frag","FilmPageFragment start")
    bind.toolbarMain.backButton.visibility  = View.INVISIBLE
    bind.toolbarMain.filterButton.visibility  = View.INVISIBLE
    bind.toolbarMain.sortButton.visibility = View.INVISIBLE
    bind.toolbarMain.searchButton.visibility = View.VISIBLE
    bind.toolbarMain.centerTitle.text = (getString(R.string.app_name))
    supportFragmentManager.beginTransaction().replace(R.id.FilmPageFragmentConteiner, FilmPageFragment.newInstance(id.toString())).commit()

}

    override fun decorView(){
        val flag : Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = flag
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
       // onWindowFocusChanged(false)
    }

    override fun idToMainFav(pos: Int, data: List<Items>) {
        TODO("Not yet implemented")
    }

    /*public override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            var v = window.decorView.systemUiVisibility
            //v = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            v = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            *//*v = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            v = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            v = View.SYSTEM_UI_FLAG_FULLSCREEN
            v = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            v = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*//*
        }

    }*/

    private fun tryAuth(){
        Log.d("Auth", "Старт tryAuth")
        val face = pref?.getString("face", "")
        Log.d("Auth", "blindface ${face}")
        if(face != null && face != "" && face != "NULL"){
            Log.d("Auth", "no fail blindface")
            authorization.authorization() { auth ->
                if (auth != null) {
                    // если ответ не null, то здесь обрабатываем данные токена авторизации (Auth)
                    if(auth.state){
                        Toast.makeText(this, "authorization пройдена ${auth.data.name}", Toast.LENGTH_SHORT).show()
                        Log.d("Auth", "Успех запроса авторизации")
                        UserData.setUserData(auth)
                        botNavMenu()
                    }
                } else {
                    // если ответ null, то здесь обрабатываем ошибку
                    Toast.makeText(this, "authorization fail}", Toast.LENGTH_SHORT).show()
                    Log.d("Auth", "Ошибка запроса авторизации")
                    botNavMenu()
                }
            }
        }
        else { Log.d("Auth", "Токена нет не залогинены")
            botNavMenu()
        }
    }

    private fun SharedPreferences() {
        //shared Preferences
        pref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        faceStatus = pref?.getString(faceStatus, "NULL")!!
    }

    fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        bind.rcView.layoutManager =  layoutManager

        recyclerViewAdapter = RecyclerViewAdapter(this@MainActivity, this, mService)
        bind.rcView.adapter = recyclerViewAdapter

        createScrollListener(layoutManager)
    }

    fun initRecyclerViewNew() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        bind.rvNew.layoutManager =  layoutManager
        adapterNew = AdapterNew(this@MainActivity, this, mService)
        bind.rvNew.adapter = adapterNew
    }

    fun createScrollListener(layoutManager: GridLayoutManager) {
        val scrollListener = object : com.golden13way.indigofilms.adapters.EndlessRecyclerViewScrollListener(layoutManager, ::loadMoreData) {
            override fun onLoadMore() {
                Log.d("infscroll","onLoadMore")
                loadMoreData()
            }
        }

        bind.rcView.addOnScrollListener(scrollListener)
    }

    fun loadMoreData() {
        Log.d("infscroll","loadMoreData()")
        // Получите следующую страницу данных с вашего сервера или из другого источника.
        // В этом примере мы используем номер страницы, чтобы получить следующую страницу данных.

        currentPage++

        bind.progressBar2.visibility = View.GONE
        bind.tErrorMessege2.visibility = View.GONE
        // Запросите данные с использованием метода makeApiCall и номера страницы.
        mainActivityViewModel.makeApiCall(currentPage.toString(), type, sort_field, sort_direction)

        // Увеличьте значение currentPage на 1, чтобы получить следующую страницу данных при следующем вызове loadMoreData().


        Log.d("infscroll","loadMoreData() end")
    }


    private fun initViewModel(page: String, type: String) {
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        Log.d("TAG", "2")
        mainActivityViewModel.getLiveDataObserver()
            .observe(
                this,
                object : Observer<Films?> {
                    override fun onChanged(t: Films?) {
                        if (t != null) {
                            Log.d("TAG", "3")
                            val data: MutableList<Films?> = listOfNotNull(t).toMutableList()
                            bind.progressBar2.visibility = View.GONE
                            bind.tErrorMessege2.visibility = View.GONE
                            recyclerViewAdapter.setUpdateData(data)
                            recyclerViewAdapter.notifyDataSetChanged()
                            Log.d("TAG", "4")
                        } else {
                            Log.d("TAG", "5")
                            Toast.makeText(this@MainActivity, "error in getting data", Toast.LENGTH_SHORT)
                                .show()
                            bind.progressBar2.visibility = View.GONE
                            bind.tErrorMessege2.visibility = View.VISIBLE

                        }
                    }
                })

        mainActivityViewModel.getLiveDataNewObserver()
            .observe(
                this,
                object : Observer<Films?> {
                    override fun onChanged(t: Films?) {
                        if (t != null) {
                            Log.d("TAG", "3")
                            val data: MutableList<Films?> = listOfNotNull(t).toMutableList()
                            bind.progressBar2.visibility = View.GONE
                            bind.tErrorMessege2.visibility = View.GONE
                            adapterNew.setUpdateData(data)
                            adapterNew.notifyDataSetChanged()
                            Log.d("TAG", "4")
                        } else {

                        }
                    }
                })
        mainActivityViewModel.makeApiCallNew()
        //mainActivityViewModel.makeApiCall(page, type)
        Log.d("TAG", "6")

    }





    /*fun buttonOnClickListener(){
        bind.bNext.setOnClickListener(){
            var pageInt = page.toInt()
           val data = recyclerViewAdapter.callBackData()
            Log.d("TAG1", "${data?.data?.pagination?.total_pages!!} total, ${data?.data?.pagination?.current_page!!} current ")
           if (pageInt < data?.data?.pagination?.total_pages!!) {
               pageInt ++
               page = pageInt.toString()
               mainActivityViewModel.makeApiCall(page, type)
               page()
               Log.d("TAG1", "${data?.data?.pagination?.total_pages!!} total, ${data?.data?.pagination?.current_page!!} current ")
           }
        }
        bind.bPrev.setOnClickListener(){
            var pageInt = page.toInt()
            val data = recyclerViewAdapter.callBackData()
            Log.d("TAG1", "${data?.data?.pagination?.total_pages!!} total, ${data?.data?.pagination?.current_page!!} current ")
            if (pageInt > 1) {
                pageInt --
                page = pageInt.toString()
                mainActivityViewModel.makeApiCall(page, type)
                page()
                Log.d("TAG1", "${data?.data?.pagination?.total_pages!!} total, ${data?.data?.pagination?.current_page!!} current ")
            }

        }
    }*/




    private fun clearRecyclerView() {
        // Очищаем данные адаптера
        val recyclerView = findViewById<RecyclerView>(R.id.rcView)
        val adapter = recyclerView.adapter as RecyclerViewAdapter
        adapter.clearData()
        currentPage = 1
        Log.d("clearData" ,"fun clearData must Hentai!!!")

    }

    fun closeFragments() {
        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments

        // Открытых фрагментов нет, выходим
        if (fragments.isEmpty()) return

        val fragmentTransaction = fragmentManager.beginTransaction()
        for (fragment in fragments) {
            // Удаляем фрагмент из контейнера
            fragmentTransaction.remove(fragment)
        }
        fragmentTransaction.commit()
    }


    private fun botNavMenuStarter(){

        

        val myBottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        myBottomNavigationView?.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.botNavMain -> {
                    bind.progressBar2.visibility = View.GONE
                    bind.tErrorMessege2.visibility = View.GONE

                    true

                }
                R.id.botNavFavorites -> {

                    false
                }

                R.id.botNavSerialsProfile ->{
                    false
                }
                else -> {



                    Log.d("TAG", type)
                    false
                }
            }

        }

    }

    private fun botNavMenu(){

        val myBottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        myBottomNavigationView?.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.botNavMain -> {
                    visibilityMainPage(true)
                    visibilityRecyclerView(false)
                    visibilityFilmPageFragment(false)
                    //closeFragments()
                    bind.toolbarMain.backButton.visibility  = View.INVISIBLE
                    bind.toolbarMain.filterButton.visibility  = View.INVISIBLE
                    bind.toolbarMain.sortButton.visibility = View.INVISIBLE
                    bind.toolbarMain.searchButton.visibility = View.VISIBLE
                    bind.toolbarMain.centerTitle.text = (getString(R.string.app_name))
                    bind.progressBar2.visibility = View.GONE
                    bind.tErrorMessege2.visibility = View.GONE

                    true
                }
                R.id.botNavFavorites -> {
                    visibilityMainPage(false)
                    visibilityRecyclerView(false)
                    visibilityFilmPageFragment(true)
                    bind.toolbarMain.backButton.visibility  = View.VISIBLE
                    bind.toolbarMain.filterButton.visibility  = View.INVISIBLE
                    bind.toolbarMain.sortButton.visibility = View.INVISIBLE
                    bind.toolbarMain.searchButton.visibility = View.VISIBLE
                    bind.toolbarMain.centerTitle.text = ("Избранное")
                    val fragmentTag = FavoritesFragment.FRAGMENT_TAG
                    val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                    if (fragment != null && fragment.isVisible) {
                        // Фрагмент уже существует и отображается, ничего не делаем
                        true
                    } else {
                        // Фрагмент не существует или не отображается, создаем новый экземпляр и добавляем его в контейнер
                        val registerFragment: Fragment = FavoritesFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.FilmPageFragmentConteiner, registerFragment).commit()
                        true
                    }
                }

                R.id.botNavSerialsProfile ->{
                    visibilityMainPage(false)
                    visibilityRecyclerView(false)
                    visibilityFilmPageFragment(true)
                    bind.toolbarMain.backButton.visibility  = View.INVISIBLE
                    bind.toolbarMain.filterButton.visibility  = View.INVISIBLE
                    bind.toolbarMain.sortButton.visibility = View.INVISIBLE
                    bind.toolbarMain.searchButton.visibility = View.INVISIBLE
                    bind.toolbarMain.centerTitle.text = ("Профиль")
                    val registerFragment: Fragment = ProfileFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.FilmPageFragmentConteiner, registerFragment).commit()
                    true
                }
                else -> {



                    Log.d("TAG", type)
                    false
                }
            }
        }
    }

    fun categoriesClickListeners(){
        bind.ivFilms.setOnClickListener(){
            type = "film"
            page = "1"
            clearRecyclerView()
            initRecyclerView()
            bind.progressBar2.visibility = View.GONE
            bind.tErrorMessege2.visibility = View.GONE
            bind.toolbarMain.backButton.visibility  = View.VISIBLE
            bind.toolbarMain.filterButton.visibility  = View.VISIBLE
            bind.toolbarMain.sortButton.visibility = View.VISIBLE
            bind.toolbarMain.searchButton.visibility = View.VISIBLE
            shiki = false
            bind.toolbarMain.centerTitle.text = "Фильмы"
            mainActivityViewModel.makeApiCall(page, type, sort_field, sort_direction)
            visibilityMainPage(false)
            visibilityRecyclerView(true)
        }
        bind.ivSerials.setOnClickListener(){
            type = "serial"
            page = "1"
            clearRecyclerView()
            initRecyclerView()
            bind.progressBar2.visibility = View.GONE
            bind.tErrorMessege2.visibility = View.GONE
            bind.toolbarMain.backButton.visibility  = View.VISIBLE
            bind.toolbarMain.filterButton.visibility  = View.VISIBLE
            bind.toolbarMain.sortButton.visibility = View.VISIBLE
            bind.toolbarMain.searchButton.visibility = View.VISIBLE
            shiki = false
            bind.toolbarMain.centerTitle.text = "Сериалы"
            mainActivityViewModel.makeApiCall(page, type, sort_field, sort_direction)
            visibilityMainPage(false)
            visibilityRecyclerView(true)
        }
        bind.ivAnime.setOnClickListener(){
            type = "anime"
            page = "1"
            bind.progressBar2.visibility = View.GONE
            bind.tErrorMessege2.visibility = View.GONE
            clearRecyclerView()
            initRecyclerView()
            bind.toolbarMain.backButton.visibility  = View.VISIBLE
            bind.toolbarMain.filterButton.visibility  = View.VISIBLE
            bind.toolbarMain.sortButton.visibility = View.VISIBLE
            bind.toolbarMain.searchButton.visibility = View.VISIBLE
            shiki = true
            bind.toolbarMain.centerTitle.text = "Аниме"
            Log.d("genres", "click ${page}, ${type}")
            mainActivityViewModel.makeApiCall(page, type, sort_field, sort_direction)
            visibilityMainPage(false)
            visibilityRecyclerView(true)

        }
        bind.ivCartoons.setOnClickListener(){
            type = "cartoon"
            page = "1"
            clearRecyclerView()
            initRecyclerView()
            bind.progressBar2.visibility = View.GONE
            bind.tErrorMessege2.visibility = View.GONE
            mainActivityViewModel.makeApiCall(page, type, sort_field, sort_direction)
            visibilityMainPage(false)
            visibilityRecyclerView(true)
            bind.toolbarMain.backButton.visibility  = View.VISIBLE
            bind.toolbarMain.filterButton.visibility  = View.VISIBLE
            bind.toolbarMain.sortButton.visibility = View.VISIBLE
            bind.toolbarMain.searchButton.visibility = View.VISIBLE
            shiki = false
            bind.toolbarMain.centerTitle.text = "Мультики"
        }
        bind.bBack.setOnClickListener(){
            visibilityRecyclerView(false)
            visibilityFilmPageFragment(false)
            visibilityMainPage(true)
            bind.progressBar2.visibility = View.GONE
            bind.tErrorMessege2.visibility = View.GONE

        }
        bind.toolbarMain.searchButton.setOnClickListener(){
            visibilityMainPage(false)
            visibilityRecyclerView(false)
            visibilityFilmPageFragment(true)
            bind.toolbarMain.backButton.visibility  = View.INVISIBLE
            bind.toolbarMain.filterButton.visibility  = View.INVISIBLE
            bind.toolbarMain.sortButton.visibility = View.INVISIBLE
            bind.toolbarMain.searchButton.visibility = View.INVISIBLE
            bind.toolbarMain.centerTitle.text = ("Поиск")
            val registerFragment: Fragment = SearchFragment()
            supportFragmentManager.beginTransaction().replace(R.id.FilmPageFragmentConteiner, registerFragment).commit()
        }

        bind.toolbarMain.sortButton.setOnClickListener(){
            showPopupMenu(it)
        }
    }
    fun visibilityMainPage(state: Boolean){
        var visibility = View.GONE
        if (state == true) visibility = View.VISIBLE
        if (state == false) visibility = View.GONE
        bind.ivAnime.visibility = visibility
        bind.ivFilms.visibility = visibility
        bind.ivCartoons.visibility = visibility
        bind.ivSerials.visibility = visibility
        bind.tvAnimes.visibility = visibility
        bind.tvFilms.visibility = visibility
        bind.tvSerials.visibility = visibility
        bind.tvCartoons.visibility = visibility
        bind.rvNew.visibility = visibility
        bind.tvNew.visibility = visibility
        bind.rvNew.scrollToPosition(0)
        if(state == false) {
            adapterNew.clearData()
            mainActivityViewModel.makeApiCallNew()
        }

    }



    override fun visibilityBackAndMenu(state: Boolean){

        var visibility = View.GONE
        if (state == true) visibility = View.VISIBLE
        if (state == false) visibility = View.GONE
        bind.bBack.visibility = visibility
        bind.bottomNavigationView.visibility = visibility
    }

    fun visibilityRecyclerView(state: Boolean){
        var visibility = View.GONE
        if (state == true) visibility = View.VISIBLE
        if (state == false) visibility = View.GONE
        bind.rcView.visibility = visibility
        bind.bBack.visibility = visibility
    }
    fun visibilityFilmPageFragment(state: Boolean){
        var visibility = View.GONE
        if (state == true) visibility = View.VISIBLE
        if (state == false) visibility = View.GONE
        bind.FilmPageFragmentConteiner.visibility = visibility
        bind.bBack.visibility = visibility
    }


    fun gc(){

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_sort_menu, popupMenu.menu)

        val shikiUpMenuUP = popupMenu.menu.findItem(R.id.shikiUP)
        val shikiUpMenuDOWN = popupMenu.menu.findItem(R.id.shikiDOWN)
        shikiUpMenuUP.isVisible = shiki
        shikiUpMenuDOWN.isVisible = shiki

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.yearDOWN -> {
                    sort_field = "release_date"
                    sort_direction = "desc"

                    currentPage = 0
                    val adapter = bind.rcView.adapter as RecyclerViewAdapter
                    adapter.clearData()
                    loadMoreData()
                    true
                }
                R.id.yearUP -> {
                    sort_field = "release_date"
                    sort_direction = "asc"
                    currentPage = 0
                    val adapter = bind.rcView.adapter as RecyclerViewAdapter
                    adapter.clearData()
                    loadMoreData()
                    true
                }
                R.id.imdpDOWN -> {
                    sort_field = "imdb_rating"
                    sort_direction = "desc"
                    currentPage = 0
                    val adapter = bind.rcView.adapter as RecyclerViewAdapter
                    adapter.clearData()
                    loadMoreData()
                    true
                }
                R.id.imdbUP -> {
                    sort_field = "imdb_rating"
                    sort_direction = "asc"
                    currentPage = 0
                    val adapter = bind.rcView.adapter as RecyclerViewAdapter
                    adapter.clearData()
                    loadMoreData()
                    true
                }
                R.id.shikiDOWN -> {
                    sort_field = "shiki_rating"
                    sort_direction = "desc"
                    currentPage = 0
                    val adapter = bind.rcView.adapter as RecyclerViewAdapter
                    adapter.clearData()
                    loadMoreData()
                    true
                }
                R.id.shikiUP -> {
                    sort_field = "shiki_rating"
                    sort_direction = "asc"
                    currentPage = 0
                    val adapter = bind.rcView.adapter as RecyclerViewAdapter
                    adapter.clearData()
                    loadMoreData()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }



}

