package com.appzoro.milton.ui.view.activity.trackmap

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.appzoro.milton.BuildConfig
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.MiltonApplication
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.GetMapTrackResponse
import com.appzoro.milton.model.StopLocationModel
import com.appzoro.milton.network.*
import com.appzoro.milton.ui.ItemClick
import com.appzoro.milton.ui.view.PickStatus
import com.appzoro.milton.ui.view.activity.group_details.GroupDetailsActivity
import com.appzoro.milton.utility.AlertDialogView
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Constant.Companion.TAG
import com.appzoro.milton.utility.Utils
import com.asif.asiflocationgetter.EasyLocation
import com.asif.asifroutedirectionlibrary.GoogleDirection
import com.asif.asifroutedirectionlibrary.config.GoogleDirectionConfiguration
import com.asif.asifroutedirectionlibrary.constant.TransportMode
import com.asif.asifroutedirectionlibrary.model.Direction
import com.asif.asifroutedirectionlibrary.model.Route
import com.asif.asifroutedirectionlibrary.util.DirectionConverter
import com.asif.asifroutedirectionlibrary.util.execute
import com.github.nkzawa.socketio.client.Socket
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
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_track_map.*
import kotlinx.android.synthetic.main.alert_dialog_with_image.*
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.content_slide_up.layoutUpcomingStudent
import kotlinx.android.synthetic.main.content_slide_up.mRecyclerPickStudent
import kotlinx.android.synthetic.main.content_slide_up.noDataFoundLayoutPick
import kotlinx.android.synthetic.main.content_slide_up.textViewDistance
import kotlinx.android.synthetic.main.content_slide_up.textViewRouteName
import kotlinx.android.synthetic.main.content_slide_up.textViewTime
import kotlinx.android.synthetic.main.layout_header_with_text.*
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

class TrackMapActivity : BaseActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var travelTime: String = "0 minute"
    private var travelDistance: String = "0.0 km"
    private lateinit var mGoogleMap: GoogleMap
    private var mapFrag: SupportMapFragment? = null
    private lateinit var mLocationRequest: LocationRequest
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private val requestCheckSetting: Int = 11
    private val permissionRequestCodeLocation: Int = 3
    lateinit var mSharedPreferences: PreferenceManager
    var mapListData = ArrayList<GetMapTrackResponse.Data.Datum>()
    var mapStopPoint = ArrayList<StopLocationModel>()
    private var mAdapter: TrackItemAdapter? = null
    private var mAdapterParent: TrackParentAdapter? = null
    private var isTripStarted: Boolean = false
    //for socket
    private var mSocket: Socket? = null
    private var parentId = ""
    private var tripId: Int = 0
    private var groupId: Int = 0
    private var isSupervisor: Int = 0

    companion object {

        private const val serverKey = Constant.mapApiKey

        // Milliseconds per second
        private val MILLISECONDS_PER_SECOND = 1000

        // Update frequency in seconds
        private val UPDATE_INTERVAL_IN_SECONDS = 5

        // Update frequency in milliseconds
        private val UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS.toLong()

        // The fastest update frequency, in seconds
        private val FASTEST_INTERVAL_IN_SECONDS = 1

        // A fast frequency ceiling in milliseconds
        private val FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS.toLong()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_map)
        initializeViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeViews() {
        mapFrag = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag!!.getMapAsync(this)
        //initialize animation
        val app = application as MiltonApplication
        mSocket = app.getSocket()!!
        mSharedPreferences = PreferenceManager(this)
        tripId = intent.getIntExtra("tripId", 0)
        groupId = intent.getIntExtra("groupId", 0)
        isSupervisor = intent.getIntExtra("isSupervisor", 0)
        parentId = mSharedPreferences.getString(Constant.parentId)!!
        AppLogger.e("isSupervisor >>> $isSupervisor")

        if (isSupervisor == 1) {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheets)
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    val tempTest = when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
                        BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
                        else -> "Persistent Bottom Sheet"
                    }
                }
            })
        } else {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetsParent)
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    val tempTest = when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
                        BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
                        else -> "Persistent Bottom Sheet"
                    }
                }
            })
        }

        Handler(Looper.getMainLooper()).postDelayed({
            connectSocketConnection()
        }, 500)

        //initialize map list array
        mapListData = ArrayList()
        getLocationUpdates()

        mAdapter = TrackItemAdapter(this, object : ItemClick {
            override fun onItemClick(pos: Int) {
                emitSocketForMarkPresent(pos)
            }
        })
        mAdapterParent = TrackParentAdapter(this, object : ItemClick {
            override fun onItemClick(pos: Int) {
                emitSocketForMarkPresent(pos)
            }
        })
        //mAdapterNormal = TrackNormalStudentAdapter(this)
        mRecyclerPickStudent.adapter = mAdapter
        mRecyclerPickStop.adapter = mAdapterParent
        //call api for get data and plot on map
        getDataForPlotOnMap()
        layoutUpcomingStudent.visibility = View.GONE
        //click for back and end trip
        if (isSupervisor != 1) {
            textViewDone.visibility = View.GONE
            textViewDone.text = ""
            bottomSheetsParent.visibility = View.VISIBLE
            bottomSheets.visibility = View.GONE
        } else {
            textViewDone.visibility = View.VISIBLE
            textViewDone.text = "End Trip"
            bottomSheetsParent.visibility = View.GONE
            bottomSheets.visibility = View.VISIBLE
        }

        imageViewBack.setOnClickListener {
            onBackPressed()
        }
        textViewDone.setOnClickListener {
            showConfirmationDialog()
        }

    }

    private fun showConfirmationDialog() {
        val logoutDialog = AlertDialogView.showDialogWithTwoButton(
            this,
            R.drawable.ic_signin_logo,
            getString(R.string.trip_end_msg),
            getString(R.string.yes),
            getString(R.string.no)
        )
        logoutDialog.buttonNo.setOnClickListener {
            logoutDialog.dismiss()

        }
        logoutDialog.buttonYes.setOnClickListener {
            logoutDialog.dismiss()
            //call api for end trip
            callApiForEndTrip()
        }

    }

    private fun connectSocketConnection() {
        checkForJoinTrips()
    }

    private fun upcomingDataUiUpdate() {
        for (index in 0 until mapListData.size) {
            if (mapListData[index].status == PickStatus.NOT_PICKED.toString()) {
                if (isSupervisor == 1) {
                    if (isTripStarted)
                        layoutUpcomingStudent.visibility = View.VISIBLE
                    else
                        layoutUpcomingStudent.visibility = View.GONE
                } else
                    layoutUpcomingStudent.visibility = View.GONE
                val upcomingStudentName = mapListData[index].studentName.toString()
                val upcomingStudentLat = mapListData[index].stopLocation!!.getLatitude()
                val upcomingStudentLong = mapListData[index].stopLocation!!.getLongitude()

                if (mLastLocation != null) {
                    getDistanceAndTimeFromGoogleMatrixApi(
                        mLastLocation!!.latitude,
                        mLastLocation!!.longitude,
                        upcomingStudentLat,
                        upcomingStudentLong,
                        mapListData[index].pickupCount,
                        mapListData[index].stopName
                    )
                }
                return
            } else {
                layoutUpcomingStudent.visibility = View.GONE
            }
        }
    }

    private fun emitSocketForMarkPresent(pos: Int) {
        if (mSocket!!.connected()) {
            val requestBody = JsonRequestBody().socketMarkPresentRequest(
                mapListData[pos].studentId.toString(),
                tripId
            )
            mSocket?.emit(ApiEndPoint.markPresent, requestBody)
        }
    }

    override fun onDestroy() {
        mSocket?.disconnect()
        super.onDestroy()
    }

    private fun showHideViews() {
        textViewDone.isVisible = isTripStarted
        if (isSupervisor == 1) {
            if (isTripStarted)
                layoutUpcomingStudent.visibility = View.VISIBLE
            else
                layoutUpcomingStudent.visibility = View.GONE
        } else layoutUpcomingStudent.visibility = View.GONE
    }


    private fun getLocationUpdates() {
        EasyLocation(this, object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {
                Timber.i("permission  denied")
            }

            override fun locationSettingFailed() {
                Timber.i("setting failed")
            }

            @SuppressLint("TimberArgCount")
            override fun getLocation(location: Location) {
                if (mLastLocation != null) {
                    mLastLocation = location
                    publishLocation()
                    upcomingDataUiUpdate()

                }
            }
        })
    }

    @SuppressLint("LogNotTimber")
    private fun publishLocation() {
        mSocket?.on("connect") {
            Log.e(TAG, "publish location on!!")
        }
        if (mSocket!!.connected()) {
            val requestBody = JsonRequestBody().socketUpdateLocationRequest(
                mLastLocation!!.latitude,
                mLastLocation!!.longitude,
                parentId,
                tripId
            )
            Log.e(TAG, "Connected for publish location!!")
            mSocket?.emit(ApiEndPoint.updateLocation, requestBody)
        } else
            Log.e(TAG, "Not Connected!!")
    }


    override fun onMapReady(p0: GoogleMap?) {
        mGoogleMap = p0!!
        checkIfTheDeviceIsAboveMarshmallow()
    }

    private fun requestDirection() {
        val origin = LatLng(
            mapStopPoint[0].latitude,
            mapStopPoint[0].longitude
        )
        val destination = LatLng(
            mapStopPoint[mapStopPoint.size - 1].latitude,
            mapStopPoint[mapStopPoint.size - 1].longitude
        )
        val returnWayPointsValue = getWayPointsList(mapStopPoint)
        GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
        GoogleDirection.withServerKey(serverKey)
            .from(origin)
            .and(returnWayPointsValue)
            .to(destination)
            .transportMode(TransportMode.DRIVING)
            .execute(
                onDirectionSuccess = { direction -> onDirectionSuccess(direction) },
                onDirectionFailure = { t -> onDirectionFailure(t) }
            )
    }

    private fun getDistanceAndTimeFromGoogleMatrixApi(
        mLastLat: Double,
        mLastLong: Double,
        upcomingStudentLat: Double,
        upcomingStudentLong: Double,
        pickupCount: Int?,
        stopName: String?
    ) {
        val start = LatLng(mLastLat, mLastLong)
        val end = LatLng(upcomingStudentLat, upcomingStudentLong)
        GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
        GoogleDirection.withServerKey(serverKey)
            .from(start)
            .to(end)
            .transportMode(TransportMode.DRIVING)
            .execute(
                onDirectionSuccess = { direction ->
                    onDirectionSuccessForTimeAndDistance(
                        direction,
                        pickupCount,
                        stopName
                    )
                },
                onDirectionFailure = { t -> onDirectionFailure(t) }
            )
    }

    private fun onDirectionSuccessForTimeAndDistance(
        direction: Direction,
        pickupCount: Int?,
        stopName: String?
    ) {
        if (direction.isOK) {
            if (direction.routeList[0] != null) {
                val route = direction.routeList[0]
                val legCount = route.legList.size
                Log.d("Location", "leg" + legCount)
                if (legCount > 0) {
                    travelDistance = if (route.legList[0].distance.text != null)
                        route.legList[0].distance.text
                    else
                        "0.0 km"
                    travelTime = if (route.legList[0].arrivalTime != null) {
                        if (route.legList[0].arrivalTime.text != null)
                            route.legList[0].arrivalTime.text
                        else
                            "0 minute"
                    } else
                        "0 minute"
                }
            }
        }
        val subScript: String = when (pickupCount) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
        textViewRouteName.text = "Pick up ${pickupCount}$subScript " + "Student"
        textViewDistance.text =
            "Your next student is  $travelDistance away."
        textViewTime.visibility = View.GONE
    }

    private fun getWayPointsList(mapListData: ArrayList<StopLocationModel>): ArrayList<LatLng> {
        val tempArray = ArrayList<LatLng>()
        var tempLatLng: LatLng
        for (index in 0 until mapListData.size) {
            tempLatLng = LatLng(
                mapListData[index].latitude,
                mapListData[index].longitude
            )
            tempArray.add(tempLatLng)
        }
        return tempArray
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
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
    private fun getMyLocation(isLastLocation: Boolean) {
        if (!(checkSelfPermission(
                Objects.requireNonNull(this@TrackMapActivity),
                ACCESS_FINE_LOCATION
            ) === PERMISSION_GRANTED || checkSelfPermission(
                this@TrackMapActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) === PERMISSION_GRANTED)
        ) {
            return
        }
        if (isLastLocation) {
            getLastLocation()
            animateCameraOnMap()
        } else animateCameraOnMap()

    }

    private fun getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        val locationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            return
        }
        locationClient.lastLocation
            .addOnSuccessListener { location -> // GPS location can be null if GPS is switched off
                location?.let { onLocationChanged(it) }
            }
            .addOnFailureListener { e ->
                Timber.d("Error trying to get last GPS location")
                e.printStackTrace()
            }
    }

    override fun onLocationChanged(it: Location) {
        mLastLocation = it
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
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = UPDATE_INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> getMyLocation(true)
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    try {
                        status.startResolutionForResult(this, requestCheckSetting)

                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
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


    }

    private fun getDataForPlotOnMap() {
        val url: String = ApiEndPoint.MAP_TRACK + tripId
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(this).getApi()
            .getTrackMapData(url, mSharedPreferences.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, this)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<GetMapTrackResponse> {
                    override fun onComplete() {
                        AppLogger.e(TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: GetMapTrackResponse) {
                        AppLogger.e(TAG + Gson().toJson(mResponse))
                        hideLoading()
                        if (mResponse.getData()?.data?.size!! > 0) {
                            if (mapListData.size > 0)
                                mapListData.clear()
                            mapListData = mResponse.getData()?.data!!
                            showHideRecycler(false, true)
                        } else {
                            showHideRecycler(true, false)

                        }
                        isTripStarted =
                            mResponse.getData()!!.tripStatus!![0].status == "TRIP_STARTED"
                        showHideViews()
                        bindDataAndPlotIntoMap(mapListData)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(this@TrackMapActivity, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun showHideRecycler(
        noDataPick: Boolean,
        pickRecycler: Boolean
    ) {
        if (isSupervisor == 1) {
            noDataFoundLayoutPick.isVisible = noDataPick
            mRecyclerPickStudent.isVisible = pickRecycler
        } else {
            noDataFoundLayoutParent.isVisible = noDataPick
            mRecyclerPickStop.isVisible = pickRecycler
        }
    }

    private fun onDirectionSuccess(direction: Direction) {
        if (direction.isOK) {
            val route = direction.routeList[0]
            val legCount = route.legList.size
            if (legCount > 1) {
                Log.d("Location leg", legCount.toString())
                for (index in 0 until legCount) {
                    val leg = route.legList[index]
                    when (index) {
                        0 -> {
                            Timber.d(index.toString())
                            mGoogleMap.addMarker(
                                MarkerOptions().position(leg.endLocation.coordination).icon(
                                    BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_home))
                                )
                            )
                        }
                        legCount - 1 -> {
                            mGoogleMap.addMarker(
                                MarkerOptions().position(leg.endLocation.coordination).icon(
                                    BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_map_end_loc))
                                )
                            )
                        }
                        else -> {
                            mGoogleMap.addMarker(
                                MarkerOptions().position(leg.startLocation.coordination).icon(
                                    BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_pin))
                                )
                            )
                        }
                    }
                    val stepList = leg.stepList
                    val pattern: List<PatternItem> = listOf(
                        Dot(), Gap(1F), Dash(20F), Gap(10F)
                    )
                    val polylineOptionList =
                        DirectionConverter.createTransitPolyline(
                            this,
                            stepList,
                            2,
                            ContextCompat.getColor(this, R.color.colorText),
                            1,
                            Color.BLUE
                        )
                    for (polylineOption in polylineOptionList) {
                        mGoogleMap.addPolyline(polylineOption.pattern(pattern))
                    }
                }
                notifyAdapterOnUpdate()
                setCameraWithCoordinationBounds(route)
            }
        }
    }

    private fun onDirectionFailure(t: Throwable) {
    }

    private fun notifyAdapterOnUpdate() {
        if (isSupervisor == 1) {
            mAdapter?.setList(mapListData, isTripStarted, isSupervisor)
            mAdapter?.notifyDataSetChanged()
        } else {
            mAdapterParent?.setList(mapListData, isTripStarted, isSupervisor)
            mAdapterParent?.notifyDataSetChanged()
        }
        AppLogger.e("$TAG notify")
//        upcomingDataUiUpdate()
    }

    private fun bindDataAndPlotIntoMap(mapListData: ArrayList<GetMapTrackResponse.Data.Datum>) {
        if (mapListData.size > 0) {
            if (mapStopPoint.size > 0)
                mapStopPoint.clear()
            val mapStopPlot1 = StopLocationModel()
            for (index in 0 until mapListData.size) {
                mapStopPlot1.latitude = mapListData[index].stopLocation!!.getLatitude()
                mapStopPlot1.longitude = mapListData[index].stopLocation!!.getLongitude()
                mapStopPoint.add(mapStopPlot1)
            }
            requestDirection()
        }
    }

    private fun checkForJoinTrips() {
        mSocket?.on("connect") {
            Timber.e("Connected!!")
            mSocket?.off("connect")
            val requestBody = JsonRequestBody().socketJoinTripRequest(tripId)
            Timber.e("Connected!!")
            mSocket?.emit(ApiEndPoint.joinTripUpdates, requestBody)
            getTripDataOnStatusChanged()
        }
    }

    private fun getTripDataOnStatusChanged() {
        if (mSocket!!.connected()) {
            mSocket?.on(ApiEndPoint.tripStatusChanged) {
                Timber.d("Call for status changed")
                parseDataAndPlotOnList(it[0])
            }
        }
    }

    private fun parseDataAndPlotOnList(any: Any) {
        runOnUiThread {
            Timber.d(Gson().toJson(any))
            try {
                val tempParent = JSONObject(any.toString())
                val parentDataObject = tempParent.getJSONObject("data")
                val tripStatusArray = parentDataObject.getJSONArray("tripStatus")
                val tripData = parentDataObject.getJSONArray("data")
                val tripListData = ArrayList<GetMapTrackResponse.Data.Datum>()
                val tripStatusListData = ArrayList<GetMapTrackResponse.Tripstatus>()
                if (tripData.length() > 0) {
                    if (tripListData.size > 0)
                        tripListData.clear()
                    for (pos in 0 until tripData.length()) {
                        val location = GetMapTrackResponse.Data.Datum().LocationData()
                        val tripDataChild = GetMapTrackResponse.Data.Datum()
                        val jsonRouteObject = tripData.getJSONObject(pos)
                        tripDataChild.id = jsonRouteObject.getInt("id")
                        tripDataChild.studentId = jsonRouteObject.getInt("studentId")
                        tripDataChild.tripId = jsonRouteObject.getInt("tripId")
                        tripDataChild.isActive = jsonRouteObject.getInt("isActive")
                        tripDataChild.createdOn = jsonRouteObject.getString("createdOn")
                        tripDataChild.modifiedOn = jsonRouteObject.getString("modifiedOn")
                        tripDataChild.stopId = jsonRouteObject.getInt("stopId")
                        tripDataChild.pickupCount = jsonRouteObject.getInt("pickupCount")
                        tripDataChild.studentName = jsonRouteObject.getString("studentName")
                        tripDataChild.studentGrade = jsonRouteObject.getString("studentGrade")
                        tripDataChild.contact = jsonRouteObject.getString("contact")
                        tripDataChild.parentName = jsonRouteObject.getString("parentName")
                        tripDataChild.stopName = jsonRouteObject.getString("stopName")
                        tripDataChild.status = jsonRouteObject.getString("status")
                        val stopLocation = jsonRouteObject.getJSONObject("stopLocation")
                        location.setLatitude(stopLocation.getDouble("1"))
                        location.setLongitude(stopLocation.getDouble("0"))
                        tripDataChild.stopLocation = location
                        tripListData.add(tripDataChild)
                    }
                }
                if (tripStatusArray.length() > 0) {
                    val tripStatusChild = GetMapTrackResponse.Tripstatus()
                    for (pos in 0 until tripStatusArray.length()) {
                        val jsonRouteObject = tripStatusArray.getJSONObject(pos)
                        if (tripStatusListData.size > 0)
                            tripStatusListData.clear()
                        tripStatusChild.id = jsonRouteObject.getInt("id")
                        tripStatusChild.status = jsonRouteObject.getString("status")
                        try {
                            tripStatusChild.longitude =
                                jsonRouteObject.getDouble("longitude").toString()
                            tripStatusChild.latitude =
                                jsonRouteObject.getDouble("latitude").toString()
                        } catch (e: java.lang.Exception) {
                            tripStatusChild.longitude = "0.0"
                            tripStatusChild.latitude = "0.0"
                        }
                        tripStatusListData.add(tripStatusChild)
                    }
                }
                AppLogger.e("Location update ${Gson().toJson(tripListData)}")
                manageAndUpdateTripListData(tripListData, tripStatusListData)
            } catch (e: Exception) {
            }
        }
    }

    private fun manageAndUpdateTripListData(
        tripListData: ArrayList<GetMapTrackResponse.Data.Datum>,
        tripStatusListData: ArrayList<GetMapTrackResponse.Tripstatus>
    ) {
        AppLogger.e("tripListData Size >> ${tripListData.size}")
        AppLogger.e("mapListData Size >> ${mapListData.size}")
        for (index in 0 until tripListData.size) {
            val id = tripListData[index].id ?: 0
            val status = tripListData[index].status ?: "NOT_SET"
            try {
                if (mapListData[index].id == id && id != 0) {
                    if (status == PickStatus.NOT_PICKED.toString()) {
                        if (mapListData[index].stopLocation?.getLatitude() != mLastLocation?.latitude &&
                            mapListData[index].stopLocation?.getLongitude() != mLastLocation?.longitude
                        ) {
                            val stopLocation = tripListData[index].stopLocation
                            val latitude = stopLocation?.getLatitude() ?: 0.0
                            val longitude = stopLocation?.getLongitude() ?: 0.0
                            if (latitude != 0.0)
                                getDistanceAndTimeFromGoogleMatrixApi(
                                    index,
                                    latitude,
                                    longitude,
                                    tripStatusListData
                                )
                        }

                    } else if (mapListData[index].status != PickStatus.PICKED.toString()) {
                        mapListData[index].status = status
                        mapListData[index].isTripStarted =
                            tripStatusListData[0].status == "TRIP_STARTED"
                        if (isSupervisor == 1)
                            mAdapter?.notifyItemChanged(index)
                        else
                            mAdapterParent?.notifyItemChanged(index)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getDistanceAndTimeFromGoogleMatrixApi(
        index: Int,
        upcomingStudentLat: Double,
        upcomingStudentLong: Double,
        tripStatusListData: ArrayList<GetMapTrackResponse.Tripstatus>
    ) {
        val latitude = mLastLocation?.latitude ?: 0.0
        val longitude = mLastLocation?.longitude ?: 0.0

        if (latitude == 0.0) {
            return
        }

        val start = LatLng(latitude, longitude)
        val end = LatLng(upcomingStudentLat, upcomingStudentLong)
        GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
        GoogleDirection.withServerKey(serverKey)
            .from(start)
            .to(end)
            .transportMode(TransportMode.DRIVING)
            .execute(
                onDirectionSuccess = { direction ->
                    var travelDistance = ""
                    var travelTime = ""
                    if (direction.isOK) {
                        if (direction.routeList[0] != null) {
                            val route = direction.routeList[0]
                            val legCount = route.legList.size
                            if (legCount > 0) {
                                if (route.legList[0].distance != null)
                                    travelDistance = route.legList[0].distance.text
                                if (route.legList[0].arrivalTime != null)
                                    travelTime = route.legList[0].arrivalTime.text
                            }
                        }
                    }

                    val indexData = mapListData[index]
                    indexData.travelDistance = travelDistance
                    indexData.travelTime = travelTime
                    indexData.isTripStarted = tripStatusListData[0].status == "TRIP_STARTED"
                    if (isSupervisor == 1)
                        mAdapter?.notifyItemChanged(index)
                    else
                        mAdapterParent?.notifyItemChanged(index)

                },
                onDirectionFailure = { t -> onDirectionFailure(t) }
            )
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
        Handler().postDelayed({
            if (mLastLocation != null) {
                mGoogleMap.clear()
                val marker = MarkerOptions().position(
                    LatLng(
                        mLastLocation?.latitude!!,
                        mLastLocation?.longitude!!
                    )
                )
                val mapMarker = mGoogleMap.addMarker(
                    marker.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_pin)))
                )
                mGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            mLastLocation?.latitude!!,
                            mLastLocation?.longitude!!
                        ), 14f
                    )
                )
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null)
                animateMarker(mLastLocation!!, mapMarker)
            }

        }, 1000)
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

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private fun animateMarker(destination: Location, marker: Marker?) {
        if (marker != null) {
            val startPosition = marker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            val startRotation = marker.rotation
            val latLngInterpolator: LatLngInterpolator = LatLngInterpolator.LinearFixed()
            val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.duration = 1000 // duration 1 second
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v: Float = animation.animatedFraction
                    val newPosition: LatLng =
                        latLngInterpolator.interpolate(v, startPosition, endPosition)
                    marker.position = newPosition
                    marker.rotation = computeRotation(
                        v,
                        startRotation,
                        destination.bearing
                    )
                } catch (ex: Exception) {
                }
            }
            valueAnimator.start()
        }
    }

    private interface LatLngInterpolator {
        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng
        class LinearFixed : LatLngInterpolator {
            override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
                val lat = (b.latitude - a.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= sign(lngDelta) * 360
                }
                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }
        }
    }

    private fun computeRotation(
        fraction: Float,
        start: Float,
        end: Float
    ): Float {
        val normalizeEnd = end - start // rotate start to 0
        val normalizedEndAbs = (normalizeEnd + 360) % 360
        val direction =
            if (normalizedEndAbs > 180) (-1).toFloat() else 1.toFloat() // -1 = anticlockwise, 1 = clockwise
        val rotation: Float
        rotation = (if (direction > 0) normalizedEndAbs else normalizedEndAbs - 360)
        val result = fraction * rotation + start
        return (result + 360) % 360
    }

    private fun callApiForEndTrip() {
        val url: String = ApiEndPoint.END_TRIP + tripId
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(this).getApi()
            .putTripEnd(url, mSharedPreferences.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, this)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonObjectResponse> {
                    override fun onComplete() {
                        AppLogger.e(TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: CommonObjectResponse) {
                        AppLogger.e(TAG + Gson().toJson(mResponse))
                        val dialog = Utils.forgotSuccessAlertDialog(
                            this@TrackMapActivity,
                            mResponse.getMessage().toString()/*"Successfully completed your trip."*/
                        )
                        dialog.btnOk.setOnClickListener {
                            startActivity(
                                Intent(this@TrackMapActivity, GroupDetailsActivity::class.java)
                                    .putExtra("groupId", groupId.toString()).putExtra(
                                        "comeFrom",
                                        "groupNav"
                                    )
                            )
                        }
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(this@TrackMapActivity, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }
}