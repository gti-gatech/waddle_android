package com.appzoro.milton.ui.view.activity.parent_details

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivityParentDetailsBinding
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.model.MediaUploadResponse
import com.appzoro.milton.ui.view.activity.selectstop.SelectStopActivity
import com.appzoro.milton.util_files.*
import com.appzoro.milton.utility.*
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_parent_details.*
import kotlinx.android.synthetic.main.layout_header_with_icons.*
import okhttp3.MultipartBody
import java.io.File
import java.util.*

class ParentDetailsActivity : BaseActivity(), View.OnClickListener,
    ProgressRequestBody.UploadCallbacks {
    private var mActivity: Activity? = null
    private var mPreferenceManager: PreferenceManager? = null
    private val galleryPicture = 101
    private val cameraRequest = 102
    private lateinit var mCameraFile: File
    private var mProgressBar: ProgressBar? = null
    private var mCount: MaterialTextView? = null
    private var mProgressDialog: ProgressDialog? = null
    private lateinit var dataBinding: ActivityParentDetailsBinding
    private lateinit var mViewModel: ParentDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_parent_details)
        mViewModel = ViewModelProviders.of(this).get(ParentDetailsViewModel::class.java)
        dataBinding.updateParentViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        textViewTitle.text = getString(R.string.parent_details_title)
        mPreferenceManager = PreferenceManager(this)
        mActivity = this@ParentDetailsActivity
        etPhone.addTextChangedListener(PhoneTextFormatter(etPhone, "(###) ###-####"))
        ivEditProfile.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
        setViewData()
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
                } else if (it is LoginResponse) disableEditable() else if (it is MediaUploadResponse) {
                    val imageName = it.data?.fd ?: ""
                    if (imageName.isNotEmpty()) {
                        mViewModel.imageUrl.value = imageName
                        loadProfileImage(imageName)
                    } else AppLogger.e("MediaUploadResponse ${Gson().toJson(it)}")
                } else AppLogger.e(" else ")
            }
        })
    }

    private fun loadProfileImage(imageName: String) {
        if (imageName.isNotEmpty()) {
            mViewModel.imageUrl.value = imageName
            Utils.loadImage(this, imageName, ivCamera, mProgress)
        }
    }

    private fun disableEditable() {
        etFullName.isEnabled = false
        etPhone.isEnabled = false
        etAddress.isEnabled = false
        tvDefaultStop.isClickable = false
        tvDefaultStop.setOnClickListener(null)
        tvSave.setOnClickListener(this)
        ivEditProfile.visibility = View.VISIBLE
        tvSave.visibility = View.GONE
        ivEditPicture.visibility = View.GONE
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                mViewModel.callApiForUpdateProfile()
            }
            R.id.ivEditProfile -> {
                etFullName.isEnabled = true
                etPhone.isEnabled = true
                etAddress.isEnabled = true
                tvDefaultStop.setOnClickListener(this)
                ivEditPicture.setOnClickListener(this)
                tvSave.setOnClickListener(this)
                UtilsNumber.setNumberFormat(etPhone)
                ivEditProfile.visibility = View.GONE
                tvSave.visibility = View.VISIBLE
                ivEditPicture.visibility = View.VISIBLE
            }
            R.id.ivEditPicture -> {
                if (AppPermissions.isCameraWithStoragePermissionGranted(mActivity!!)) {
                    imageBoothDialog()
                }
            }
            R.id.tvDefaultStop -> {
                startActivityForResult(
                    Intent(this, SelectStopActivity::class.java).putExtra(
                        "comeFrom", "profileEdit"
                    )
                        .putExtra("stopId", mViewModel.defaultStopId.value.toString())
                        .putExtra("stopName", mViewModel.defaultStopName.value.toString()), 222
                )
            }

        }
    }

    private fun setViewData() {
        if (mPreferenceManager != null) {
            mViewModel.fullName.value = (mPreferenceManager?.getString(Constant.fullName) ?: "")
            mViewModel.inputEmail.value = (mPreferenceManager?.getString(Constant.email) ?: "")
            val contact = (mPreferenceManager?.getString(Constant.contact) ?: "")
            val contactNew: String
            contactNew = //try {
                PhoneNumberUtils.formatNumber(contact, Locale.US.country)
            mViewModel.phone.value = contactNew
            mViewModel.address.value = (mPreferenceManager?.getString(Constant.address) ?: "")
            mViewModel.defaultStopId.value = (mPreferenceManager?.getString(Constant.stopId) ?: "")
            mViewModel.defaultStopName.value =
                (mPreferenceManager?.getString(Constant.stopName) ?: "")
            val image = (mPreferenceManager?.getString(Constant.image) ?: "")
            mViewModel.imageUrl.value = image
            loadProfileImage(image)
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
            if (AppPermissions.checkImagePermissionGranted(this)) imageBoothDialog()
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
                if (selectedImage == null) Utils.alertDialog(
                    mActivity!!,
                    getString(R.string.try_again)
                ) else {
                    val selectedImagePath =
                        CreateImageFile.getRealPathFromURI(mActivity!!, selectedImage)
                    if (selectedImagePath != null) uploadImageRequest(selectedImagePath)
                }
            } else if (requestCode == 222) {
                val isLocation = data?.hasExtra("location") ?: false
                if (isLocation) {
                    val location = data?.getStringExtra("location") ?: ""
                    val stopId = data?.getStringExtra("stopId") ?: ""
                    AppLogger.e("location Address  $location")
                    AppLogger.e("stopId  $stopId")
                    mViewModel.defaultStopId.value = stopId
                    mViewModel.defaultStopName.value = location
                }

            }
        }
    }

    private fun uploadImageRequest(filePath: String) {

        if (!Utils.isOnline(mActivity!!)) Utils.alertDialog(
            mActivity!!,
            getString(R.string.no_internet_connection)
        ) else {
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
            if (mProgressBar == null) mProgressBar = mProgressDialog?.findViewById(R.id.pb_loading)
            if (mCount == null) mCount = mProgressDialog?.findViewById(R.id.mCount)
            mCount?.text = percentage.toString()
            mProgressBar?.progress = percentage
        }
    }

}