package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LocationModel:Serializable {
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