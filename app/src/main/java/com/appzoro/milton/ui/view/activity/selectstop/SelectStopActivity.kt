package com.appzoro.milton.ui.view.activity.selectstop

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivitySelectStopBinding
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.ui.view.activity.onboarding.AllSetActivity
import com.appzoro.milton.ui.view.activity.onboarding.addingroup.AddStudentGroupActivity
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
import kotlinx.android.synthetic.main.activity_select_stop.*
import kotlinx.android.synthetic.main.layout_header.*
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS", "NAME_SHADOWING")
class SelectStopActivity : BaseActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
    View.OnClickListener {
    private var showHomeStopId: Int = 0
    private var animationBounce: Animation? = null
    private var mGoogleMap: GoogleMap? = null
    private var mapFrag: SupportMapFragment? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private val requestCheckSetting: Int = 11
    private val permissionRequestCodeLocation: Int = 3

    private var commonListResponse: CommonListResponse? = null
    private var selectedStopId: Int = 0
    private var selectedLocation: String = ""
    private var mSharedPreferences: PreferenceManager? = null

    private lateinit var dataBinding: ActivitySelectStopBinding
    private var mViewModel: SelectStopViewModel? = null
    private var selectedStudentId: ArrayList<SelectedStudentIdModel>? = null

    private var comeFrom: String = ""
    private var routeId: String = ""
    private var groupId: String = ""
    private var stopName: String = ""
    private var stopId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_stop)
        mViewModel = ViewModelProviders.of(this).get(SelectStopViewModel::class.java)
        dataBinding.stopModel = mViewModel
        dataBinding.lifecycleOwner = this

        initializeViews()
    }

    private fun initializeViews() {
        mapFrag = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)
        //initialize animation
        mSharedPreferences = PreferenceManager(this)
        animationBounce = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        commonListResponse = CommonListResponse()
        showHomeStopId = if (mSharedPreferences?.getString(Constant.stopId)!!.isEmpty())
            0
        else
            mSharedPreferences?.getString(Constant.stopId)!!.toInt()
        //get intent data
        comeFrom = intent?.getStringExtra("comeFrom") ?: ""
        when (comeFrom) {
            "searchGroup" -> {
                selectedStudentId =
                    intent.getIntegerArrayListExtra("selectedStudentId") as ArrayList<SelectedStudentIdModel>
                routeId = intent.getStringExtra("routeId")!!
                groupId = intent.getStringExtra("groupId")!!
            }
            "schedule" -> {
                routeId = intent.getStringExtra("routeId") ?: ""
                groupId = intent.getStringExtra("groupId") ?: ""

            }
            "edit" -> {
                stopName = intent.getStringExtra("stopName")!!
                stopId = intent.getStringExtra("stopId")!!
                routeId = intent.getStringExtra("routeId") ?: ""
            }
            "addInGroup" -> {
                groupId = intent.getStringExtra("groupId")!!
            }
            "profileEdit" -> {
                stopName = intent.getStringExtra("stopName")!!
                stopId = intent.getStringExtra("stopId")!!
            }
        }
        cardViewStop.visibility = View.GONE
        imageViewBack.setOnClickListener(this)
        buttonConfirmStop.setOnClickListener(this)
        mViewModel?.callback?.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it is CommonListResponse) {
                    commonListResponse = it
                    animateCameraOnMap()
                }
            }
        })

    }

    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
        checkIfTheDeviceIsAboveMarshmallow()
        mGoogleMap?.setOnMarkerClickListener(this)
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

    //getMy current location
    private fun Boolean.getMyLocation() {
        if (!(ActivityCompat.checkSelfPermission(
                Objects.requireNonNull(this@SelectStopActivity),
                ACCESS_FINE_LOCATION
            ) === PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this@SelectStopActivity,
                ACCESS_COARSE_LOCATION
            ) === PERMISSION_GRANTED)
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

    //provide request permission for marshmallow
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(strPermission: String, perCode: Int) {
        if (shouldShowRequestPermissionRationale(strPermission))
            Utils.showToast(this, getString(R.string.gps_permission_allow_us_message))
        else
            requestPermissions(arrayOf(strPermission), perCode)
    }

    //check permission for granted or not
    private fun String.checkPermission(_c: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(_c, this)
        return result == PERMISSION_GRANTED
    }

    // Create an instance of GoogleAPIClient.
    private fun locationGetterWork() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(Objects.requireNonNull(this))
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
        val result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> true.getMyLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    try {
                        status.startResolutionForResult(this, requestCheckSetting)

                    } catch (e: IntentSender.SendIntentException) {

                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(Objects.requireNonNull(this))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

            mGoogleApiClient?.connect()
        }

        if (comeFrom == "onResult") comeFrom =
            "schedule" else if (comeFrom == "searchGroup" || comeFrom == "schedule" || comeFrom == "edit") mViewModel?.fetchMapDataBasedRouteId(
            routeId
        ) else mViewModel?.callApiForGetMapData()

        mViewModel?.loader?.observe(this, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let { it ->
                if (it) showLoading() else hideLoading()
            }

        })

        mViewModel?.error?.observe(this, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let { it ->
                Utils.alertDialog(
                    this,
                    it.message
                )

            }
        })

        mViewModel?.joinGroupResponse?.observe(this, androidx.lifecycle.Observer {
            AppLogger.d(Constant.TAG, Gson().toJson(it))
            startActivity(Intent(this, AllSetActivity::class.java))
        })

    }


    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    //call for check marshmallow compatibility
    private fun checkIfTheDeviceIsAboveMarshmallow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ACCESS_FINE_LOCATION.checkPermission(this))
                locationGetterWork()
            else
                requestPermission(
                    ACCESS_FINE_LOCATION,
                    permissionRequestCodeLocation
                )
        } else
            locationGetterWork()
    }

    private fun animateCameraOnMap() {
        Handler(Looper.getMainLooper()).postDelayed({
            mGoogleMap?.clear()
            if (commonListResponse?.getData() != null) {
                for (data in commonListResponse?.getData()!!) {
                    if (comeFrom == "schedule") {
                        if (showHomeStopId == data.getStopId()) {
                            stopName = data.getName().toString()
                            stopId = data.getStopId().toString()
                        }
                    }
                    val latitude = data.getLocation()?.getLatitude()
                    val longitude = data.getLocation()?.getLongitude()
                    if (latitude != null && longitude != null) {
                        val marker = MarkerOptions().position(LatLng(latitude, longitude))
                        mGoogleMap?.addMarker(
                            marker.title(
                                data.getName()
                            ).snippet(data.getCreatedOn())
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_pin)))
                        )?.tag = data.getStopId()
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
            if (comeFrom == "edit") showDefaultStopBanner()
            if (comeFrom == "schedule") {
                if (showHomeStopId != 0) if (stopId.isNotEmpty()) showDefaultStopBanner()
            }
            if (comeFrom == "profileEdit") if (stopId.isNotEmpty()) showDefaultStopBanner()
        }, 1000)
    }

    private fun showDefaultStopBanner() {
        cardViewStop.visibility = View.VISIBLE
        cardViewStop.startAnimation(animationBounce)
        val defaultStopName = "Default Stop: $stopName"
        textViewStopTitle.text = defaultStopName
        selectedLocation = stopName
        selectedStopId = stopId.toInt()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        cardViewStop.visibility = View.VISIBLE
        cardViewStop.startAnimation(animationBounce)
        selectedLocation = marker?.title.toString()
        val title = "Default Stop: ${marker?.title}"
        textViewStopTitle.text = title
        selectedStopId = marker?.tag as Int
        return true

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.buttonConfirmStop -> {
                confirmSelectedStop()
            }
        }
    }

    private fun confirmSelectedStop() {

        if (comeFrom == "searchGroup") {
            if (selectedStudentId != null) {
                mViewModel?.callApiForJoinGroup(
                    groupId,
                    selectedStopId.toString(),
                    selectedStudentId!!
                )
            }
        } else if (comeFrom == "schedule" || comeFrom == "addInGroup") {
            val intent = Intent(this, AddStudentGroupActivity::class.java)
            intent.putExtra("comeFrom", comeFrom)
            intent.putExtra("groupId", groupId)
            intent.putExtra("stopId", selectedStopId.toString())
            if (comeFrom == "schedule")
                startActivityForResult(intent, 1232)
            else startActivity(intent)

        } else if (comeFrom == "edit") {
            mSharedPreferences?.setString(Constant.selectedStopNameTemp, selectedLocation)
            mSharedPreferences?.setString(
                Constant.selectedStopIdTemp,
                selectedStopId.toString()
            )
            finish()
        } else {
            val resultIntent = Intent()
            resultIntent.putExtra("location", selectedLocation)
            resultIntent.putExtra("stopId", selectedStopId.toString())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

    }

    private fun getMarkerBitmapFromView(@DrawableRes resId: Int): Bitmap {
        val customMarkerView: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogger.e("SelectStop requestCode  $requestCode resultCode $resultCode")
        if (requestCode == 1232 && resultCode == Activity.RESULT_OK) {
            comeFrom = "onResult"
            val intent = Intent()
            if (data != null)
                intent.putExtras(data)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }

}