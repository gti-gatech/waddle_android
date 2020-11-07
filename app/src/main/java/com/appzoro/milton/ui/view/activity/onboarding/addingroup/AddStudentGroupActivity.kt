package com.appzoro.milton.ui.view.activity.onboarding.addingroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.databinding.ActivityAddStudentGroupBinding
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.ui.OnClickInterface
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.onboarding.searchgroup.SearchGroupActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_student_group.*
import kotlinx.android.synthetic.main.layout_header_add_srudent_group.*

class AddStudentGroupActivity : BaseActivity(), View.OnClickListener {
    lateinit var dataBinding: ActivityAddStudentGroupBinding
    lateinit var mViewModel: AddStudentGroupViewModel
    lateinit var studentListData: CommonListResponse
    var comeFrom = ""
    var groupId = ""
    var stopId = ""
    lateinit var selectedStudentId: ArrayList<SelectedStudentIdModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_student_group)
        mViewModel = ViewModelProviders.of(this).get(AddStudentGroupViewModel::class.java)
        dataBinding.mAddInGroupStudent = mViewModel
        dataBinding.lifecycleOwner = this
        selectedStudentId = ArrayList()
        //
        textViewTitleGroups.text = getString(R.string.add_students)

        //get intent value
        comeFrom = intent.getStringExtra("comeFrom")!!
        if (comeFrom == "schedule" || comeFrom == "addInGroup") {
            groupId = intent.getStringExtra("groupId")!!
            stopId = intent.getStringExtra("stopId")!!
            callApiForGetGetStudentData()
        } else {
            studentListData = intent.getSerializableExtra("studentListData") as CommonListResponse
            bindDataIntoAdapter(studentListData)
        }
        //click on view
        imageViewBacks.setOnClickListener(this)
        imageViewDone.setOnClickListener(this)
        handleResponse()

    }

    private fun handleResponse() {
        mViewModel.loader.observe(this, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let { it ->
                if (it) showLoading() else hideLoading()
            }

        })

        mViewModel.error.observe(this, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let { it ->
                Utils.alertDialog(
                    this,
                    it.message
                )

            }
        })
        mViewModel.studentListData?.observe(
            this, androidx.lifecycle.Observer {
                AppLogger.d(Constant.TAG, Gson().toJson(it))
                studentListData = it
                bindDataIntoAdapter(studentListData)
            }
        )
    }

    private fun bindDataIntoAdapter(studentListData: CommonListResponse) {
        recyclerViewStudentList.setHasFixedSize(true)
        recyclerViewStudentList.adapter =
            AddStudentGroupAdapter(studentListData, object : OnClickInterface {
                override fun onClickItem(pos: Int, isSelected: Boolean) {
                    AppLogger.d(Constant.TAG, studentListData)
                    if (isSelected)
                        selectedStudentId.add(SelectedStudentIdModel(studentListData.getData()?.get(pos)?.getId()?:0))
                }
            })
        Utils.runAnimationAgain(this, recyclerViewStudentList)
    }

    private fun callApiForGetGetStudentData() {
        mViewModel.callApiForGetStudent()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBacks -> {
                onBackPressed()
            }
            R.id.imageViewDone -> {
                if (selectedStudentId.size > 0) {
                    if (comeFrom == "schedule") {
                        //TODO In Working Progress (Update in Future)
                        val intent = Intent()
                        intent.putExtra("comeFrom", "fromSchedule")
                        intent.putExtra("groupId", groupId)
                        intent.putExtra("stopId", stopId)
                        intent.putExtra("selectedStudentId", selectedStudentId)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else if (comeFrom == "addInGroup") {
                        mViewModel.callApiForJoinGroup(
                            groupId,
                            stopId,
                            selectedStudentId
                        )
                    } else {
                        val intent = Intent(this, SearchGroupActivity::class.java)
                        intent.putExtra("selectedStudentId", selectedStudentId)
                        intent.putExtra("comeFrom", "AddStudentGroup")
                        startActivity(intent)
                    }
                } else
                    Utils.alertDialog(this, "Please select student")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.joinGroupResponse?.observe(this, androidx.lifecycle.Observer {
            AppLogger.d(Constant.TAG, Gson().toJson(it))
            startActivity(
                Intent(this, DashBoardActivity::class.java).putExtra(
                    "comeFrom",
                    comeFrom
                )
            )
        })
    }
}