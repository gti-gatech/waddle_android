package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class MediaUploadResponse {

    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {

        @SerializedName("fd")
        @Expose
        var fd: String = ""

        @SerializedName("status")
        @Expose
        var status: String = ""

        @SerializedName("filename")
        @Expose
        var filename: String = ""

    }

}