package com.appzoro.milton.ui.view.activity.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.databinding.ActivityForgotBinding
import com.appzoro.milton.ui.view.activity.login.LoginActivity
import com.appzoro.milton.utility.Utils
import kotlinx.android.synthetic.main.alert_dialog_with_image.*
import kotlinx.android.synthetic.main.layout_header.*

class ForgotActivity : BaseActivity(), View.OnClickListener {
    private lateinit var dataBinding: ActivityForgotBinding
    private lateinit var mViewModel: ForgotViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot)
        mViewModel = ViewModelProviders.of(this).get(ForgotViewModel::class.java)
        dataBinding.forgotViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        textViewTitle.text = getString(R.string.forgot_passport_title)
        imageViewBack.setOnClickListener(this)
        mViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Utils.alertDialog(
                    this,
                    it
                )

            }
        })

        mViewModel.loader.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it) showLoading() else hideLoading()

            }
        })

        mViewModel.error.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val dialogTemp = Utils.forgotSuccessAlertDialog(this, it.message)
                dialogTemp.btnOk.setOnClickListener {
                    finish()
                }

            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
        }
    }

}