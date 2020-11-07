package com.appzoro.milton.ui.view.activity.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager.widget.ViewPager
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.MediaUploadResponse
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.onboarding.OnBoardingActivity
import com.appzoro.milton.ui.view.fragment.SignInFragment
import com.appzoro.milton.ui.view.fragment.sign_up.SignUpFragment
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
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MultipartBody
import java.io.File

class LoginActivity : BaseActivity(), ProgressRequestBody.UploadCallbacks {

    private var mActivity: Activity? = null
    private val galleryPicture = 101
    private val cameraRequest = 102
    private lateinit var mCameraFile: File

    private var mProgressBar: ProgressBar? = null
    private var mCount: MaterialTextView? = null
    private var mProgressDialog: ProgressDialog? = null
    private var comeFrom: String = ""
    private var selectedTab: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mActivity = this@LoginActivity
        PreferenceManager(this).setString(Constant.profileImage, "")
        comeFrom = intent.getStringExtra("comeFrom") ?: ""

        if (comeFrom == "forgot" || comeFrom == "logout") {
            selectedTab = 1
        }

        val viewPagerAdapter = LoginPagerAdapter(this, supportFragmentManager)
        mViewPager.adapter = viewPagerAdapter
        mTabLayout.setupWithViewPager(mViewPager, true)

//        mTabLayout.setScrollPosition(selectedTab,0f,true)
        Handler(Looper.getMainLooper()).postDelayed({
            mViewPager.setCurrentItem(selectedTab, true)
        }, 50)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onPageSelectedPosition(position)
            }
        })

        ivCamera.setOnClickListener {
            if (AppPermissions.isCameraWithStoragePermissionGranted(mActivity!!)) {
                imageBoothDialog()
            }
        }

    }

    private fun onPageSelectedPosition(position: Int) {
        AppLogger.e("onPageSelected position $position")
        if (position == 0) {
            ivCamera.visibility = View.VISIBLE
            ivLogo.visibility = View.GONE
        } else {
            ivCamera.visibility = View.GONE
            ivLogo.visibility = View.VISIBLE
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
            } else if (requestCode == 222) {

                for (fragment in supportFragmentManager.fragments) {
                    fragment.onActivityResult(requestCode, resultCode, data)
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
            mProgressDialog = AlertDialogView.showUploadingDialog(mActivity)
            val file = File(imagePath)
            val fileBody = ProgressRequestBody(file, this)
            val body = MultipartBody.Part.createFormData(
                "file", "IMG_" + System.currentTimeMillis() + ".png", fileBody
            )

            callApiForUploadImage(body)
        }

    }

    private fun callApiForUploadImage(fileBody: MultipartBody.Part) {
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(this)

        RetrofitService.getInstance(this).getApi()
            .uploadImage(fileBody, (mPreferenceManager.getString(Constant.authToken) ?: ""))
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, this)
            }.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<MediaUploadResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: MediaUploadResponse) {
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        mProgressDialog?.dismiss()
                        loadProfileImage(mResponse)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        mProgressDialog?.dismiss()

                        Utils.alertDialog(
                            mActivity!!, (temError?.mMessage ?: Constant.emptyString)
                        )

                    }

                })
    }

    private fun loadProfileImage(mResponse: MediaUploadResponse) {

        val imageName = mResponse.data?.fd ?: ""
        if (imageName.isNotEmpty()) {
//            val url = ApiEndPoint.BASE_MEDIA_URL + imageName
//            Glide.with(this).load(url).error(R.drawable.ic_camera).into(ivCamera)

            val preference = PreferenceManager(this)
            preference.setString(Constant.profileImage_url, imageName)

            Utils.loadImage(this, imageName, ivCamera, mProgress)
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

//            AppLogger.e("mCount > $mCount ")
//            AppLogger.e("mProgressBar > $mProgressBar ")

            mCount?.text = percentage.toString()
            mProgressBar?.progress = percentage
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}