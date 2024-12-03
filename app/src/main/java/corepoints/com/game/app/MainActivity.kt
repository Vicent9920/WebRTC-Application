package corepoints.com.game.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.digitalfun.app2.R


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.webView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<WebView>(R.id.webView).run {
            settings.apply {
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                setEnableSmoothTransition(false)
                allowFileAccess = true
                blockNetworkImage = false
                blockNetworkLoads = false
                builtInZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                databaseEnabled = false
                displayZoomControls = false
                allowFileAccess = true
                allowContentAccess = true
                domStorageEnabled = true
                textZoom = 100
                setGeolocationEnabled(false)
                javaScriptCanOpenWindowsAutomatically = false
                javaScriptEnabled = true
                lightTouchEnabled = false
                loadsImagesAutomatically = true
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = 1
                setNeedInitialFocus(true)
                pluginState = WebSettings.PluginState.OFF
                setRenderPriority(WebSettings.RenderPriority.NORMAL)
                saveFormData = false
                savePassword = false
                setSupportMultipleWindows(false)
                setSupportZoom(false)
                useWideViewPort = true
                loadWithOverviewMode = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//                ViewCompat.setOverScrollMode(View.OVER_SCROLL_NEVER)
            }
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            setInitialScale(0)
            requestDisallowInterceptTouchEvent(true)
            loadUrl("https://2fhhg.win/index.html?partner=88880001")
        }


    }





}