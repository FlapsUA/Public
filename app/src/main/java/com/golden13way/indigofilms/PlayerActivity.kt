package com.golden13way.indigofilms

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.golden13way.indigofilms.databinding.PlayerBinding
import java.net.HttpURLConnection
import java.net.URL

class PlayerActivity : AppCompatActivity() {
    lateinit var bind : PlayerBinding
    lateinit var cellClickListener: CellClickListener
    lateinit var src : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = PlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        if (savedInstanceState != null) savedInstanceState.getBundle("webViewState")?.let {
            bind.wvPlayer.restoreState(it)
        }



        //create this code in Another_activity.kt
//getting/fetch Intent value
        val data = intent.extras?.getString("srcTag")
        //Now its your choice whatever you want to do with data.
        if (data != null) {
            src = data
        }
        player()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        bind.wvPlayer.saveState(bundle)
        outState.putBundle("webViewState", bundle)
    }

    override fun onResume() {
        super.onResume()
        val flag : Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = flag
    }



    fun player() {
        val referer = "https://indigofilms.online"
        val url = URL(src)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Referer", referer)

        bind.wvPlayer.apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.pluginState = WebSettings.PluginState.ON
            settings.builtInZoomControls = false
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            setBackgroundColor(getResources().getColor(R.color.black))

            loadDataWithBaseURL(referer, "<iframe src=\"$src\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"no\" allowfullscreen fullscreen webkitallowfullscreen mozallowfullscreen></iframe>", "text/html", "utf-8", null)

            visibility = View.VISIBLE

            webViewClient = object : WebViewClient() {
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    visibility = View.GONE
                    bind.customView.visibility = View.VISIBLE
                    bind.customView.addView(view)
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    visibility = View.VISIBLE
                    bind.customView.visibility = View.GONE
                }
            }
        }
    }


}