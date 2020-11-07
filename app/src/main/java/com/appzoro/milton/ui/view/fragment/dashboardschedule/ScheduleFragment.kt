package com.appzoro.milton.ui.view.fragment.dashboardschedule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.CommonStringTypeResponse
import com.appzoro.milton.model.GetScheduleResponse
import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.network.*
import com.appzoro.milton.ui.view.activity.group_details.GroupDetailsActivity
import com.appzoro.milton.ui.view.activity.onboarding.searchgroup.SearchGroupActivity
import com.appzoro.milton.ui.view.activity.selectstop.SelectStopActivity
import com.appzoro.milton.ui.view.calendar_view.RobotoCalendarListener
import com.appzoro.milton.utility.AlertDialogView
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.create_schedule_dialog.*
import kotlinx.android.synthetic.main.dashboard_fragment_header.view.*
import kotlinx.android.synthetic.main.edit_schedule_dialog.*
import kotlinx.android.synthetic.main.edit_schedule_dialog.buttonDone
import kotlinx.android.synthetic.main.edit_schedule_dialog.imageViewCross
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM = "param1"

class ScheduleFragment : BaseFragment(), RobotoCalendarListener, View.OnClickListener,
    OnClickScheduleInterface {

    private var finalDate: String = ""
    private var dialog: Dialog? = null
    private var mList: ArrayList<GetScheduleResponse.Datum>? = null
    private var param: String? = null
    private var mAdapter: ScheduleAdapter? = null
    private var mPreferences: PreferenceManager? = null
    private var mMonthlyDatumList: ArrayList<GetScheduleResponse.Datum>? = null
    private var mView: View? = null
    private var selectedDate: String = ""
    private var selectedDateInDateFormat: Date? = null
    private var selectedMonthDate: Date? = null
    private var mActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param = it.getString(ARG_PARAM)
        }
    }

    override fun setUp(view: View) {
        mView = view
        mActivity = activity
        mPreferences = PreferenceManager(activity!!)
        mAdapter = ScheduleAdapter(this)
        mList = ArrayList()
        view.mRecyclerView?.adapter = mAdapter
        AppLogger.e("param $param")
        selectedMonthDate = Calendar.getInstance(Locale.US).time
        selectedDate = Utils.getCurrentDate()
        view.tvDayName.text = Utils.getCurrentWeekName()
        view.textViewTitle.visibility=View.VISIBLE
        val isWeekendDate =
            Utils().getWeekendFromDate(selectedMonthDate!!)
        view.robotoCalendarPicker.markCurrentDate()
        view.robotoCalendarPicker.currentDate
        onVisibleMonthYear(selectedMonthDate)

        if (!isWeekendDate) {
            selectedDateInDateFormat = Calendar.getInstance(Locale.US).time
            callApiForGetScheduleFromSelectedDate(selectedDate)
        } else {
            view.mFloatingButton.isVisible = false
        }
        callApiForGetScheduleDates(selectedMonthDate!!, false)
        val mSwitch: SwitchCompat = view.mSwitchSupervisor
        mSwitch.setOnCheckedChangeListener { _, b ->
            mPreferences?.setBoolean(Constant.isSuperVisor, b)
            callApiForGetScheduleFromSelectedDate(selectedDate)
            callApiForGetScheduleDates(selectedMonthDate!!, false)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        view.robotoCalendarPicker.setRobotoCalendarListener(this)
        view.imageNotification.isVisible = false
        //view.imageNotification.setImageResource(R.drawable.ic_create_schedule)
        //click on view
        view.imageViewHamburger.setOnClickListener(this)
        view.imageNotification.setOnClickListener(this)
        //view.mFloatingButton.isVisible=false
        view.mFloatingButton.setOnClickListener {
            val isSupervisor: Boolean = mView?.mSwitchSupervisor?.isChecked ?: false
            mPreferences?.setBoolean(Constant.isSuperVisor, isSupervisor)
            mPreferences?.setString(Constant.selectedDate, selectedDate)
            val intent = Intent(activity!!, SearchGroupActivity::class.java)
            intent.putExtra("comeFrom", "schedule")
            startActivityForResult(intent, 1232)
        }
        return view
    }


    companion object {

        @JvmStatic
        fun newInstance(from: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM, param)
                }
            }

        var selectedStopId: String = ""
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewHamburger -> {
                Utils.drawerOpenClose(drawerLayout = requireActivity().drawer_layout)
            }

        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun showPopupMenu(
        view: View,
        pos: Int,
        item: GetScheduleResponse.Datum
    ) {
        val popUpMenu = PopupMenu(activity!!, view)
        popUpMenu.menuInflater.inflate(R.menu.schedule_pop_up_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.edit_schedule) -> editRecordItem(item, pos)
                getString(R.string.delete_schedule) -> showConfirmAlert(item, pos)
                else -> {
                    AppLogger.e("showPopupMenu in data >>> ${Gson().toJson(item)}")
                    startActivity(
                        Intent(activity!!, GroupDetailsActivity::class.java)
                            .putExtra("groupId", (item.groupId ?: 0).toString())
                            .putExtra("comeFrom", "group")
                    )
                }
            }
            return@setOnMenuItemClickListener true
        }
        popUpMenu.show()
    }

    private fun showConfirmAlert(
        item: GetScheduleResponse.Datum,
        pos: Int
    ) {
        val logoutDialog = AlertDialogView.showDialogWithTwoButton(
            activity!!,
            R.drawable.ic_signin_logo,
            getString(R.string.delete_msg),
            getString(R.string.yes),
            getString(R.string.no)
        )
        logoutDialog.buttonNo.setOnClickListener {
            logoutDialog.dismiss()

        }
        logoutDialog.buttonYes.setOnClickListener {
            logoutDialog.dismiss()
            deleteRecordItem(item, pos)
        }

    }

    override fun onResume() {
        super.onResume()
        if (dialog != null) {
            dialog?.textViewDefaultStopValue?.text =
                mPreferences?.getString(Constant.selectedStopNameTemp)
            selectedStopId = mPreferences?.getString(Constant.selectedStopIdTemp).toString()
        }
    }



    private fun editRecordItem(item: GetScheduleResponse.Datum, pos: Int) {
        dialog =
            AlertDialogView.showCreateScheduleDialog(activity!!, R.layout.edit_schedule_dialog)
        dialog?.datePicker?.minDate = System.currentTimeMillis() - 1000
        //
        if (mPreferences!!.getBoolean(Constant.isSuperVisor)) {
            dialog?.textViewDefaultStopTitle?.visibility = View.GONE
            dialog?.textViewDefaultStopValue?.visibility = View.GONE
        } else {
            dialog?.textViewDefaultStopTitle?.visibility = View.VISIBLE
            dialog?.textViewDefaultStopValue?.visibility = View.VISIBLE
        }
        val year: Int = selectedDate.substring(0, 4).toInt()
        val month = selectedDate.substring(5, 7).toInt()
        val date = selectedDate.substring(8, 10).toInt()
        dialog?.datePicker?.updateDate(year, month - 1, date)
        dialog?.textViewDefaultStopValue?.text = item.stopName
        selectedStopId = item.stopId.toString()
        finalDate = selectedDate
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            dialog?.datePicker?.setOnDateChangedListener { datePicker, year, month, dayOfMonth ->
                run {
                    val selectedYear: String = year.toString()
                    var selectedMonth = ""
                    when (month) {
                        0 -> selectedMonth = "01"
                        1 -> selectedMonth = "02"
                        2 -> selectedMonth = "03"
                        3 -> selectedMonth = "04"
                        4 -> selectedMonth = "05"
                        5 -> selectedMonth = "06"
                        6 -> selectedMonth = "07"
                        7 -> selectedMonth = "08"
                        8 -> selectedMonth = "09"
                        9 -> selectedMonth = "10"
                        10 -> selectedMonth = "11"
                        11 -> selectedMonth = "12"
                    }

                    val date: String = if (dayOfMonth.toString().length == 1)
                        "0$dayOfMonth"
                    else
                        dayOfMonth.toString()
                    finalDate = "$selectedYear-$selectedMonth-$date"

                }
            }

        }

//        dialog?.datePicker?.setOnDateChangedListener { datePicker, year, month, dayOfMonth ->
//            run {
//                val selectedYear: String = year.toString()
//                var selectedMonth = ""
//                when (month) {
//                    0 -> selectedMonth = "01"
//                    1 -> selectedMonth = "02"
//                    2 -> selectedMonth = "03"
//                    3 -> selectedMonth = "04"
//                    4 -> selectedMonth = "05"
//                    5 -> selectedMonth = "06"
//                    6 -> selectedMonth = "07"
//                    7 -> selectedMonth = "08"
//                    8 -> selectedMonth = "09"
//                    9 -> selectedMonth = "10"
//                    10 -> selectedMonth = "11"
//                    11 -> selectedMonth = "12"
//                }
//
//                val date: String = if (dayOfMonth.toString().length == 1)
//                    "0$dayOfMonth"
//                else
//                    dayOfMonth.toString()
//                finalDate = "$selectedYear-$selectedMonth-$date"
//
//            }
//        }
        dialog?.imageViewCross?.setOnClickListener {
            showConfirmCancelAlert(dialog!!)
        }
        dialog?.textViewDefaultStopValue?.setOnClickListener {
            if (dialog?.textViewDefaultStopValue?.text.isNullOrEmpty()) {
                Utils.alertDialog(activity!!, "You don't have stop name")
            } else {
                val intent = Intent(activity, SelectStopActivity::class.java)
                intent.putExtra("comeFrom", "edit")
                intent.putExtra("stopName", item.stopName)
                intent.putExtra("stopId", item.stopId)
                intent.putExtra("routeId", (item.routeId ?: 0).toString())
                selectedStopId = item.stopId ?: ""
                mPreferences!!.setString(Constant.selectedStopNameTemp, item.stopName!!)
                mPreferences!!.setString(
                    Constant.selectedStopIdTemp,
                    selectedStopId
                )
                startActivity(intent)
            }
        }

        dialog?.buttonDone?.setOnClickListener {
            val isDateChanged: Boolean = finalDate != selectedDate
            val isWeekDay: Boolean =
                Utils.getWeekendNameFromDate(Utils.convertDateFromString(finalDate)!!)
            if (isWeekDay) {
                dialog?.textViewDaySpecificError?.isVisible = true
            } else {
                dialog?.textViewDaySpecificError?.isVisible = false
                if (mPreferences!!.getBoolean(Constant.isSuperVisor)) {
                    callApiForEditSchedule(
                        item.tripId!!,
                        finalDate,
                        isDateChanged,
                        "0",
                        mPreferences?.getBoolean(Constant.isSuperVisor) ?: false,
                        "0"
                    )

                } else {
                    when {
                        dialog?.textViewDefaultStopValue?.text.isNullOrEmpty() -> {
                            Utils.alertDialog(activity!!, "You don't have stop name")
                        }
                        item.studentId == null -> {
                            Utils.alertDialog(activity!!, "You don't have any student id.")
                        }
                        else -> {
                            callApiForEditSchedule(
                                item.tripId!!,
                                finalDate,
                                isDateChanged,
                                selectedStopId,
                                mPreferences?.getBoolean(Constant.isSuperVisor) ?: false,
                                item.studentId
                            )
                        }
                    }
                }
            }
        }

    }

    private fun showConfirmCancelAlert(dialog: Dialog) {
        val logoutDialog = AlertDialogView.showDialogWithTwoButton(
            activity!!,
            R.drawable.ic_signin_logo,
             getString(R.string.cancel_msg),
            getString(R.string.yes),
            getString(R.string.no)
        )
        logoutDialog.buttonNo.setOnClickListener {
            logoutDialog.dismiss()

        }
        logoutDialog.buttonYes.setOnClickListener {
            logoutDialog.dismiss()
            dialog.dismiss()
        }
    }

    private fun callApiForEditSchedule(
        tripId: String,
        finalDate: String,
        dateChanged: Boolean,
        selectedStopId: String,
        isSuperVisor: Boolean,
        studentId: String
    ) {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(activity!!).getApi().editSchedule(
            JsonRequestBody().editScheduleJsonRequest(
                tripId,
                finalDate,
                dateChanged,
                selectedStopId,
                isSuperVisor,
                studentId
            ),
            mPreferences?.getString(Constant.authToken).toString()
        ).doOnError {
            temError = UtilThrowable.mCheckThrowable(it, activity!!)
        }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonObjectResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: CommonObjectResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        dialog?.dismiss()
                        callApiForGetScheduleFromSelectedDate(selectedDate)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }
                })
    }

    private fun deleteRecordItem(
        item: GetScheduleResponse.Datum,
        pos: Int
    ) {
        val studentId: String
        val tripId: String
        if (mPreferences!!.getBoolean(Constant.isSuperVisor)) {
            studentId = "0"
            tripId = item.tripId!!
        } else {
            studentId = item.studentId!!
            tripId = item.tripId!!
        }
        callApiForDeleteRecord(
            studentId,
            tripId,
            mPreferences!!.getBoolean(Constant.isSuperVisor),
            pos
        )
    }

    private fun callApiForDeleteRecord(
        studentId: String,
        tripId: String,
        isSuperVisor: Boolean,
        pos: Int
    ) {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(activity!!).getApi().deleteSchedule(
            JsonRequestBody().deleteScheduleJsonRequest(isSuperVisor, studentId, tripId),
            mPreferences?.getString(Constant.authToken).toString()
        ).doOnError {
            temError = UtilThrowable.mCheckThrowable(it, activity!!)
        }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonObjectResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: CommonObjectResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        mList?.removeAt(pos)
                        mAdapter?.notifyItemRemoved(pos)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }
                })
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onItemClickItem(view: View, pos: Int, item: GetScheduleResponse.Datum) {
        val isSupervisor: Boolean = mView?.mSwitchSupervisor?.isChecked ?: false
        mPreferences?.setBoolean(Constant.isSuperVisor, isSupervisor)
        AppLogger.e("onItemClickItem >> ${Gson().toJson(item)}")
        mPreferences?.setString(Constant.selectedDate, selectedDate)
        showPopupMenu(view, pos, item)
    }

    override fun onDayClick(date: Date, isPreviousDate: Boolean) {
        AppLogger.e("onDayClick $date")
        if (isPreviousDate) {
            mView?.mFloatingButton?.visibility = View.GONE
        } else {
            mView?.mFloatingButton?.visibility = View.VISIBLE
        }

        selectedDateInDateFormat = date
        selectedDate = Utils.getDateInString(date)
        mPreferences?.setString(Constant.selectedDate, selectedDate)
        mView?.tvDayName?.text = Utils.getWeekName(date)
        markDates(isClear = false, isMonthChanged = false, data = mMonthlyDatumList)
        callApiForGetScheduleFromSelectedDate(selectedDate)
    }

    override fun onVisibleMonthYear(date: Date?) {
//        mView?.tvYear?.text = Utils.getYearFromDate(date)
        mView?.textViewTitle?.text = Utils.getYearFromDate(date)
    }

    override fun onRightButtonClick(date: Date) {
        mView?.mFloatingButton?.visibility = View.GONE
        selectedDateInDateFormat = null
        selectedMonthDate = date
        val dateString = Utils.getMonthYearFromDate(date)
        AppLogger.e("dateString  $dateString")
        callApiForGetScheduleDates(date, true)
    }

    override fun onLeftButtonClick(date: Date) {
        mView?.mFloatingButton?.visibility = View.GONE

        selectedMonthDate = date
        selectedDateInDateFormat = null
        val dateString = Utils.getMonthYearFromDate(date)
        AppLogger.e("dateString  $dateString")
        callApiForGetScheduleDates(date, true)
    }

    private fun callApiForGetScheduleFromSelectedDate(date: String) {
        val isSupervisor: Boolean = mView?.mSwitchSupervisor?.isChecked ?: false
        val url: String = ApiEndPoint.SCHEDULE_TIME + date + "/" + isSupervisor
        AppLogger.e("Api url 2$url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(activity!!).getApi()
            .getSchedule(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, activity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<GetScheduleResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: GetScheduleResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))

                        if (mListResponse.data != null) {
                            mList = mListResponse.data
                            mAdapter?.setList(isSupervisor, mListResponse.data!!)
                            if (mListResponse.data?.size ?: 0 > 0) {
                                view?.tvNoSchedule?.visibility = View.GONE
                            } else {
                                view?.tvNoSchedule?.visibility = View.VISIBLE
                            }
                        } else {
                            val mList: ArrayList<GetScheduleResponse.Datum> = ArrayList()
                            mAdapter?.setList(isSupervisor, mList)
                            mAdapter?.setList(isSupervisor, ArrayList())
                            view?.tvNoSchedule?.visibility = View.VISIBLE
                        }
                        mAdapter?.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        mAdapter?.setList(isSupervisor, ArrayList())
                        view?.tvNoSchedule?.visibility = View.VISIBLE
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun callApiForGetScheduleDates(date: Date, isMonthChanged: Boolean) {
        val dateString = Utils.getMonthYearFromDate(date)
        val isSupervisor: Boolean = mView?.mSwitchSupervisor?.isChecked ?: false
        val url: String = ApiEndPoint.GET_SCHEDULE_DATES + dateString + isSupervisor
        AppLogger.e("Api url 1 $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(activity!!).getApi()
            .getScheduleDates(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, activity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<GetScheduleResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: GetScheduleResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        markDates(true, isMonthChanged, mListResponse.data)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun markDates(
        isClear: Boolean,
        isMonthChanged: Boolean,
        data: ArrayList<GetScheduleResponse.Datum>?
    ) {

        AppLogger.e("Month in mark Dates Size ${data?.size ?: 0}")

        if (!isMonthChanged) {
            if (mMonthlyDatumList != null) {
                // Extract the day from the text
                for (item in mMonthlyDatumList!!) {
//                AppLogger.e("Date ${item.dueOn}")
                    val date = Utils.getCalendarFromServerTime(item.dueOn!!)
                    if (date != null) {
                        view?.robotoCalendarPicker?.clearDateColor(date)
                    }
                }
            }
        }
        if (isClear) {
            mMonthlyDatumList?.clear()
        }

        if (data != null) {
            // Extract the day from the text
            for (itemData in data.withIndex()) {
//                AppLogger.e("Date ${item.dueOn}")

                val item = itemData.value
                val date = Utils.getCalendarFromServerTime(item.dueOn!!)

                if (date != null && selectedMonthDate != null) {
                    AppLogger.e("Selected Date $date")
                    val isSameMonth = Utils.compareMonth(date, selectedMonthDate!!)
                    AppLogger.e("Selected Date isSameMonth $isSameMonth")
                    if (isSameMonth) {
                        view?.robotoCalendarPicker?.updateDateColor(date)
                    }
                }
            }
        }

        if (data != null) {
//            AppLogger.e("mMonthlyDataList " + Gson().toJson(data))
            mMonthlyDatumList = data
        }

        if (selectedDateInDateFormat != null) {
            mView?.robotoCalendarPicker?.markDayAsSelectedDay(selectedDateInDateFormat!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPreferences!!.setString(Constant.selectedStopNameTemp, "")
        mPreferences!!.setString(
            Constant.selectedStopIdTemp,
            ""
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogger.e("Fragment ScheduleFragment requestCode  $requestCode resultCode $resultCode")
        if (requestCode == 1232 && resultCode == Activity.RESULT_OK) {

            val groupId = data?.getStringExtra("groupId") ?: ""
            val stopId = data?.getStringExtra("stopId") ?: ""
            val selectedStudentId: ArrayList<SelectedStudentIdModel> =
                data?.getIntegerArrayListExtra("selectedStudentId") as ArrayList<SelectedStudentIdModel>

            AppLogger.e("groupId $groupId")
            AppLogger.e("stopId $stopId")
            AppLogger.e("selectedStudentId $selectedStudentId")

            showDialogForCreateSchedule(mActivity!!, groupId, stopId, selectedStudentId)

        }
    }

    private fun showDialogForCreateSchedule(
        mActivity: Activity,
        groupId: String,
        stopId: String,
        selectedStudentId: ArrayList<SelectedStudentIdModel>
    ) {
        var repeatedDay = ""
        var repeatSelectedValue: String

        val alertDialog =
            AlertDialogView.showCreateScheduleDialog(mActivity, R.layout.create_schedule_dialog)
        alertDialog.textViewMon.setOnClickListener {
            Utils.showSelectedView(
                alertDialog.textViewMon, alertDialog.textViewTue, alertDialog.textViewWed,
                alertDialog.textViewThu, alertDialog.textViewFri, 1, mActivity
            )
            alertDialog.linearLayoutParent.setBackgroundResource(R.drawable.day_out_line)
            repeatedDay = "Monday"
        }
        alertDialog.textViewTue.setOnClickListener {

            Utils.showSelectedView(
                alertDialog.textViewMon, alertDialog.textViewTue, alertDialog.textViewWed,
                alertDialog.textViewThu, alertDialog.textViewFri, 2, mActivity
            )
            alertDialog.linearLayoutParent.setBackgroundResource(R.drawable.day_out_line)
            repeatedDay = "Tuesday"
        }
        alertDialog.textViewWed.setOnClickListener {
            Utils.showSelectedView(
                alertDialog.textViewMon, alertDialog.textViewTue, alertDialog.textViewWed,
                alertDialog.textViewThu, alertDialog.textViewFri, 3, mActivity
            )
            alertDialog.linearLayoutParent.setBackgroundResource(R.drawable.day_out_line)
            repeatedDay = "Wednesday"
        }
        alertDialog.textViewThu.setOnClickListener {
            Utils.showSelectedView(
                alertDialog.textViewMon, alertDialog.textViewTue, alertDialog.textViewWed,
                alertDialog.textViewThu, alertDialog.textViewFri, 4, mActivity
            )
            alertDialog.linearLayoutParent.setBackgroundResource(R.drawable.day_out_line)
            repeatedDay = "Thursday"
        }
        alertDialog.textViewFri.setOnClickListener {
            Utils.showSelectedView(
                alertDialog.textViewMon, alertDialog.textViewTue, alertDialog.textViewWed,
                alertDialog.textViewThu, alertDialog.textViewFri, 5, mActivity
            )
            alertDialog.linearLayoutParent.setBackgroundResource(R.drawable.day_out_line)
            repeatedDay = "Friday"

        }
        alertDialog.imageViewCross.setOnClickListener {
            showConfirmCancelAlert(alertDialog)
//            alertDialog.dismiss()
        }
        alertDialog.spinnerRepeat.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                repeatSelectedValue = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        repeatSelectedValue = alertDialog.spinnerRepeat.selectedItem.toString()
        alertDialog.buttonDone.setOnClickListener {
//            if (repeatedDay == "") {
            if (repeatedDay == "" && alertDialog.spinnerRepeat.selectedItemPosition > 0) {
                Utils.alertDialog(mActivity, "Please select repeat day")
            } else {

                val isRepetition: Boolean = repeatSelectedValue != "Never"
                val json = JsonRequestBody().createScheduleRequest(
                    groupId, stopId, selectedStudentId, isRepetition,
                    repeatSelectedValue, repeatedDay, selectedDate,
                    (view?.mSwitchSupervisor?.isChecked ?: false)
                )
                callApiForCreateSchedule(alertDialog, json)
            }
        }

    }

    private fun callApiForCreateSchedule(alertDialog: Dialog, json: JsonObject) {

        //code for create schedule
        showLoading()
        AppLogger.e("createScheduleRequest json $json")

        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .createSchedule(
                json, (mPreferences?.getString(Constant.authToken) ?: "")
                    .toString()
            )
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonStringTypeResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: CommonStringTypeResponse) {
                        hideLoading()
                        alertDialog.dismiss()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        showDialogForShowScheduledTrip(mListResponse)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }
                })
    }

    private fun showDialogForShowScheduledTrip(mListResponse: CommonStringTypeResponse) {
        val dialogs = AlertDialogView.showScheduleTripsDialog(mActivity!!, R.layout.show_schedule_trip_status)
        val superviseCountView =
            dialogs.findViewById(R.id.textViewSupervising) as MaterialTextView

        val buttonOkay = dialogs.findViewById(R.id.buttonOkay) as MaterialButton
        superviseCountView.text = mListResponse.message

        buttonOkay.setOnClickListener {
            dialogs.dismiss()
            updatePageData()
        }
    }

    private fun updatePageData() {
        val data = GetScheduleResponse.Datum()
        data.dueOn = Utils.getServerTime(selectedDate)
        if (mMonthlyDatumList == null) {
            mMonthlyDatumList = ArrayList()
        }
        mMonthlyDatumList?.add(data)
        markDates(isClear = false, isMonthChanged = false, data = mMonthlyDatumList)
        callApiForGetScheduleFromSelectedDate(selectedDate)
    }

}