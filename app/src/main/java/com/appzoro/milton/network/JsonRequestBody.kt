package com.appzoro.milton.network

import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.utility.Constant
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.ArrayList

class JsonRequestBody {
    fun addStudentJsonRequest(
        mStudentName: String,
        mEmail: String,
        mSchoolName: String,
        mStudentGrade: String
    ): JsonObject {
        val jsonObjectParent = JsonObject()
        jsonObjectParent.addProperty("fullName", mStudentName)
        jsonObjectParent.addProperty("email", mEmail)
        jsonObjectParent.addProperty("schoolName", mSchoolName)
        jsonObjectParent.addProperty("image", Constant.emptyString)
        jsonObjectParent.addProperty("grade", mStudentGrade)
        return jsonObjectParent
    }
    fun deleteScheduleJsonRequest(
        mIsSuperVisor: Boolean,
        mStudentId: String,
        mTripId: String
    ): JsonObject {
        val jsonObjectParent = JsonObject()
        jsonObjectParent.addProperty("isSupervisor", mIsSuperVisor)
        jsonObjectParent.addProperty("tripId", mTripId)
        jsonObjectParent.addProperty("studentId", mStudentId)
        return jsonObjectParent
    }
    fun editScheduleJsonRequest(
        tripId: String,
        finalDate: String,
        dateChanged: Boolean,
        selectedStopId: String,
        superVisor: Boolean,
        studentId: String
    ): JsonObject {
        val jsonObjectParent = JsonObject()
        jsonObjectParent.addProperty("isSupervisor", superVisor)
        jsonObjectParent.addProperty("tripId", tripId)
        jsonObjectParent.addProperty("studentId", studentId)
        jsonObjectParent.addProperty("newDate", finalDate)
        jsonObjectParent.addProperty("isDateChanged", dateChanged)
        jsonObjectParent.addProperty("stopId", selectedStopId)
        return jsonObjectParent
    }

    fun joinGroupRequest(
        mGroupId: String,
        mStopId: String,
        mStudentsListId: ArrayList<SelectedStudentIdModel>
    ): JsonObject {
        val jsonObjectParent = JsonObject()
        jsonObjectParent.addProperty("groupId", mGroupId)
        jsonObjectParent.addProperty("stopId", mStopId)
        val mJsonArray = JsonArray()
        for (index in 0 until mStudentsListId.size)
            mJsonArray.add(mStudentsListId.get(index).selectedStudentId)

        jsonObjectParent.add("students", mJsonArray)
        return jsonObjectParent

    }
    fun createScheduleRequest(
        mGroupId: String,
        mStopId: String,
        mStudentsListId: ArrayList<SelectedStudentIdModel>,
        repetition: Boolean,
        repetitionCount: String,
        repeatDay: String,
        selectedDate: String,
        isSupervisor: Boolean
    ): JsonObject {
        val jsonObjectParent = JsonObject()
        jsonObjectParent.addProperty("groupId", mGroupId)
        jsonObjectParent.addProperty("stopId", mStopId)
        val mJsonArray = JsonArray()
        for (index in 0 until mStudentsListId.size)
            mJsonArray.add(mStudentsListId.get(index).selectedStudentId)

        jsonObjectParent.add("students", mJsonArray)
        jsonObjectParent.addProperty("repetition", repetition)
        jsonObjectParent.addProperty("repetitionCount", repetitionCount)
        jsonObjectParent.addProperty("repeatDay", repeatDay)
        jsonObjectParent.addProperty("selectedDate", selectedDate)
        jsonObjectParent.addProperty("isSupervisor", isSupervisor)
        return jsonObjectParent

    }

    fun socketUpdateLocationRequest(
        latitude: Double,
        longitude: Double,
        parentId: String,
        tripId: Int
    ): JSONObject {
        val jsonObjectParent = JSONObject()
        jsonObjectParent.put("parentId", parentId)
        jsonObjectParent.put("tripId", tripId)
        jsonObjectParent.put("longitude", longitude)
        jsonObjectParent.put("latitude", latitude)
        return jsonObjectParent

    }

    fun socketMarkPresentRequest(
        studentId: String,
        tripId: Int
    ): JSONObject {
        val jsonObjectParent = JSONObject()
        jsonObjectParent.put("studentId", studentId)
        jsonObjectParent.put("tripId", tripId)
        return jsonObjectParent

    }
fun socketJoinTripRequest(
        tripId: Int
    ): JSONObject {
        val jsonObjectParent = JSONObject()
        jsonObjectParent.put("tripId", tripId)
        return jsonObjectParent

    }
}