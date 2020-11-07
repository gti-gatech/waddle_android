package com.appzoro.milton.ui.view.activity.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appzoro.milton.R
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.utility.Constant
import kotlinx.android.synthetic.main.activity_all_set.*
import kotlinx.android.synthetic.main.layout_header_with_text.imageViewBack

class AllSetActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var mPreferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_set)
        mPreferenceManager = PreferenceManager(this)

        imageViewBack.setOnClickListener(this)
        buttonDone.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.buttonDone -> {
                mPreferenceManager.setString(Constant.isFirstTime, "1")
                startActivity(Intent(this, DashBoardActivity::class.java).putExtra(
                    "comeFrom",
                    ""
                ))
            }
        }
    }
}