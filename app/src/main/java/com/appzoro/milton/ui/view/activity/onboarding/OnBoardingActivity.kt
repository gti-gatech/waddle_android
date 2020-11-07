package com.appzoro.milton.ui.view.activity.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.onboarding.addstudent.AddStudentActivity
import com.appzoro.milton.utility.Constant
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : BaseActivity(), View.OnClickListener {
    lateinit var mPreferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        mPreferenceManager= PreferenceManager(this)
        imageViewAddStudent.setOnClickListener(this)
        buttonSkip.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewAddStudent -> {
                startActivity(Intent(this, AddStudentActivity::class.java))
            }
            R.id.buttonSkip -> {
                mPreferenceManager.setString(Constant.isFirstTime,"1")
                startActivity(Intent(this, DashBoardActivity::class.java).putExtra(
                    "comeFrom",
                    ""
                ))
                finish()
            }
        }
    }
}