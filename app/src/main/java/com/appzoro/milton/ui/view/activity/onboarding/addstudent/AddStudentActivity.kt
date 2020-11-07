package com.appzoro.milton.ui.view.activity.onboarding.addstudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.databinding.ActivityAddStudentBinding
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.ui.view.activity.onboarding.addingroup.AddStudentGroupActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.appzoro.milton.utility.UtilsNumber
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_student.*
import kotlinx.android.synthetic.main.alert_dialog_with_image.*
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.layout_header_with_icons.*

class AddStudentActivity : BaseActivity(), View.OnClickListener {
    var comeFrom: String = ""
    lateinit var dataBinding: ActivityAddStudentBinding
    lateinit var mViewModel: AddStudentViewModel

    private var studentItem: DatumModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_student)
        mViewModel = ViewModelProviders.of(this).get(AddStudentViewModel::class.java)
        dataBinding.addStudentViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        //click on view
        imageViewBack.setOnClickListener(this)
        imageViewEdit.setOnClickListener(this)
        imageViewDelete.setOnClickListener(this)

        textViewTitle.text = getString(R.string.add_student_title)
        comeFrom = intent.getStringExtra("comeFrom").toString()
        if (comeFrom == "edit") {
            updateUiData()
        }
        mViewModel.message.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                Utils.alertDialog(this, it)
            }
        })

        mViewModel.loader.observe(this, androidx.lifecycle.Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it) showLoading() else hideLoading()
            }
        })

        mViewModel.addStudentListResponse?.observe(this, Observer {
            if (comeFrom == "student") {
                val dialog = Utils.forgotSuccessAlertDialog(this, it.getMessage().toString())
                dialog.btnOk.setOnClickListener {
                    setResultForActivity()
                }
            } else {
                Utils.showToast(this, it.getMessage().toString())
                val intent = Intent(this, AddStudentGroupActivity::class.java)
                intent.putExtra("studentListData", it)
                intent.putExtra("comeFrom", "")
                startActivity(intent)
            }
            clearUiData()
        })

        mViewModel.errorDialog.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                Utils.alertDialog(this, it.message)
            }
        })
        mViewModel.callback.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it is CommonObjectResponse) {
                    val dialog = Utils.forgotSuccessAlertDialog(this, it.getMessage().toString())
                    dialog.btnOk.setOnClickListener {
                        setResultForActivity()
                    }
                } else AppLogger.e(" else ")
            }
        })
        UtilsNumber.removeWhiteSpace(editTextEmail)
    }

    private fun clearUiData() {
        mViewModel.inputStudentName.value = ""
        mViewModel.inputEmail.value = ""
        mViewModel.inputSchoolName.value = ""
        mViewModel.inputStudentGrade.value = ""
    }

    private fun updateUiData() {
        if (intent.hasExtra("studentItem")) {
            studentItem = intent.getSerializableExtra("studentItem") as DatumModel
            AppLogger.e("studentItem >>> ${Gson().toJson(studentItem)}")
            if (studentItem != null) mViewModel.studentItem?.value = studentItem
            mViewModel.comeFrom = comeFrom
            mViewModel.inputStudentName.value = (studentItem?.getFullName() ?: "")
            mViewModel.inputEmail.value = (studentItem?.getEmail() ?: "")
            mViewModel.inputSchoolName.value = (studentItem?.getSchoolName() ?: "")
            mViewModel.inputStudentGrade.value = (studentItem?.getGrade() ?: "")
            editTexStudentName.isEnabled = false
            editTextEmail.isEnabled = false
            editTextSchoolName.isEnabled = false
            editTextStudentGrade.isEnabled = false
            imageViewEdit.visibility = View.VISIBLE
            imageViewDelete.visibility = View.VISIBLE
            mSave.visibility = View.GONE
        }
    }

    private fun setResultForActivity() {
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.imageViewEdit -> {
                editTexStudentName.isEnabled = true
                editTextEmail.isEnabled = true
                editTextSchoolName.isEnabled = true
                editTextStudentGrade.isEnabled = true
                imageViewEdit.visibility = View.GONE
                mSave.visibility = View.VISIBLE
            }
            R.id.imageViewDelete -> {
                deleteDialog()
            }
        }
    }

    private fun deleteDialog() {
        val mDialog = Utils.alertDialogWithTwoButton(
            this,
            getString(R.string.are_you_sure_want_delete_stundent_details)
        )
        mDialog.buttonYes.setOnClickListener {
            mDialog.dismiss()
            AppLogger.e("setOnClickListener buttonYes")
            mViewModel.callApiForDeleteStudent()
        }
    }

}