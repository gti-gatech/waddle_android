package com.appzoro.milton.utility

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.appzoro.milton.R
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.create_schedule_dialog.*
import java.util.*

class AlertDialogView {
    companion object {
        fun showDialog(view: Int, context: Context, customAnimation: Int): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            Objects.requireNonNull<Window>(window).setGravity(Gravity.CENTER)
            Objects.requireNonNull<Window>(dialog.window)
                .setBackgroundDrawableResource(android.R.color.transparent)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setContentView(view)
            dialog.setCancelable(true)
            dialog.dismiss()
            dialog.window!!.attributes.windowAnimations = customAnimation
            dialog.show()
            return dialog
        }

        fun showDialog(
            context: Context,
            icon: Int,
            title: String,
            description: String,
            buttonText: String
        ): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            Objects.requireNonNull<Window>(window).setGravity(Gravity.CENTER)
            Objects.requireNonNull<Window>(dialog.window)
                .setBackgroundDrawableResource(android.R.color.transparent)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setContentView(R.layout.alert_dialog)
            dialog.window!!.attributes.windowAnimations = R.style.CustomDialog
            dialog.textViewBodyMsg1.text = title
            dialog.imageViewClose.visibility = View.GONE
            dialog.textViewBodyMsg2.text = description
            dialog.buttonPlease.text = buttonText
            dialog.buttonPlease.setOnClickListener { dialog.dismiss() }
            dialog.imageViewFingerScan.setImageResource(icon)
            dialog.setCancelable(false)
            dialog.dismiss()
            dialog.show()
            return dialog
        }

        fun showCreateScheduleDialog(context: Context, layout: Int): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            Objects.requireNonNull<Window>(window).setGravity(Gravity.CENTER)
            Objects.requireNonNull<Window>(dialog.window)
                .setBackgroundDrawableResource(android.R.color.transparent)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setContentView(layout)
            dialog.window!!.attributes.windowAnimations = R.style.CustomDialog
            dialog.buttonDone.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(false)
            dialog.dismiss()

            dialog.show()
            return dialog
        }

        fun showScheduleTripsDialog(context: Context, layout: Int): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            Objects.requireNonNull<Window>(window).setGravity(Gravity.CENTER)
            Objects.requireNonNull<Window>(dialog.window)
                .setBackgroundDrawableResource(android.R.color.transparent)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setContentView(layout)
            dialog.window!!.attributes.windowAnimations = R.style.CustomDialog
//            dialog.buttonDone.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(false)
            dialog.dismiss()

            dialog.show()
            return dialog
        }

        fun showDialogWithTwoButton(
            context: Context,
            icon: Int,
            description: String,
            buttonText1: String,
            buttonText2: String
        ): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            Objects.requireNonNull<Window>(window).setGravity(Gravity.CENTER)
            Objects.requireNonNull<Window>(dialog.window)
                .setBackgroundDrawableResource(android.R.color.transparent)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setContentView(R.layout.alert_dialog_with_two_button)
            dialog.window!!.attributes.windowAnimations = R.style.CustomDialog
            dialog.setCancelable(false)
            dialog.textViewMessage.text = description
            dialog.buttonYes.text = buttonText1
            dialog.buttonNo.text = buttonText2
            dialog.dismiss()
            dialog.show()
            return dialog
        }


        fun createDialog(mActivity: Context, layoutResID: Int): Dialog {
            val dialog = Dialog(mActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            Objects.requireNonNull<Window>(window).setGravity(Gravity.CENTER)
            Objects.requireNonNull<Window>(dialog.window)
                .setBackgroundDrawableResource(android.R.color.transparent)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setContentView(layoutResID)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.setCancelable(true)
            dialog.show()
            return dialog
        }

        fun showUploadingDialog(
            context: Activity?
        ): ProgressDialog? {
            val progressDialog =
                ProgressDialog(context, R.style.ProgressBar)
            progressDialog.show()
            if (progressDialog.window != null) {
                progressDialog.window!!.setBackgroundDrawable(
                    ColorDrawable(
                        ContextCompat.getColor(
                            context!!,
                            R.color.dialog_trans
                        )
                    )
                )
                progressDialog.window!!.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            progressDialog.setContentView(R.layout.dialog_progress_uploading_count)
            progressDialog.isIndeterminate = false
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            return progressDialog
        }


    }
}