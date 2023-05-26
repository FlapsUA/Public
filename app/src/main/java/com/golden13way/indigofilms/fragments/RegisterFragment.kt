package com.golden13way.indigofilms.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import com.golden13way.indigofilms.API.AccessTokenProviderImpl
import com.golden13way.indigofilms.API.Authorization
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.FragmentRegisterBinding
import org.json.JSONObject
import android.util.Base64
import com.golden13way.indigofilms.dataclass.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var bind: FragmentRegisterBinding
    @Inject
    lateinit var mService: RetroFitServiceInterface
    private lateinit var webView: WebView
    private lateinit var sharedPreferences: SharedPreferences
    val authorization = Authorization()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
        sharedPreferences = requireActivity()
            .getSharedPreferences("Settings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentRegisterBinding.inflate(inflater)
        (DaggerRetroFitComponent.create()).inject(this)
        // Inflate the layout for this fragment

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //faceStatus = sharedPreferences.getString(faceStatus, "NULL")!!
        webView = bind.webview
        telegramAuth()
        bind.bRegister.setOnClickListener{
            changeviseble(false)
            registration()
        }
    }
        fun changeviseble(boolean: Boolean){
            if(boolean){
                bind.webview.visibility = View.VISIBLE
                bind.bRegister.visibility = View.INVISIBLE
                bind.buttonTelegram.visibility = View.INVISIBLE
                bind.etRegisterEmail.visibility = View.INVISIBLE
                bind.etRegisterName.visibility = View.INVISIBLE
                bind.etRegisterConfirmPassword.visibility = View.INVISIBLE
                bind.etRegisterPassword.visibility = View.INVISIBLE
            }
            if(!boolean){
                bind.webview.visibility = View.INVISIBLE
                bind.bRegister.visibility = View.VISIBLE
                bind.buttonTelegram.visibility = View.VISIBLE
                bind.etRegisterEmail.visibility = View.VISIBLE
                bind.etRegisterName.visibility = View.VISIBLE
                bind.etRegisterConfirmPassword.visibility = View.VISIBLE
                bind.etRegisterPassword.visibility = View.VISIBLE
            }
        }
    fun telegramAuth() {
        bind.buttonTelegram.setOnClickListener {
            changeviseble(true)
            webView.settings.javaScriptEnabled = true
            val referer = "https://indigofilms.online"
            val botName = "IndigoFilmsBot"
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val url = request.url.toString()
                    if (url.startsWith("https://indigofilms.online/#tgAuthResult=")) {
                        val userDataHash = url.substringAfter("#tgAuthResult=")
                        val decodedUserData = String(Base64.decode(userDataHash, Base64.DEFAULT))
                        WebAppInterface(requireContext()).receiveUserData(decodedUserData)
                        return true
                    }
                    return false
                }
            }
            val htmlContent = """

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
</head>
<body>
<style>
body {
display: flex;
justify-content: center;
align-items: center;
height: 100vh;
}
.telegram-button {
display: block;
text-align: center;
}
</style>
<script async src="https://telegram.org/js/telegram-widget.js?22" data-telegram-login="$botName" data-size="large" data-userpic="true" data-onauth="onTelegramAuth" data-request-access="write"></script>
<script>
function onTelegramAuth(user) {
    AndroidFunction.receiveUserData(JSON.stringify(user));
}
</script>
</body>
</html>
""".trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.addJavascriptInterface(WebAppInterface(requireContext()), "AndroidFunction")
            webView.loadDataWithBaseURL(referer, htmlContent, "text/html", "utf-8", null)
        }
    }

    inner class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun receiveUserData(jsonData: String) {
            try {
                val user = JSONObject(jsonData)
                val id = user.getInt("id")
                val firstName = user.getString("first_name")
                val hash = user.optString("hash")
                val username = user.optString("username")
                val img = user.optString("photo_url")
                val authDate = user.getInt("auth_date")
                val userInfo = "Logged in as $username $firstName  $hash $img )"
                (context as Activity).runOnUiThread {
                    Log.d("TelegramAuth", "$userInfo")
                    Log.d("TelegramAuth", "${user.toString()}")
                    Toast.makeText(context, userInfo, Toast.LENGTH_LONG).show()
                    changeviseble(false)

                    val call: Call<ResponseRegisterLogin>? = mService.postAuthTelegram(
                        TelegramAuth(TG(authDate, firstName, hash, id, img, username))
                    )
                    call?.enqueue(object : Callback<ResponseRegisterLogin> {
                        override fun onFailure(call: Call<ResponseRegisterLogin>, t: Throwable) {
                            Log.e("RegisterFragment", "Ошибка запроса: ${t.message}")
                            t.printStackTrace()
                            Toast.makeText(context, "Ответ от сервера не получен", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<ResponseRegisterLogin>,
                            response: Response<ResponseRegisterLogin>
                        ) {
                            Log.d("TAG", "onResponse")

                            if (response.isSuccessful) {
                                Log.d("TAG", "onResponse isSuccessful")
                                if (response.body()?.state == true){
                                    val face = response.body()!!.data.access_token
                                    val editor = sharedPreferences.edit()
                                    editor.putString("face", face)
                                    editor.apply()
                                    Toast.makeText(context, "Регистрация пройдена ${response.body()!!.data.access_token}", Toast.LENGTH_SHORT).show()
                                    giveToken()
                                    tryAuth()
                                }
                            }
                            if (response.body()?.state == false || response.body()?.state == null){
                                Toast.makeText(context, "Регистрация не пройдена", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Log.d("TAG", "onResponse else")
                            }
                        }
                    })
                }
            } catch (e: Exception) {
                Log.d("TelegramAuth", "Error processing user data", e)
            }
        }
    }


    private fun registration() {
       var checkEmail = false
       var checkName = false
       var checkPassword = false
       var checkConfirmPassword = false
       var email = ""
       var name = ""
       var password = ""
       var confirmPassword = ""
       if (bind.etRegisterEmail.text.isNotBlank()) {
           val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
           email = bind.etRegisterEmail.text.toString()
           if (emailRegex.matches(bind.etRegisterEmail.text.toString())){
               checkEmail = true
           }
           else {
               bind.etRegisterEmail.error = "Введённый Email не похож на настоящий"
           }
       }
       if (bind.etRegisterName.text.isNotBlank()) {
           val nameRegex = Regex("^[a-zA-Zа-яА-Я]{2,14}$")
           if (nameRegex.matches(bind.etRegisterName.text.toString())) {
               name = bind.etRegisterName.text.toString()
               checkName = true
           } else {
               bind.etRegisterName.error = "Имя должно содержать только русские или английские буквы и быть длиной от 2 до 14 символов"
           }
       }
       if (bind.etRegisterPassword.text.isNotBlank()) {
           password = bind.etRegisterPassword.text.toString()
           if (password.length >= 6 && password.length <= 15) {
               checkPassword = true
           } else {
               bind.etRegisterPassword.error = "Пароль должен содержать не менее 8 символов и не более 15"
           }
       }
       if (bind.etRegisterConfirmPassword.text.isNotBlank()) {
           confirmPassword = bind.etRegisterConfirmPassword.text.toString()
           if (confirmPassword == password) {
               checkConfirmPassword = true
           } else {
               bind.etRegisterConfirmPassword.error = "Введённые пароли не совпадают"
           }
       }
       if (checkEmail && checkName && checkPassword && checkConfirmPassword) {
           val data = Register(email, name, password, confirmPassword)

           val call: Call<ResponseRegisterLogin>? = mService.postRegister(data)
           call?.enqueue(object : Callback<ResponseRegisterLogin> {
               override fun onFailure(call: Call<ResponseRegisterLogin>, t: Throwable) {
                   Log.e("RegisterFragment", "Ошибка запроса: ${t.message}")
                   t.printStackTrace()
                   Toast.makeText(context, "Ответ от сервера не получен", Toast.LENGTH_SHORT).show()
               }

               override fun onResponse(
                   call: Call<ResponseRegisterLogin>,
                   response: Response<ResponseRegisterLogin>
               ) {
                   Log.d("TAG", "onResponse")

                   if (response.isSuccessful) {
                       Log.d("TAG", "onResponse isSuccessful")
                       if (response.body()?.state == true){
                           val face = response.body()!!.data.access_token
                           val editor = sharedPreferences.edit()
                           editor.putString("face", face)
                           editor.apply()
                           Toast.makeText(context, "Регистрация пройдена ${response.body()!!.data.access_token}", Toast.LENGTH_SHORT).show()
                           giveToken()
                           tryAuth()
                       }
                   }
                       if (response.body()?.state == false || response.body()?.state == null){
                           Toast.makeText(context, "Регистрация не пройдена", Toast.LENGTH_SHORT).show()
                   }
                   else {
                       Log.d("TAG", "onResponse else")
                   }
               }
           })
       }
   }

    private fun tryAuth(){
        Log.d("Auth", "Старт tryAuth")
        val face = sharedPreferences?.getString("face", "")
        Log.d("Auth", "blindface ${face}")
        if(face != null && face != "" && face != "NULL"){
            Log.d("Auth", "no fail blindface")
            authorization.authorization() { auth ->
                if (auth != null) {
                    // если ответ не null, то здесь обрабатываем данные токена авторизации (Auth)
                    if(auth.state){
                        Toast.makeText(context, "authorization пройдена ${auth.data.name}", Toast.LENGTH_SHORT).show()
                        Log.d("Auth", "Успех запроса авторизации")
                        UserData.setUserData(auth)
                        val fragment = ProfileFragment()
                        val fragmentManager = requireActivity().supportFragmentManager
                        val transaction = fragmentManager.beginTransaction()
                        transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                        Log.d("Profile", "переход на профиль")
                    }
                    else{}
                }
                else {
                    // если ответ null, то здесь обрабатываем ошибку
                    Toast.makeText(context, "authorization fail}", Toast.LENGTH_SHORT).show()
                    Log.d("Auth", "Ошибка запроса авторизации")
                }
            }

        }
        else { Log.d("Auth", "Токена нет не залогинены") }
    }



    fun giveToken(){

        val face = sharedPreferences?.getString("face", "").toString()
        Log.d("Auth", "GiveToken ${face}")
        AccessTokenProviderImpl.setAccessToken(face)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        /*@JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    *//*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*//*
                }
            }*/
    }
}