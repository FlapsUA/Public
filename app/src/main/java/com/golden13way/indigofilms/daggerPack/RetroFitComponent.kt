package com.golden13way.indigofilms.daggerPack


import com.golden13way.indigofilms.API.Authorization
import com.golden13way.indigofilms.MainActivity
import com.golden13way.indigofilms.adapters.RecyclerViewAdapter
import com.golden13way.indigofilms.fragments.EditProfileFragment
import com.golden13way.indigofilms.fragments.FilmPageFragment
import com.golden13way.indigofilms.fragments.LoginFragment
import com.golden13way.indigofilms.fragments.ProfileFragment
import com.golden13way.indigofilms.fragments.RegisterFragment
import com.golden13way.indigofilms.fragments.SearchFragment
import com.golden13way.indigofilms.viewModels.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, AccessTokenModule::class])
interface RetroFitComponent {

    fun inject(mainActivityViewModel: MainActivityViewModel)
    fun inject(registerFragment: RegisterFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(authorization: Authorization)
    fun inject(mainActivity: MainActivity)
    fun inject(profileFragment: ProfileFragment)
    fun inject(editProfileFragment: EditProfileFragment)
    fun inject(recyclerViewAdapter: RecyclerViewAdapter)
    fun inject(filmPageFragment: FilmPageFragment)
    fun inject(searchFragment: SearchFragment)
}

