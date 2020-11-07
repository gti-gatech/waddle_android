package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MessageModel : Serializable {

    @SerializedName("groupId")
    @Expose
    var groupId: Int? = null

    @SerializedName("groupName")
    @Expose
    var groupName: String = ""

    @SerializedName("routeId")
    @Expose
    var routeId: Int? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("totalUnRead")
    @Expose
    var totalUnRead: Int? = null

    @SerializedName("messageId")
    @Expose
    var messageId: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("senderId")
    @Expose
    var senderId: String? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("senderName")
    @Expose
    var senderName: String = ""

    @SerializedName("fullName")
    @Expose
    var fullName: String = ""

}