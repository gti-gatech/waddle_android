package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class LoginResponse {

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

        @SerializedName("parentData")
        @Expose
        var parentData: ParentData? = null

        @SerializedName("auth")
        @Expose
        var auth: Auth? = null

        class ParentData {

            @SerializedName("parentId")
            @Expose
            var parentId: String = ""

            @SerializedName("email")
            @Expose
            var email: String = ""

            @SerializedName("fullName")
            @Expose
            var fullName: String = ""

            @SerializedName("contact")
            @Expose
            var contact: String = ""

            @SerializedName("address")
            @Expose
            var address: String = ""

            @SerializedName("createdOn")
            @Expose
            var createdOn: String = ""

            @SerializedName("isFirstTime")
            @Expose
            var isFirstTime: String = ""

            @SerializedName("totalTrips")
            @Expose
            var totalTrips: String = "0"

            @SerializedName("totalStudents")
            @Expose
            var totalStudents: String = "0"

            @SerializedName("image")
            @Expose
            var image: String = ""

            @SerializedName("stopId")
            @Expose
            var stopId: String = ""

            @SerializedName("stopName")
            @Expose
            var stopName: String = ""

        }

        class Auth {

            @SerializedName("type")
            @Expose
            var type: String = ""

            @SerializedName("authToken")
            @Expose
            var authToken: String = ""

        }

    }

}