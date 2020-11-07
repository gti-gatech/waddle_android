package com.appzoro.milton.ui.view.activity.onboarding.searchgroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivitySearchGroupBinding
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.GroupModelData
import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.ui.ItemClick
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.selectstop.SelectStopActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_group.*
import kotlinx.android.synthetic.main.layout_header_with_icons.*
import kotlinx.android.synthetic.main.layout_header_with_text.imageViewBack
import java.util.*
import kotlin.collections.ArrayList

class SearchGroupActivity : BaseActivity(), View.OnClickListener, TextWatcher {
    private var comeFrom: String = ""
    private lateinit var mSearchAdapter: SearchGroupAdapter
    var selectedStudentId: ArrayList<SelectedStudentIdModel>? = null
    lateinit var dataBinding: ActivitySearchGroupBinding
    lateinit var mViewModel: SearchGroupViewModel
    lateinit var groupListData: CommonListResponse
    lateinit var groupListRecordData: ArrayList<GroupModelData>
    lateinit var mPreferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_group)
        mViewModel = ViewModelProviders.of(this).get(SearchGroupViewModel::class.java)
        dataBinding.searchViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        mPreferenceManager = PreferenceManager(this)
        textViewTitle.text = ""
        comeFrom = intent.getStringExtra("comeFrom")!!
        if (comeFrom == "schedule") {
            //call api for get group
        } else if (comeFrom == "addInGroup") {
            //call from group fragment
        } else {
            selectedStudentId =
                intent.getIntegerArrayListExtra("selectedStudentId") as ArrayList<SelectedStudentIdModel>
        }
        imageViewEdit.setImageResource(R.drawable.ic_cancel_update)
        imageViewEdit.visibility = View.VISIBLE
        //click on view
        imageViewBack.setOnClickListener(this)
        imageViewEdit.setOnClickListener(this)
        handleResponse()
        searchView.addTextChangedListener(this)
    }

    private fun handleResponse() {
        mViewModel.callApiForSearchGroup()
        mViewModel.loader.observe(this, Observer {
            it.getContentIfNotHandled()?.let { it ->
                if (it) showLoading() else hideLoading()
            }

        })
        mViewModel.error.observe(this, Observer {
            it.getContentIfNotHandled()?.let { it ->
                Utils.alertDialog(
                    this,
                    it.message
                )

            }
        })
        mViewModel.groupListData?.observe(
            this, Observer {
                AppLogger.d(Constant.TAG, Gson().toJson(it))
                groupListData = it
                groupListRecordData = ArrayList()
                for (index in groupListData.getData()!!.indices) {
                    groupListRecordData.add(
                        GroupModelData(
                            groupId = groupListData.getData()?.get(index)?.getGroupId() ?: 0,
                            groupName = groupListData.getData()?.get(index)?.getGroupName() ?: "",
                            image = groupListData.getData()?.get(index)?.getImage().toString(),
                            routeId = groupListData.getData()?.get(index)?.getRouteId()!!.toInt(),
                            totalStudents = groupListData.getData()?.get(index)?.getTotalStudents()
                                ?: 0,
                            totalTrip = groupListData.getData()?.get(index)?.getTotalTrips() ?: 0,
                            onCreatedTime = groupListData.getData()?.get(index)?.getCreatedOn()
                                ?: ""
                        )
                    )
                }
                bindDataIntoAdapter()
            }
        )
    }

    private fun bindDataIntoAdapter() {
        recyclerViewGroupList.setHasFixedSize(true)
        mSearchAdapter =
            SearchGroupAdapter(this, groupListRecordData, object : ItemClick {
                override fun onItemClick(pos: Int) {
                    AppLogger.d(Constant.TAG, groupListRecordData)
                    val intent = Intent(this@SearchGroupActivity, SelectStopActivity::class.java)
                    if (comeFrom == "schedule" || comeFrom == "addInGroup") intent.putExtra(
                        "comeFrom",
                        comeFrom
                    ) else intent.putExtra("comeFrom", "searchGroup")
                    intent.putExtra("selectedStudentId", selectedStudentId)
                    val routeId = (groupListData.getData()?.get(pos)?.getRouteId() ?: "").toString()
                    val groupId = (groupListData.getData()?.get(pos)?.getGroupId() ?: "").toString()
                    intent.putExtra("routeId", routeId)
                    intent.putExtra("groupId", groupId)
                    if (comeFrom == "schedule") startActivityForResult(
                        intent,
                        1232
                    ) else startActivity(intent)
                }

            })
        recyclerViewGroupList.adapter = mSearchAdapter
        Utils.runAnimationAgain(this, recyclerViewGroupList)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.imageViewEdit -> {
                mPreferenceManager.setString(Constant.isFirstTime, "1")
                startActivity(
                    Intent(this, DashBoardActivity::class.java).putExtra(
                        "comeFrom",
                        ""
                    )
                )
                finish()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        filter(p0.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun filter(search: String) { //new array list that will hold the filtered data
        val messagesDataList: ArrayList<GroupModelData> =
            ArrayList()
        for (i in groupListRecordData.indices) {
            if (groupListRecordData[i].groupName.isNotEmpty())
                if (groupListRecordData[i].groupName.toLowerCase(Locale.getDefault()).contains(
                        search.toLowerCase(Locale.getDefault())
                    )
                ) messagesDataList.add(groupListRecordData[i])
        }
        mSearchAdapter.filterList(messagesDataList)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogger.e("SelectGroup requestCode  $requestCode resultCode $resultCode")
        if (requestCode == 1232 && resultCode == Activity.RESULT_OK) {
            val intent = Intent()
            if (data != null)
                intent.putExtras(data)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}