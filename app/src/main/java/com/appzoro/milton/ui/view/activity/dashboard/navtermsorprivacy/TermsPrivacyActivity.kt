package com.appzoro.milton.ui.view.activity.dashboard.navtermsorprivacy

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.network.ApiEndPoint
import kotlinx.android.synthetic.main.activity_terms_privacy.*
import kotlinx.android.synthetic.main.layout_header.*

@SuppressLint("SetJavaScriptEnabled")
class TermsPrivacyActivity : BaseActivity() {

    private var title = ""
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_privacy)

        imageViewBack.setOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra("comeFrom")) {
            val comeFrom = intent.getStringExtra("comeFrom") ?: ""

            if (comeFrom == "terms") {
                title = getString(R.string.term_condition_title)
                url = ApiEndPoint.TERMS_URL
            } else if (comeFrom == "privacy") {
                title = getString(R.string.privacy_policy)
                url = ApiEndPoint.PRIVACY_URL
            }

        }

        textViewTitle.text = title
        if (url.isNotEmpty()) {
            loadUrl(url)
        }
    }

    private fun loadUrl(mPageUrl: String) {
        showLoading()

        // Javascript enabled on web view
        mWebView.settings.javaScriptEnabled = true

        // Other web view options
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.builtInZoomControls = true
        mWebView.settings.allowFileAccess = true
        //mWebView.settings.setSupportZoom(true)

        //Other web view settings
        mWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        mWebView.isScrollbarFadingEnabled = false

        mWebView.loadUrl(mPageUrl)

        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                hideLoading()
            }

        }
    }

}