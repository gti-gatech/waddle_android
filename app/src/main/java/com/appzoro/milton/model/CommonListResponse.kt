package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CommonListResponse : Serializable{
    @SerializedName("type")
    @Expose
    private var type: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("data")
    @Expose
    private var data: ArrayList<DatumModel>? = null

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

    fun getData(): ArrayList<DatumModel>? {
        return data
    }

    fun setData(data: ArrayList<DatumModel>) {
        this.data = data
    }
}