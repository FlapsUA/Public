package com.golden13way.indigofilms.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.golden13way.indigofilms.API.Films
import com.golden13way.indigofilms.API.Items
import com.golden13way.indigofilms.CellClickListener
import com.golden13way.indigofilms.MainActivity
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.adapters.AdapterSearch
import com.golden13way.indigofilms.adapters.RecyclerViewFavoritesAdapter
import com.golden13way.indigofilms.classes.DoubleOrbitView
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.ActivityMainBinding
import com.golden13way.indigofilms.databinding.FragmentSearchBinding
import com.golden13way.indigofilms.dataclass.Favorites
import com.golden13way.indigofilms.viewModels.MainActivityViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), CellClickListener {
    lateinit var bind: FragmentSearchBinding
    lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var recyclerViewAdapter: AdapterSearch
    private var currentPage = 1
    var fragmentId : Int = 0
    var find = ""
    @Inject
    lateinit var mService: RetroFitServiceInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentSearchBinding.inflate(inflater)
        (DaggerRetroFitComponent.create()).inject(this)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRecyclerView()
        clickListeners()
    }


   fun clickListeners(){
       bind.etSearch.addTextChangedListener(object : TextWatcher {
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
               // Не используется
           }

           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               Log.d("infscroll","onTextChanged")
               // Вызывается при изменении текста в EditText

               if (!bind.etSearch.text.isNullOrBlank() || !bind.etSearch.text.isEmpty()) {
                   find = s.toString()
                   if (find.length > 1) {
                       Log.d("infscroll", "больше 2")
                       currentPage = 0
                       val adapter = bind.rvSearch.adapter as AdapterSearch
                       adapter.clearData()
                       loadMoreData()
                       bind.tHelperSearch.visibility = View.GONE
                       bind.rvSearch.visibility = View.VISIBLE
                   }
               }
                   if (find == "" || find.isBlank() || find.isEmpty() || bind.etSearch.text.isNullOrBlank() || bind.etSearch.text.isEmpty()) {
                       Log.d("infscroll","меньше 2")
                       currentPage = 0
                       val adapter = bind.rvSearch.adapter as AdapterSearch
                       adapter.clearData()
                       bind.tHelperSearch.visibility = View.VISIBLE
                       bind.rvSearch.visibility = View.INVISIBLE
                   }

           }

           override fun afterTextChanged(s: Editable?) {

               // Выполните здесь необходимые действия при изменении текста в EditText, например, отправку запроса

           }
       })
   }


    fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        bind.rvSearch.layoutManager =  layoutManager
        recyclerViewAdapter = AdapterSearch(this@SearchFragment, requireContext(), mService)
        bind.rvSearch.adapter = recyclerViewAdapter
        Log.d("infscroll","initRecyclerView")

        createScrollListener(layoutManager)
    }

    fun createScrollListener(layoutManager: LinearLayoutManager) {
        Log.d("infscroll","createScrollListener")
        val scrollListener = object : com.golden13way.indigofilms.adapters.EndlessRecyclerViewScrollListener(layoutManager, ::loadMoreData) {
            override fun onLoadMore() {
                Log.d("infscroll","onLoadMore")
                loadMoreData()
            }
        }

        bind.rvSearch.addOnScrollListener(scrollListener)
    }


    fun loadMoreData() {
        Log.d("infscroll","loadMoreData()")
        // Получите следующую страницу данных с вашего сервера или из другого источника.
        // В этом примере мы используем номер страницы, чтобы получить следующую страницу данных.



        // Запросите данные с использованием метода makeApiCall и номера страницы.
        if (find.length > 1) {
            currentPage++
            mainActivityViewModel.makeApiCallSearch(currentPage.toString(), find)
            bind.tHelperSearch.visibility = View.GONE
        }
        if (find == "" || find.isBlank() || find.isEmpty() || bind.etSearch.text.isNullOrBlank() || bind.etSearch.text.isEmpty()) {
            currentPage = 0
            val adapter = bind.rvSearch.adapter as AdapterSearch
            adapter.clearData()
            bind.tHelperSearch.visibility = View.VISIBLE
        }
        // Увеличьте значение currentPage на 1, чтобы получить следующую страницу данных при следующем вызове loadMoreData().


        Log.d("infscroll","loadMoreData() end")
    }




    private fun initViewModel() {
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        Log.d("TAG", "2")
        mainActivityViewModel.getLiveDataSearchObserver()
            .observe(
                viewLifecycleOwner,
                object : Observer<Films?> {
                    override fun onChanged(t: Films?) {
                        if (t != null) {
                            Log.d("TAG", "3")
                            val data: MutableList<Films?> = listOfNotNull(t).toMutableList()
                            recyclerViewAdapter.setUpdateData(data)
                            recyclerViewAdapter.notifyDataSetChanged()

                            Log.d("TAG", "4")
                        } else {
                            Log.d("TAG", "5")
                            Toast.makeText(context, "error in getting data", Toast.LENGTH_SHORT)
                                .show()

                        }

                        //doubleOrbitView.stopAnimation()
                        //bind.doubleOrbitView.visibility = View.GONE


                    }
                })

        Log.d("TAG", "6")
    }

    override fun idToMain(pos: Int, data: List<Items>){


        if (data != null) {
            fragmentId = data[pos].id.toInt()
            (activity as MainActivity).startFragmentFilmPage(fragmentId)
            Log.d("TAG1", "${id} id on Main")
                //startFragmentFilmPage(fragmentId)
        }
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

    fun startFragmentFilmPage(id: Int){
        val fragment = FilmPageFragment.newInstance(fragmentId.toString())
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {


        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}