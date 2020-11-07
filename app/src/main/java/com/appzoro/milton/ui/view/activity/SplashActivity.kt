package com.appzoro.milton.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.appzoro.milton.R
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.login.LoginActivity
import com.appzoro.milton.ui.view.activity.onboarding.OnBoardingActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    lateinit var mSharedPreferences: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mSharedPreferences = PreferenceManager(this)
        Glide.with(this).load(R.drawable.ic_splash_icon).into(imageViewSplash)
//        checkBundleValues()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mSharedPreferences.getString(Constant.isLogin).equals("1")) {
                if (mSharedPreferences.getString(Constant.isFirstTime).equals("1")) {
                    startActivity(
                        Intent(this, DashBoardActivity::class.java)
                            .putExtra(
                                "comeFrom",
                                ""
                            )
                    )
                } else
                    startActivity(Intent(this, OnBoardingActivity::class.java))
            } else
                startActivity(
                    Intent(this, LoginActivity::class.java).putExtra(
                        "comeFrom",
                        "splash"
                    )
                )
            finish()
        }, 2000)
    }

//    private fun checkBundleValues(){
//        val bundle = intent.extras
//        AppLogger.e("SplashActivity  bundle >> $bundle <<")
//        if (bundle != null) {
//            for (key in bundle.keySet()) {
//                val value = "key:- " + key + ", Value:-  " + bundle[key] + ""
//                AppLogger.e("SplashActivity  >> $value <<")
//            }
//        }
//
//    }

}