package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class HomePageResponse : Serializable {

    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data : Serializable {

        @SerializedName("studentTrips")
        @Expose
        var studentTrips: ArrayList<StudentTrips>? = null

        @SerializedName("supervisorTrips")
        @Expose
        var supervisorTrips: ArrayList<StudentTrips>? = null

        @SerializedName("parentData")
        @Expose
        var parentData: ParentData? = null

        @SerializedName("tripsWalked")
        @Expose
        var tripsWalked: Int? = null

        class StudentTrips : Serializable {

//            "studentId": 75,
//            "tripId": 86,
//            "stopId": 181,
//            "groupId": 1,
//            "isSupervised": 1,
//            "supervisorId": "39d4044f-7e89-4893-bd7b-089fad730746",
//            "status": "TRIP_NOT_STARTED",
//            "displayTime": "8:00 am",
//            "dueOn": "2020-07-22T18:30:00.000Z",
//            "groupName": "Group R3",
//            "supervisorName": "Rohan Updated",
//            "studentName": "James Mathew"

            @SerializedName("studentId")
            @Expose
            val studentId: String = ""

            @SerializedName("tripId")
            @Expose
            val tripId: String = ""

            @SerializedName("stopId")
            @Expose
            val stopId: String = ""

            @SerializedName("groupId")
            @Expose
            val groupId: String = ""

            @SerializedName("isSupervised")
            @Expose
            val isSupervised: String = ""

            @SerializedName("supervisorId")
            @Expose
            val supervisorId: String = ""

            @SerializedName("status")
            @Expose
            val status: String = ""

            @SerializedName("displayTime")
            @Expose
            val displayTime: String = ""

//            "supervisorId": "39d4044f-7e89-4893-bd7b-089fad730746",
//            "status": "TRIP_NOT_STARTED",
//            "displayTime": "8:00 am",
//            "dueOn": "2020-07-22T18:30:00.000Z",
//            "groupName": "Group R3",
//            "supervisorName": "Rohan Updated",
//            "studentName": "James Mathew"

            @SerializedName("dueOn")
            @Expose
            val dueOn: String = ""

            @SerializedName("groupName")
            @Expose
            val groupName: String = ""

            @SerializedName("supervisorName")
            @Expose
            val supervisorName: String = ""

            @SerializedName("studentName")
            @Expose
            val studentName: String = ""

            var isSupervisor: Boolean = true

        }

        class ParentData : Serializable {
            @SerializedName("parentId")
            @Expose
            var parentId: String? = null

            @SerializedName("email")
            @Expose
            var email: String? = null

            @SerializedName("fullName")
            @Expose
            var fullName: String? = null

            @SerializedName("contact")
            @Expose
            var contact: String? = null

            @SerializedName("address")
            @Expose
            var address: String? = null

            @SerializedName("image")
            @Expose
            var image: String? = null

            @SerializedName("createdOn")
            @Expose
            var createdOn: String? = null

            @SerializedName("isFirstTime")
            @Expose
            var isFirstTime: Int? = null

            @SerializedName("totalStudents")
            @Expose
            var totalStudents: Int? = null

            @SerializedName("totalTrips")
            @Expose
            var totalTrips: Int? = null

            @SerializedName("stopId")
            @Expose
            var stopId: Int? = null

            @SerializedName("stopName")
            @Expose
            var stopName: String? = null
        }

    }
}