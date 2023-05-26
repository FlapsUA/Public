package com.golden13way.indigofilms.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.golden13way.indigofilms.API.Authorization
import com.golden13way.indigofilms.API.UserData
import com.golden13way.indigofilms.R
import com.golden13way.indigofilms.daggerPack.DaggerRetroFitComponent
import com.golden13way.indigofilms.daggerPack.RetroFitServiceInterface
import com.golden13way.indigofilms.databinding.FragmentEditProfileBinding
import com.golden13way.indigofilms.dataclass.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var bind : FragmentEditProfileBinding
    lateinit var userData: Auth
    private var sharedPreferences : SharedPreferences? = null
    val authorization = Authorization()
    @Inject
    lateinit var mService: RetroFitServiceInterface
    var face = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (DaggerRetroFitComponent.create()).inject(this)
        bind = FragmentEditProfileBinding.inflate(inflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        face = sharedPreferences?.getString ("face", "").toString()
        Log.d("Profile", "face - ${face}")
        if(face != "") setContent()
        else{

        }
        setOnClickListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            Log.d("Profile", "ava - ${data.data}")
            // Запуск обрезки изображения с помощью библиотеки Image Cropper
            CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .setRequestedSize(300, 300)
                .setBackgroundColor(R.color.purpleIF)
                .setBorderLineColor(R.color.blueIF)
                .setGuidelinesColor(R.color.blueIF)
                .setBorderCornerColor(R.color.purpleIF)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(requireContext(), this)
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val croppedUri = result.uri
            bind.imageView2.setImageURI(croppedUri)
            CoroutineScope(Dispatchers.Main).launch {
                uploadPicture(croppedUri)
                // Обрабатываем ответ от сервера
                tryAuth()
                bind.imageView2.setImageURI(croppedUri)
            }

        }
    }

    suspend fun uploadPicture(imageUri: Uri): Auth {
        val contentResolver = context?.contentResolver
        val inputStream = contentResolver?.openInputStream(imageUri)
        val file = File(context?.cacheDir, "temp_image")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)
        return withContext(Dispatchers.IO) {
            mService.uploadPicture(body).execute().body()!!
        }
    }

    private fun croper(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        Log.d("Profile", "ava - ${MediaStore.Images.Media.EXTERNAL_CONTENT_URI}")
    }


    private fun tryAuth(){
        Log.d("Auth", "Старт tryAuth")
        face = sharedPreferences?.getString("face", "").toString()
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
                        setContent()
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

    fun changeProfile(){
        var checkName = false
        var checkNickName = false
        var checkAbout = false
        var name = ""
        var nickName = ""
        var about = ""


        if (bind.etName.text.isNotBlank()) {
            val nameRegex = Regex("^[a-zA-Zа-яА-Я]{2,14}$")
            if (nameRegex.matches(bind.etName.text.toString())) {
                name = bind.etName.text.toString()
                checkName = true
            } else {
                bind.etName.error = "Имя должно содержать только русские или английские буквы и быть длиной от 2 до 14 символов"
            }
        }
        if (bind.etNickName.text.isNotBlank()) {
            val nameRegex = Regex("^[a-zA-Z]{2,14}$")
            if (nameRegex.matches(bind.etNickName.text.toString())) {
                nickName = bind.etNickName.text.toString()
                checkNickName = true
            } else {
                bind.etNickName.error = "Ник должно содержать только английские буквы и быть длиной от 2 до 14 символов"
            }
        }

        if (bind.etAbout.text.isNotBlank()) {
            val nameRegex = Regex("^.{0,400}\$")
            if (nameRegex.matches(bind.etAbout.text.toString())) {
                about = bind.etAbout.text.toString()
                checkAbout = true
            } else {
                bind.etAbout.error = "О себе должно быть длиной от 1 до 400 символов"
            }
        }


        if (checkName && checkNickName && checkAbout) {
            val data = ChangeProfile(name, nickName, about)

            val call: Call<ResponseRegisterLogin>? = mService.postChangeUserProfile(data)
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
                            Toast.makeText(context, "Данные изменины}", Toast.LENGTH_SHORT).show()
                            tryAuth()

                        }
                    }
                    if (response.body()?.state == false || response.body()?.state == null){
                        Toast.makeText(context, "Что пошло не так", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Log.d("TAG", "onResponse else")
                        Toast.makeText(context, "Что пошло не так", Toast.LENGTH_SHORT).show()
                    }
                }

            })

        }
    }



    fun setOnClickListeners(){
        bind.bPasswordChange.setOnClickListener {
            changePassword()
        }
        bind.bChangeAvatar.setOnClickListener {
            croper()
        }
        bind.bEdit.setOnClickListener {
            changeProfile()
        }
        bind.bAbort.setOnClickListener {

        }
    }

    fun changePassword() {


        var checkPassword = false
        var checkConfirmPassword = false

        var password = ""
        var confirmPassword = ""

        if (bind.etChangePassword.text.isNotBlank()) {
            password = bind.etChangePassword.text.toString()
            if (password.length >= 6 && password.length <= 15) {
                checkPassword = true
            } else {
                bind.etChangePassword.error =
                    "Пароль должен содержать не менее 8 символов и не более 15"
            }
        }
        if (bind.etChangeConfirmPassword .text.isNotBlank()) {
            confirmPassword = bind.etChangeConfirmPassword.text.toString()
            if (confirmPassword == password && password.length >= 6 && password.length <= 15) {
                checkConfirmPassword = true
            } else {
                bind.etChangeConfirmPassword.error = "Введённые пароли не совпадают"
            }
        }

        if (checkPassword && checkConfirmPassword) {
            val data = Password(password)


            val call: Call<ResponseRegisterLogin>? = mService.postChangePass(data)
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
                            bind.etChangePassword.setText("")
                            bind.etChangeConfirmPassword.setText("")
                             checkPassword = false
                            checkConfirmPassword = false
                            password = ""
                            confirmPassword = ""
                            Toast.makeText(context, "Пароль изменён}", Toast.LENGTH_SHORT).show()
                        }
                        if (response.body()?.state == false || response.body()?.state == null){
                            Toast.makeText(context, "Ошибка изменения пароля", Toast.LENGTH_SHORT).show()
                    }

                    }
                    else {
                        Log.d("TAG", "onResponse else&")
                        Toast.makeText(context, "Ошибка изменения пароля", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
        else{
        }


    }



    fun setContent() {
        val list = listOf<Int>()
        userData = Auth(false, AuthData("","","",0,"","","","","", list))


        try {
            userData = UserData.getUserData()!!
        } catch (e: NullPointerException) {
            // обрабатываем исключение, если метод "getUserData()" возвращает null

        }

        if (userData != null) {
            if (userData.state == true) {

                if (userData.data.name != null && userData.data.name != "") bind.etName.setText(
                    userData.data.name
                )

                if (userData.data.user_name != null && userData.data.user_name != "") bind.etNickName.setText(
                    userData.data.user_name
                )

                if (userData.data.email != null && userData.data.email != "") bind.tEmail.text =
                    ("Email - " + userData.data.email)
                else bind.tEmail.visibility = View.INVISIBLE
                if (userData.data.birth_date != null && userData.data.birth_date != "") bind.tDate.setText(
                    userData.data.birth_date
                )
                else bind.tDate.visibility = View.INVISIBLE
                if (userData.data.about != null && userData.data.about != "") bind.etAbout.setText(
                    userData.data.about
                )
                else bind.etAbout.setText("")

                if (userData.data.poster_large != null && userData.data.poster_large != "") {
                    Glide.with(bind.imageView2)
                        .load(userData.data.poster_large)
                        .placeholder(R.drawable.loaderpic)
                        .error(R.drawable.error404)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(bind.imageView2)
                }
                if (userData.data.poster_large== null && userData.data.poster_large == "") {
                    Glide.with(bind.imageView2)
                        .load(R.drawable.avatar)
                        .placeholder(R.drawable.loaderpic)
                        .error(R.drawable.error404)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(bind.imageView2)
                }
            }
        }
        }
    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 1
    }
    }


