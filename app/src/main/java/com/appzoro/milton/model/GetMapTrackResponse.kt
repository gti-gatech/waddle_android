package com.appzoro.milton.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetMapTrackResponse {
    @SerializedName("type")
    @Expose
    private var type: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("data")
    @Expose
    private var data: Data? = null

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): Data? {
        return data
    }

    fun setData(data: Data?) {
        this.data = data
    }


    class Tripstatus {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

    }

    class Data {
        @SerializedName("tripStatus")
        @Expose
        var tripStatus: List<Tripstatus>? = null

        @SerializedName("data")
        @Expose
        var data: ArrayList<Datum>? = null

        class Datum {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("studentId")
            @Expose
            var studentId: Int? = null

            @SerializedName("tripId")
            @Expose
            var tripId: Int? = null

            @SerializedName("status")
            @Expose
            var status: String? = null

            @SerializedName("isActive")
            @Expose
            var isActive: Int? = null

            @SerializedName("createdOn")
            @Expose
            var createdOn: String? = null

            @SerializedName("modifiedOn")
            @Expose
            var modifiedOn: String? = null

            @SerializedName("stopId")
            @Expose
            var stopId: Int? = null

            @SerializedName("pickupCount")
            @Expose
            var pickupCount: Int? = null

            @SerializedName("studentName")
            @Expose
            var studentName: String? = null

            @SerializedName("studentGrade")
            @Expose
            var studentGrade: String? = null

            @SerializedName("contact")
            @Expose
            var contact: String? = null

            @SerializedName("parentName")
            @Expose
            var parentName: String? = null

            @SerializedName("stopName")
            @Expose
            var stopName: String? = null

            var travelDistance: String = ""
            var travelTime: String = ""

            @SerializedName("stopLocation")
            @Expose
            var stopLocation: LocationData? = null

            var isTripStarted: Boolean = false

            inner class LocationData {
                @SerializedName("0")
                @Expose
                private var longitude: Double = 0.0

                @SerializedName("1")
                @Expose
                private var latitude: Double = 0.0

                @SerializedName("2")
                @Expose
                private var _2: Int = 0

                fun getLongitude(): Double {
                    return longitude
                }

                fun setLongitude(latitude: Double) {
                    this.longitude = latitude
                }

                fun getLatitude(): Double {
                    return latitude
                }

                fun setLatitude(latitude: Double) {
                    this.latitude = latitude
                }

                fun get2(): Int {
                    return _2
                }

                fun set2(_2: Int) {
                    this._2 = _2
                }

            }

        }
    }
}