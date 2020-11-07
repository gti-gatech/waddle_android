package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GroupDetailsResponse : Serializable {

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

        @SerializedName("groupDetails")
        @Expose
        var groupDetails: GroupDetails? = null

        @SerializedName("groupStudents")
        @Expose
        var groupStudents: ArrayList<DatumModel>? = null

        @SerializedName("trips")
        @Expose
        var trips: ArrayList<DatumModel>? = null

        class GroupDetails : Serializable {

            @SerializedName("groupId")
            @Expose
             val groupId: String = ""

            @SerializedName("groupName")
            @Expose
             val groupName: String = ""

            @SerializedName("routeId")
            @Expose
             val routeId: String = ""

            @SerializedName("image")
            @Expose
             val image: String = ""

            @SerializedName("createdOn")
            @Expose
             val createdOn: String = ""

            @SerializedName("totalStudents")
            @Expose
             val totalStudents: String = ""

            @SerializedName("tripsWalked")
            @Expose
             val tripsWalked: String = ""

        }

    }
}