package com.appzoro.milton.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DatumModel : Serializable {
    @SerializedName("stopId")
    @Expose
    private var stopId: Int? = null

    @SerializedName("deviceToken")
    @Expose
    private var deviceToken: String? = null

    @SerializedName("routeId")
    @Expose
    private var routeId: Int? = null

    @SerializedName("name")
    @Expose
    private var name: String? = null

    @SerializedName("createdOn")
    @Expose
    private var createdOn: String? = null

    @SerializedName("location")
    @Expose
    private var location: LocationModel? = null

    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("studentId")
    @Expose
    private var studentId: String? = null

    @SerializedName("parentId")
    @Expose
    private var parentId: String? = null

    @SerializedName("fullName")
    @Expose
    private var fullName: String? = null

    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("grade")
    @Expose
    private var grade: String? = null

    @SerializedName("image")
    @Expose
    private var image: String? = null

    @SerializedName("schoolName")
    @Expose
    private var schoolName: String? = null

    var isItemCheck: Boolean = false

    @SerializedName("groupId")
    @Expose
    private var groupId: Int? = null

    @SerializedName("groupName")
    @Expose
    private var groupName: String? = null


    @SerializedName("totalStudents")
    @Expose
    private var totalStudents: Int? = null

    @SerializedName("totalTrips")
    @Expose
    private var totalTrips: Int? = null

    @SerializedName("startLocation")
    @Expose
    private var startLocation: LocationModel? = null

    @SerializedName("endLocation")
    @Expose
    private var endLocation: LocationModel? = null


    @SerializedName("students")
    @Expose
    private var students: List<Int?>? = null


    @SerializedName("tripsWalked")
    @Expose
    private var tripsWalked: Int? = null

    @SerializedName("isSupervised")
    @Expose
    private var isSupervised: Int? = null

    @SerializedName("supervisorId")
    @Expose
    private var supervisorId: String? = null

    @SerializedName("supervisorName")
    @Expose
    private var supervisorName: String? = null

    @SerializedName("tripDate")
    @Expose
    private var tripDate: String? = null

    @SerializedName("displayTime")
    @Expose
    private var displayTime: String? = null

    @SerializedName("dueOn")
    @Expose
    private var dueOn: String? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("supervisorStar")
    @Expose
    private var supervisorStar: Int? = null

    @SerializedName("isActive")
    @Expose
    private var isActive: Int? = null

    @SerializedName("tripId")
    @Expose
    private var tripId: Int? = null

    @SerializedName("stopName")
    @Expose
    private var stopName: String = ""

    @SerializedName("pickupStopName")
    @Expose
    private var pickupStopName: String = ""

    @SerializedName("pickupStop")
    @Expose
    private var pickupStop: String = ""

    @SerializedName("studentName")
    @Expose
    private var studentName: String = ""

    @SerializedName("startTripFlag")
    @Expose
    private var startTripFlag: Int = 0

    @SerializedName("supervising")
    @Expose
    private var supervising: Int? = null

    @SerializedName("alreadySupervised")
    @Expose
    private var alreadySupervised: Int? = null

    fun getSupervising(): Int? {
        return supervising
    }

    fun setSupervising(supervising: Int?) {
        this.supervising = supervising
    }

    fun getAlreadySupervised(): Int? {
        return alreadySupervised
    }

    fun setAlreadySupervised(alreadySupervised: Int?) {
        this.alreadySupervised = alreadySupervised
    }

    fun getStartTripFlag(): Int {
        return startTripFlag
    }

    fun getStudentName(): String {
        return studentName
    }

    fun getStopName(): String {
        return stopName
    }

    fun getPickupStopNamee(): String? {
        return pickupStopName
    }

    fun getPickupStop(): String {
        return pickupStop
    }

    fun getGroupId(): Int {
        return groupId ?: 0
    }

    fun getTripId(): Int {
        return tripId ?: 0
    }

    fun setPickUpStop(pickupStopName: String?) {
        this.pickupStopName = pickupStopName!!
    }

    fun setStartTripFlag(startTripFlag: Int) {
        this.startTripFlag = startTripFlag
    }

    fun setGroupId(groupId: Int?) {
        this.groupId = groupId
    }

    fun getRouteId(): Int {
        return routeId ?: 0
    }

    fun setRouteId(routeId: Int?) {
        this.routeId = routeId
    }

    fun getImage(): String {
        return image ?: ""
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getTripsWalked(): Int {
        return tripsWalked ?: 0
    }

    fun setTripsWalked(tripsWalked: Int?) {
        this.tripsWalked = tripsWalked
    }

    fun getGroupName(): String {
        return groupName ?: ""
    }

    fun setGroupName(groupName: String?) {
        this.groupName = groupName
    }

    fun getIsSupervised(): Int {
        return isSupervised ?: 0
    }

    fun setIsSupervised(isSupervised: Int?) {
        this.isSupervised = isSupervised
    }

    fun getSupervisorId(): String {
        return supervisorId ?: "0"
    }

    fun getSupervisorName(): String {
        return supervisorName ?: ""
    }

    fun setSupervisorName(superVisorName: String?) {
        this.supervisorName = superVisorName
    }

    fun setSupervisorId(supervisorId: String?) {
        this.supervisorId = supervisorId
    }

    fun getTripDate(): String? {
        return tripDate
    }

    fun setTripDate(tripDate: String?) {
        this.tripDate = tripDate
    }

    fun getDisplayTime(): String? {
        return displayTime
    }

    fun setDisplayTime(displayTime: String?) {
        this.displayTime = displayTime
    }

    fun getDueOn(): String {
        return dueOn ?: ""
    }

    fun setDueOn(dueOn: String?) {
        this.dueOn = dueOn
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getSupervisorStar(): Int? {
        return supervisorStar
    }

    fun setSupervisorStar(supervisorStar: Int?) {
        this.supervisorStar = supervisorStar
    }


    fun getStudents(): List<Int?>? {
        return students
    }

    fun setStudents(students: List<Int?>?) {
        this.students = students
    }

    fun getTotalStudents(): Int {
        return totalStudents ?: 0
    }

    fun setTotalStudents(students: Int?) {
        this.totalStudents = students
    }


    fun getCreatedOn(): String? {
        return createdOn
    }

    fun setCreatedOn(createdOn: String) {
        this.createdOn = createdOn
    }


    fun getTotalTrips(): Int? {
        return totalTrips
    }

    fun setTotalTrips(totalTrips: Int?) {
        this.totalTrips = totalTrips
    }

    fun getStartLocation(): LocationModel? {
        return startLocation
    }

    fun setStartLocation(startLocation: LocationModel?) {
        this.startLocation = startLocation
    }

    fun getEndLocation(): LocationModel? {
        return endLocation
    }

    fun setEndLocation(endLocation: LocationModel?) {
        this.endLocation = endLocation
    }


    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getParentId(): String? {
        return parentId
    }

    fun setParentId(parentId: String?) {
        this.parentId = parentId
    }

    fun getFullName(): String? {
        return fullName
    }

    fun setFullName(fullName: String?) {
        this.fullName = fullName
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getGrade(): String? {
        return grade
    }

    fun setGrade(grade: String?) {
        this.grade = grade
    }

    fun getSchoolName(): String? {
        return schoolName
    }

    fun setSchoolName(schoolName: String?) {
        this.schoolName = schoolName
    }


    fun getStopId(): Int? {
        return stopId
    }

    fun setStopId(stopId: Int?) {
        this.stopId = stopId
    }


    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getLocation(): LocationModel? {
        return location
    }

    fun setLocation(location: LocationModel?) {
        this.location = location
    }

}