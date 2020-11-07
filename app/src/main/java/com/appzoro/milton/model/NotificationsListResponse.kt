package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationsListResponse : Serializable {

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

        @SerializedName("today")
        @Expose
        var today: ArrayList<Datum>? = null

        @SerializedName("yesterday")
        @Expose
        var yesterday: ArrayList<Datum>? = null

        @SerializedName("previous")
        @Expose
        var previous: ArrayList<Datum>? = null

        class Datum : Serializable {

            @SerializedName("id")
            @Expose
            internal val id: String? = null

            @SerializedName("parentId")
            @Expose
            internal val parentId: String? = null

            @SerializedName("hasActions")
            @Expose
            internal var hasActions: String? = null

            @SerializedName("message")
            @Expose
            internal val message: String? = null

            @SerializedName("type")
            @Expose
            internal val type: String? = null

            @SerializedName("status")
            @Expose
            internal val status: String? = null

            @SerializedName("dueOn")
            @Expose
            internal val dueOn: String? = null

            @SerializedName("payload")
            @Expose
            internal val payload: DatumModel? = null

            @SerializedName("actions")
            @Expose
            internal var actions: Actions? = null

            class Actions : Serializable {

                @SerializedName("0")
                @Expose
                internal val zero0: String? = null

                @SerializedName("1")
                @Expose
                internal val one1: String? = null

            }

             var notificationType: String = ""

        }
    }
}