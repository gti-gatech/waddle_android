package com.appzoro.milton.ui.view.activity.group_details

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivityGroupDetailsBinding
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.GroupDetailsResponse
import com.appzoro.milton.model.MediaUploadResponse
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.fragment.group_details_students.GroupDetailsStudentsFragment
import com.appzoro.milton.ui.view.fragment.group_details_trips.GroupDetailsTripsFragment
import com.appzoro.milton.util_files.*
import com.appzoro.milton.utility.AlertDialogView
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_group_details.*
import okhttp3.MultipartBody
import java.io.File

class GroupDetailsActivity : BaseActivity(), View.OnClickListener,
    ProgressRequestBody.UploadCallbacks {

    private var mActivity: Activity? = null
    private var mPreferences: PreferenceManager? = null
    private val galleryPicture = 101
    private val cameraRequest = 102
    private val editStudentRequest = 1234
    private lateinit var mCameraFile: File

    private var mProgressBar: ProgressBar? = null
    private var mCount: MaterialTextView? = null
    private var mProgressDialog: ProgressDialog? = null

    private lateinit var dataBinding: ActivityGroupDetailsBinding
    lateinit var mViewModel: GroupDetailsViewModel
    private val studentsFragment = GroupDetailsStudentsFragment.NewInstance.instance()
    private val tripsFragment = GroupDetailsTripsFragment.NewInstance.instance()

    private var groupId: String = ""
    private var comeFrom: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_group_details)
        mViewModel = ViewModelProviders.of(this).get(GroupDetailsViewModel::class.java)
        dataBinding.groupDetailsViewModel = mViewModel
        dataBinding.lifecycleOwner = this

        mPreferences = PreferenceManager(this)
        mActivity = this@GroupDetailsActivity

        if (intent.hasExtra("groupId")) {
            groupId = intent.getStringExtra("groupId") ?: ""
            mViewModel.groupId.value = groupId

            AppLogger.e("groupId $groupId")
            callApiForGetGroups(groupId)
        }

        comeFrom = intent.getStringExtra("comeFrom") ?: ""

        imageViewBack.setOnClickListener(this)
        ivEditImage.setOnClickListener(this)
        ivEditGroup.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        manageCallbacks()
        setTabLayout()

        if (comeFrom == "group" || comeFrom == "groupNav") {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewPager.setCurrentItem(1, true)
            }, 50)
        }
    }

    private fun manageCallbacks() {

        mViewModel.callback.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it is String) Utils.alertDialog(
                    this,
                    it
                ) else if (it is DialogMessage) Utils.alertDialog(
                    this,
                    it.message
                ) else if (it is Boolean) {
                    if (it) showLoading() else {
                        hideLoading()
                        mProgressDialog?.dismiss()
                    }

                } else if (it is CommonObjectResponse) {
                    tvGroupName.isEnabled = false
                    ivEditImage.visibility = View.GONE
                    tvSave.visibility = View.GONE
                    ivEditGroup.visibility = View.VISIBLE
                    Utils.alertDialog(this, (it.getMessage() ?: ""))
                } else if (it is MediaUploadResponse) {
                    val imageName = it.data?.fd ?: ""
                    if (imageName.isNotEmpty()) {
                        mViewModel.imageUrl.value = imageName
                        loadGroupImage(imageName)
                    } else AppLogger.e("MediaUploadResponse ${Gson().toJson(it)}")
                } else AppLogger.e(" else ")
            }
        })

    }

    private fun setTabLayout() {
        val viewPagerAdapter =
            GroupPagerAdapter(studentsFragment, tripsFragment, supportFragmentManager)
        mViewPager?.adapter = viewPagerAdapter
        mTabLayout?.setupWithViewPager(mViewPager)
        val firstTab = (mTabLayout!!.getChildAt(0) as ViewGroup).getChildAt(0)
        val secondTab = (mTabLayout!!.getChildAt(0) as ViewGroup).getChildAt(1)
        firstTab.background =
            ContextCompat.getDrawable(mActivity!!, R.drawable.bg_accent_left_round)
        secondTab.background =
            ContextCompat.getDrawable(mActivity!!, R.drawable.bg_white_right_round)

        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    firstTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_accent_left_round)
                    secondTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_white_right_round)
                } else {
                    firstTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_white_left_round)
                    secondTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_accent_right_round)
                }
            }

        })

    }

    private fun manageGroupDetailsData(groupDetails: GroupDetailsResponse.Data.GroupDetails?) {
        AppLogger.e("groupDetails ${Gson().toJson(groupDetails)}")
        val groupName = (groupDetails?.groupName ?: "")
        val image = (groupDetails?.image ?: "")
        tvGroupName.setText(groupName)
        mViewModel.imageUrl.value = image
        mViewModel.groupName.value = groupName
        mViewModel.groupId.value = groupId
        loadGroupImage(image)

    }

    private fun loadGroupImage(image: String) {

        if (image.isNotEmpty()) {
            Utils.loadImage(this, image, ivGroupImage, mProgress)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.imageViewBack -> {
                if (comeFrom == "groupNav") {
                    startActivity(
                        Intent(this, DashBoardActivity::class.java).putExtra(
                            "comeFrom",
                            "addInGroup"
                        )
                    )
                } else
                    onBackPressed()
            }
            R.id.tvSave -> {
                mViewModel.callApiForUpdateGroupDetails()
            }

            R.id.ivEditGroup -> {
                tvGroupName.isEnabled = true
                ivEditImage.visibility = View.VISIBLE
                ivEditGroup.visibility = View.GONE
                tvSave.visibility = View.VISIBLE
            }

            R.id.ivEditImage -> {
                if (AppPermissions.isCameraWithStoragePermissionGranted(mActivity!!)) {
                    imageBoothDialog()
                }
            }

        }
    }

    private fun imageBoothDialog() {
        val customDialogThemeClass = object : CustomDialogImage(mActivity) {
            override fun onClick(v: View) {
                when (v.id) {

                    R.id.take_picture -> {
                        dismiss()
                        takeImage()
                    }

                    R.id.select_picture -> {
                        val i = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(i, galleryPicture)
                        dismiss()
                    }

                    R.id.lin_cancel -> dismiss()
                }
            }
        }
        customDialogThemeClass.show()

    }

    private fun takeImage() {
        try {
            mCameraFile = CreateImageFile.createImageFile(mActivity!!)
            CreateImageFile.takeImageFromCamera(mActivity!!, mCameraFile, cameraRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1115) {
            if (AppPermissions.checkImagePermissionGranted(this)) {
                imageBoothDialog()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        AppLogger.e("requestCode $requestCode")
        AppLogger.e("resultCode $resultCode")

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == cameraRequest) {
                AppLogger.e("cameraRequest " + mCameraFile.absolutePath + "")
                uploadImageRequest(mCameraFile.absolutePath)

            } else if (requestCode == galleryPicture) {
                AppLogger.e("galleryPicture ${data?.data}")

                val selectedImage = data?.data
                if (selectedImage == null) {
                    Utils.alertDialog(mActivity!!, getString(R.string.try_again))
                } else {
                    val selectedImagePath =
                        CreateImageFile.getRealPathFromURI(mActivity!!, selectedImage)
                    if (selectedImagePath != null) {
                        uploadImageRequest(selectedImagePath)
                    }
                }
            } else if (requestCode == editStudentRequest) {
                if (mViewPager.currentItem == 0) {
//                   studentsFragment.onActivityResult(requestCode, resultCode, data)
                    callApiForGetGroups(groupId)
                }
            }
        }
    }

    private fun uploadImageRequest(filePath: String) {

        if (!Utils.isOnline(mActivity!!)) {
            Utils.alertDialog(mActivity!!, getString(R.string.no_internet_connection))
        } else {

            var imagePath: String
            try {
                val imageFile = FileCompress.compressImage(
                    FileUtil.from(mActivity!!, Uri.fromFile(File(filePath))), mActivity!!
                )
                imagePath = imageFile.absolutePath
            } catch (e: Exception) {
                imagePath = filePath
                e.printStackTrace()
            }
            AppLogger.e("final image path <<<<<<===>>>>> $imagePath ")


//          Glide.with(this).load(uriPath).into(ivCamera)

            mProgressDialog = AlertDialogView.showUploadingDialog(mActivity)
            val file = File(imagePath)

            val fileBody = ProgressRequestBody(file, this)
            val body = MultipartBody.Part.createFormData(
                "file", "IMG_" + System.currentTimeMillis() + ".png", fileBody
            )

            mViewModel.callApiForUploadImage(body)
        }

    }

    override fun onProgressUpdate(percentage: Int, mUploaded: Long, mTotal: Long) {
        AppLogger.e("Uploaded percentage $percentage")

        if (percentage > 0) {
            AppLogger.e("onProgressUpdate  > $percentage ")

            if (mProgressBar == null) {
                mProgressBar = mProgressDialog?.findViewById(R.id.pb_loading)
            }

            if (mCount == null) {
                mCount = mProgressDialog?.findViewById(R.id.mCount)
            }

            mCount?.text = percentage.toString()
            mProgressBar?.progress = percentage
        }
    }

    private fun callApiForGetGroups(groupId: String) {

        val url: String = ApiEndPoint.GROUPS_DETAILS + groupId
        AppLogger.e("Api url $url")

        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(this).getApi()
            .getGroupDetails(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, this)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<GroupDetailsResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: GroupDetailsResponse) {
//                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        manageGroupDetailsData(mResponse.data?.groupDetails)
                        studentsFragment.getStudentsList(mResponse.data?.groupStudents)
                        tripsFragment.getTripsList(mResponse.data?.trips)
                        hideLoading()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }


}