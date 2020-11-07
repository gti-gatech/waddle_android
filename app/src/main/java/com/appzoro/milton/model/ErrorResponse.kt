package com.appzoro.milton.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorResponse {

    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

}