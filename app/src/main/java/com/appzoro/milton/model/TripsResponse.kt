package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TripsResponse : Serializable {

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

        @SerializedName("history")
        @Expose
        var history: History? = null

        @SerializedName("upcoming")
        @Expose
        var upcoming: Upcoming? = null

        class History : Serializable {

            @SerializedName("studentsHistory")
            @Expose
            var studentsHistory: AllHistory? = null

            @SerializedName("supervisorHistory")
            @Expose
            var supervisorHistory: AllHistory? = null

            class AllHistory : Serializable {

                @SerializedName("today")
                @Expose
                var today: ArrayList<DatumModel>? = null

                @SerializedName("yesterday")
                @Expose
                var yesterday: ArrayList<DatumModel>? = null

                @SerializedName("previous")
                @Expose
                var previous: ArrayList<DatumModel>? = null

            }

        }

        class Upcoming : Serializable {

            @SerializedName("studentsUpcoming")
            @Expose
            var studentsUpcoming: ArrayList<DatumModel>? = null

            @SerializedName("supervisorUpcoming")
            @Expose
            var supervisorUpcoming: ArrayList<DatumModel>? = null

        }

    }
}