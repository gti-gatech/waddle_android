package com.appzoro.milton.ui.view.activity.term

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.ui.view.activity.onboarding.OnBoardingActivity
import com.appzoro.milton.utility.Constant
import kotlinx.android.synthetic.main.activity_term_condition.*
import kotlinx.android.synthetic.main.layout_header.*

@SuppressLint("SetJavaScriptEnabled")
class TermConditionActivity : BaseActivity(), View.OnClickListener {

    private var title = ""
    private var url = ""
    private var mSharedPreferences: PreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_condition)
        imageViewBack.setOnClickListener(this)
        buttonAgree.setOnClickListener(this)
        mSharedPreferences = PreferenceManager(this)
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
        if (url.isNotEmpty()) loadUrl(url)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.buttonAgree -> {
                if ((mSharedPreferences?.getString(Constant.isLogin)?:"") == "1")
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                else onBackPressed()
            }
        }
    }

    private fun loadUrl(mPageUrl: String) {
        showLoading()
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.builtInZoomControls = true
        mWebView.settings.allowFileAccess = true
        mWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        mWebView.isScrollbarFadingEnabled = false
        mWebView.loadUrl(mPageUrl)
        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                hideLoading()
                buttonAgree.visibility = View.VISIBLE
            }

        }
    }

}