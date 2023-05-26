package com.golden13way.indigofilms.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.golden13way.indigofilms.API.Items
import com.golden13way.indigofilms.CellClickListener
import com.golden13way.indigofilms.MainActivity
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.adapters.RecyclerViewFavoritesAdapter
import com.golden13way.indigofilms.classes.DoubleOrbitView
import com.golden13way.indigofilms.databinding.FragmentFavoritesBinding
import com.golden13way.indigofilms.dataclass.Favorites
import com.golden13way.indigofilms.viewModels.MainActivityViewModel



class FavoritesFragment : Fragment(), CellClickListener {

    companion object {
        const val FRAGMENT_TAG = "favorites_fragment"
    }

    lateinit var bind: FragmentFavoritesBinding
    var page = "1"
    var type = "film"
    lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var recyclerViewAdapter: RecyclerViewFavoritesAdapter
    private var currentPage = 1
    var fragmentId : Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentFavoritesBinding.inflate(inflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //doubleOrbitView = view.findViewById(R.id.double_orbit_view)
        //doubleOrbitView.startAnimation()
        // Получите экземпляр ViewModel
        bind.progressBar.visibility = View.VISIBLE
        initViewModel()
        initRecyclerView()
    }






    fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3)
        bind.rvFav.layoutManager =  layoutManager

        recyclerViewAdapter = RecyclerViewFavoritesAdapter(this@FavoritesFragment, requireContext(), requireActivity())
        bind.rvFav.adapter = recyclerViewAdapter
        Log.d("infscroll","initRecyclerView")

        createScrollListener(layoutManager)
    }

    fun createScrollListener(layoutManager: GridLayoutManager) {
        Log.d("infscroll","createScrollListener")
        val scrollListener = object : com.golden13way.indigofilms.adapters.EndlessRecyclerViewScrollListener(layoutManager, ::loadMoreData) {
            override fun onLoadMore() {
                Log.d("infscroll","onLoadMore")
                loadMoreData()
            }
        }

        bind.rvFav.addOnScrollListener(scrollListener)
    }

    fun loadMoreData() {
        Log.d("infscroll","loadMoreData()")
        // Получите следующую страницу данных с вашего сервера или из другого источника.
        // В этом примере мы используем номер страницы, чтобы получить следующую страницу данных.

        currentPage++

        // Запросите данные с использованием метода makeApiCall и номера страницы.
        mainActivityViewModel.makeApiCallFavorites(currentPage.toString())

        // Увеличьте значение currentPage на 1, чтобы получить следующую страницу данных при следующем вызове loadMoreData().


        Log.d("infscroll","loadMoreData() end")
    }


    private fun initViewModel() {
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        Log.d("TAG", "2")
        mainActivityViewModel.getLiveDataFavoritesObserver()
            .observe(
                viewLifecycleOwner,
                object : Observer<Favorites?> {
                    override fun onChanged(t: Favorites?) {
                        if (t != null) {
                            Log.d("TAG", "3")
                            val data: MutableList<Favorites?> = listOfNotNull(t).toMutableList()
                            recyclerViewAdapter.setUpdateData(data)
                            recyclerViewAdapter.notifyDataSetChanged()
                            bind.progressBar.visibility = View.GONE
                            Log.d("TAG", "4")
                        } else {
                            Log.d("TAG", "5")
                            Toast.makeText(context, "error in getting data", Toast.LENGTH_SHORT)
                                .show()

                            bind.tErrorMessege.visibility = View.VISIBLE
                            bind.progressBar.visibility = View.GONE

                        }

                        //doubleOrbitView.stopAnimation()
                        //bind.doubleOrbitView.visibility = View.GONE
                        bind.progressBar.visibility = View.GONE

                    }
                })
        mainActivityViewModel.makeApiCallFavorites(currentPage.toString())
        Log.d("TAG", "6")
    }

    override fun idToMain(pos: Int, data: List<Items>){


        if (data != null) {
            fragmentId = data[pos].id.toInt()
            Log.d("TAG1", "${id} id on Main")
            (activity as MainActivity).startFragmentFilmPage(fragmentId)
            //startFragmentFilmPage(fragmentId)
        }
    }


    fun startFragmentFilmPage(id: Int){
        val fragment = FilmPageFragment.newInstance(fragmentId.toString())
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun visibilityBackAndMenu(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun decorView() {
        TODO("Not yet implemented")
    }

    override fun idToMainFav(pos: Int, data: List<Items>) {
        TODO("Not yet implemented")
    }


}