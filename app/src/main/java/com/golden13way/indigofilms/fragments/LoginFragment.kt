package com.golden13way.indigofilms.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.golden13way.indigofilms.API.AccessTokenProviderImpl
import com.golden13way.indigofilms.API.Authorization
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.FragmentLoginBinding
import com.golden13way.indigofilms.dataclass.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var bind: FragmentLoginBinding
    @Inject
    lateinit var mService: RetroFitServiceInterface
    private lateinit var webView: WebView
    private lateinit var sharedPreferences: SharedPreferences
    val authorization = Authorization()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        sharedPreferences = requireActivity()
            .getSharedPreferences("Settings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentLoginBinding.inflate(inflater)
        (DaggerRetroFitComponent.create()).inject(this)
        // Inflate the layout for this fragment
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //faceStatus = sharedPreferences.getString(faceStatus, "NULL")!!
        webView = bind.webview
        onClickListeners()
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


    fun onClickListeners(){
        bind.bLogin.setOnClickListener{
            login()
        }
        bind.bRegister.setOnClickListener {
            register()
        }
        telegramAuth()
    }

    fun changeviseble(boolean: Boolean){
        if(boolean){
            bind.webview.visibility = View.VISIBLE
            bind.bRegister.visibility = View.INVISIBLE
            bind.etLoginEmail.visibility = View.INVISIBLE
            bind.bLogin.visibility = View.INVISIBLE
            bind.etLoginPassword.visibility = View.INVISIBLE
            bind.buttonTelegram.visibility = View.INVISIBLE

        }
        if(!boolean){
            bind.webview.visibility = View.INVISIBLE
            bind.bRegister.visibility = View.VISIBLE
            bind.buttonTelegram.visibility = View.VISIBLE
            bind.etLoginEmail.visibility = View.VISIBLE
            bind.bLogin.visibility = View.VISIBLE
            bind.etLoginPassword.visibility = View.VISIBLE
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


    fun register(){

        val fragment = RegisterFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.FilmPageFragmentConteiner, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun login() {
        var checkEmail = false
        var checkPassword = false
        var email = ""
        var password = ""
        if (bind.etLoginEmail.text.isNotBlank()) {
            val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            email = bind.etLoginEmail.text.toString()
            if (emailRegex.matches(bind.etLoginEmail.text.toString())){
                checkEmail = true
            }
            else {
                bind.etLoginEmail.error = "Введённый Email не похож на настоящий"
            }
        }

        if (bind.etLoginPassword.text.isNotBlank()) {
            password = bind.etLoginPassword.text.toString()
            if (password.length >= 6 && password.length <= 15) {
                checkPassword = true
            } else {
                bind.etLoginPassword.error = "Пароль должен содержать не менее 8 символов и не более 15"
            }
        }

        if (checkEmail && checkPassword) {
            val data = Login(email, password)
            val call: Call<ResponseRegisterLogin>? = mService.postLogin(data)
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
                            Toast.makeText(context, "Логин пройден ${response.body()!!.data.access_token}", Toast.LENGTH_SHORT).show()
                            var data = ""
                            data = sharedPreferences.getString("face", "").toString()
                            Log.d("Profile", "data - ${data}")
                            if(data != ""){
                                giveToken()
                                tryAuth()

                            }
                        }
                    }
                    if (response.body()?.state == false || response.body()?.state == null){
                        Toast.makeText(context, "Логин не пройдена", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Log.d("TAG", "onResponse else")


                    }
                }

            })

        }

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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters


    }
}