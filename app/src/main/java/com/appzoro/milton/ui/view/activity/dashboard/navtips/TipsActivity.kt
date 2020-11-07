package com.appzoro.milton.ui.view.activity.dashboard.navtips

import android.content.Intent
import android.os.Bundle
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import kotlinx.android.synthetic.main.layout_header.*


class TipsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_tips)
        textViewTitle.text=getString(R.string.tips)
        imageViewBack.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java).putExtra(
                "comeFrom",
                ""
            ))
        }
    }
}