package com.appzoro.milton.ui.view.fragment.group_nearby

import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.content.ContextCompat
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_nearby.*
import java.util.Objects.requireNonNull

class NearbyFragment : BaseFragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {
    private var mSharedPreferences: PreferenceManager? = null
    private var showHomeStopId: Int = 0
    private var animationBounce: Animation? = null

    object NewInstance {
        fun instance(): NearbyFragment {
            val fragment =
                NearbyFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var mView: View? = null
    private var mActivity: Activity? = null
    private var mGoogleMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private val requestCheckSetting: Int = 11
    private val permissionRequestCodeLocation: Int = 3
    private var commonListResponse: CommonListResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_nearby, container, false)
        mView = root

        initializeViews()
        return root
    }

    private fun initializeViews() {
        val mapFrag = childFragmentManager
            .findFragmentById(R.id.mapNearBy) as SupportMapFragment?
        mapFrag?.getMapAsync(this)
        animationBounce = AnimationUtils.loadAnimation(activity, R.anim.slide_down)

    }

    override fun setUp(view: View) {
        mActivity = activity
        mSharedPreferences = PreferenceManager(mActivity!!)
        showHomeStopId = (mSharedPreferences?.getString(Constant.stopId) ?: "0").toInt()

    }

    override fun onMapReady(p0: GoogleMap?) {

        mGoogleMap = p0!!
        checkIfTheDeviceIsAboveMarshmallow()
        mGoogleMap?.setOnMarkerClickListener(this)
    }


    // Create an instance of GoogleAPIClient.
    private fun locationGetterWork() {
        mGoogleMap?.uiSettings?.setAllGesturesEnabled(true)
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(mActivity!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
            mGoogleApiClient?.connect()
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> true.getMyLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied. But could be fixed by showing the user
                    try {
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(mActivity, requestCheckSetting)

                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// settings so we won't show the dialog.
        }
    }

    //getMy current location
    private fun Boolean.getMyLocation() {
        if (checkSelfPermission(
                requireNonNull(mActivity!!),
                ACCESS_FINE_LOCATION
            ) !== PERMISSION_GRANTED && checkSelfPermission(
                mActivity!!,
                permission.ACCESS_COARSE_LOCATION
            ) !== PERMISSION_GRANTED
        ) {
            return
        }

        if (this) Handler(Looper.getMainLooper()).postDelayed({
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient
            )
            if (mLastLocation != null) animateCameraOnMap()
        }, 100) else animateCameraOnMap()
    }

    //call back for permission result
    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCodeLocation -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PERMISSION_GRANTED
            )
                locationGetterWork()
        }
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onResume() {
        super.onResume()
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(mActivity!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

            mGoogleApiClient?.connect()
        }

        callApiForGetMapData()
    }



    private fun callApiForGetMapData() {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi().getCommonStop(
            ApiEndPoint.COMMON_STOP
        ).doOnError {
            temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
        }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonListResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: CommonListResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        if (commonListResponse == null) {
                            commonListResponse = CommonListResponse()
                        }
                        commonListResponse = mListResponse
                        animateCameraOnMap()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()

                        e.printStackTrace()
                        Utils.alertDialog(
                            mActivity!!, (temError?.mMessage ?: "")
                        )
                    }

                })
    }

    //call for check marshmallow compatibility
    private fun checkIfTheDeviceIsAboveMarshmallow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(ACCESS_FINE_LOCATION, mActivity!!))
                locationGetterWork()
            else
                requestPermission(
                    ACCESS_FINE_LOCATION,
                    permissionRequestCodeLocation
                )
        } else
            locationGetterWork()
    }

    //provide request permission for marshmallow
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(strPermission: String, perCode: Int) {
        if (shouldShowRequestPermissionRationale(strPermission))
            Utils.showToast(mActivity!!, getString(R.string.gps_permission_allow_us_message))
        else
            requestPermissions(arrayOf(strPermission), perCode)
    }

    //check permission for granted or not
    private fun checkPermission(strPermission: String, _c: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(_c, strPermission)
        return result == PERMISSION_GRANTED
    }

    private fun animateCameraOnMap() {
        Handler(Looper.getMainLooper()).postDelayed({
            mGoogleMap?.clear()
            if (commonListResponse?.getData() != null) {
                for (i in commonListResponse?.getData()!!.indices) {
                    val latitude =
                        commonListResponse?.getData()?.get(i)?.getLocation()?.getLatitude()
                    val longitude =
                        commonListResponse?.getData()?.get(i)?.getLocation()?.getLongitude()
                    if (latitude != null && longitude != null) {
                        val marker = MarkerOptions().position(LatLng(latitude, longitude))
                        if (commonListResponse?.getData() != null) {
                            if (showHomeStopId == commonListResponse?.getData()?.get(i)
                                    ?.getStopId()
                            ) {
                                mGoogleMap?.addMarker(
                                    marker.title(
                                        commonListResponse?.getData()?.get(i)?.getName()
                                    ).snippet(commonListResponse?.getData()?.get(i)?.getGroupName())
                                        .icon(
                                            BitmapDescriptorFactory.fromBitmap(
                                                getMarkerBitmapFromView(
                                                    R.drawable.ic_home_map
                                                )
                                            )
                                        )
                                )?.tag = commonListResponse?.getData()?.get(i)?.getStopId()
                            } else {
                                mGoogleMap?.addMarker(
                                    marker.title(
                                        commonListResponse?.getData()?.get(i)?.getName()
                                    ).snippet(commonListResponse?.getData()?.get(i)?.getGroupName())
                                        .icon(
                                            BitmapDescriptorFactory.fromBitmap(
                                                getMarkerBitmapFromView(R.drawable.ic_pin)
                                            )
                                        )
                                )?.tag = commonListResponse?.getData()?.get(i)?.getStopId()
                            }
                        }
                    }
                }
            }
            /*
            * long: -84.351978
            * lat: 34.089545
            * */
            mGoogleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
//                        mLastLocation?.latitude!!,
//                        mLastLocation?.longitude!!
                        34.089545,
                        -84.351978
                    ), 14f
                )
            )
            mGoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null)
        }, 1000)
    }

    private fun getMarkerBitmapFromView(@DrawableRes resId: Int): Bitmap {
        val customMarkerView: View =
            (mActivity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_custom_marker,
                null
            )
        val markerImageView: ImageView =
            customMarkerView.findViewById<View>(R.id.profile_image) as ImageView
        markerImageView.setImageResource(resId)
        customMarkerView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap: Bitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        customMarkerView.background?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) fragmentManager!!.beginTransaction().detach(this).attach(this).commit()

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        cardViewStop.visibility = View.VISIBLE
        cardViewStop.startAnimation(animationBounce)
        val title = "Default Stop: ${marker?.title}"
        val groupName = marker?.snippet
        textViewStopTitle.text = title
        textViewGroupName.text = groupName
        return true

    }

}
