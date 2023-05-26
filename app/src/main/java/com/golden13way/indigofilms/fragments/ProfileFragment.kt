package com.golden13way.indigofilms.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.golden13way.indigofilms.API.AccessTokenProviderImpl
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.FragmentProfileBinding
import com.golden13way.indigofilms.dataclass.Auth
import com.golden13way.indigofilms.dataclass.AuthData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var bind: FragmentProfileBinding
    lateinit var userData: Auth
    private var sharedPreferences : SharedPreferences? = null
    @Inject
    lateinit var mService: RetroFitServiceInterface
    var face = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity()
            .getSharedPreferences("Settings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentProfileBinding.inflate(inflater)
        (DaggerRetroFitComponent.create()).inject(this)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        face = sharedPreferences?.getString ("face", "").toString()
        Log.d("Profile", "face - ${face}")
        if(face != "") setContent()
        if (face == "") {
            val fragment = LoginFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        setOnClickListeners()

    }




    fun ClearText(){
        bind.tEmail.text = ""
        bind.tdate.text = ""
        bind.tName.text = ""
        bind.tAbout.text = ""
        bind.tNickName.text = ""
        bind.imageView2.visibility = View.INVISIBLE
    }

fun setOnClickListeners(){
    bind.bLogout.setOnClickListener {
        logout()
    }
    bind.bEdit.setOnClickListener {
        editProfile()
    }
}
    fun editProfile(){
        val fragment = EditProfileFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


fun logout(){
    val editor = sharedPreferences?.edit()
    editor?.putString("face", "")
    editor?.apply()
    face = ""
    AccessTokenProviderImpl.setAccessToken(face)
    val list = listOf<Int>()
    userData = Auth(false, AuthData("","","",0,"","","", poster_large = "", user_name = "", list))
    UserData.setUserData(userData)

    ClearText()
    val fragment = LoginFragment()
    val fragmentManager = requireActivity().supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
    transaction.addToBackStack(null)
    transaction.commit()

    val call: Call<Auth>? = mService.getAuth()
    call?.enqueue(object : Callback<Auth> {
        override fun onFailure(call: Call<Auth>, t: Throwable) {

        }

        override fun onResponse(
            call: Call<Auth>,
            response: Response<Auth>
        ) {


            if (response.isSuccessful) {

            } else {
                Log.d("TAG", "onResponse else")

            }
        }
    })
}

    fun setContent() {
        val list = listOf<Int>()
        userData = Auth(false, AuthData("","","",0,"","","", poster_large = "", user_name = "", list))


        try {
            userData = UserData.getUserData()!!
        } catch (e: NullPointerException) {
            // обрабатываем исключение, если метод "getUserData()" возвращает null

        }

        if (userData != null) {
            if (userData.state == true) {

                if (userData.data.name != null && userData.data.name != "") bind.tName.text =
                    userData.data.name
                else bind.tName.visibility = View.INVISIBLE
                if (userData.data.user_name != null && userData.data.user_name != "") bind.tNickName.text =
                    userData.data.user_name
                else bind.tNickName.visibility = View.INVISIBLE
                if (userData.data.email != null && userData.data.email != "") bind.tEmail.text =
                    userData.data.email
                else bind.tEmail.visibility = View.INVISIBLE
                if (userData.data.birth_date != null && userData.data.birth_date != "") bind.tdate.text =
                    userData.data.birth_date
                else bind.tdate.visibility = View.INVISIBLE
                if (userData.data.about != null && userData.data.about != "") bind.tAbout.text =
                    userData.data.about
                else bind.tAbout.visibility = View.INVISIBLE
                if (userData.data.poster_large != null && userData.data.poster_large != "") {
                    Glide.with(bind.imageView2)
                        .load(userData.data.poster_large)
                        .placeholder(R.drawable.loaderpic)
                        .error(R.drawable.error404)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(bind.imageView2)
                }
                if (userData.data.poster_large == null && userData.data.poster_large == "") {
                    Glide.with(bind.imageView2)
                        .load(R.drawable.avatar)
                        .placeholder(R.drawable.loaderpic)
                        .error(R.drawable.error404)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(bind.imageView2)
                }
            }
            else {logout()}
        }
    }
}