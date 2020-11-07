package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetScheduleResponse1 : Serializable {

    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null

    class Datum : Serializable {
        @SerializedName("studentId")
        @Expose
        internal val studentId: String? = null

        @SerializedName("tripId")
        @Expose
        internal val tripId: String? = null

        @SerializedName("stopId")
        @Expose
        internal val stopId: String? = null

        @SerializedName("groupId")
        @Expose
        internal val groupId: Int? = null

        @SerializedName("isSupervised")
        @Expose
        internal val isSupervised: Int? = null

        @SerializedName("supervisorId")
        @Expose
        internal val supervisorId: String? = null

        @SerializedName("status")
        @Expose
        internal val status: String? = null

        @SerializedName("displayTime")
        @Expose
        internal val displayTime: String? = null

        @SerializedName("dueOn")
        @Expose
        internal val dueOn: String? = null

        @SerializedName("groupName")
        @Expose
        internal val groupName: String? = null

        @SerializedName("routeId")
        @Expose
        internal val routeId: Int? = null

        @SerializedName("supervisorName")
        @Expose
        internal val supervisorName: String? = null

        @SerializedName("studentName")
        @Expose
        internal val studentName: String? = null

        @SerializedName("stopName")
        @Expose
        internal val stopName: String? = null
    }
}