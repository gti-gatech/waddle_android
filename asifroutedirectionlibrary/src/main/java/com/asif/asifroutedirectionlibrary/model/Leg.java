
package com.asif.asifroutedirectionlibrary.model;

import com.asif.asifroutedirectionlibrary.util.DirectionConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Leg {
    @SerializedName("arrival_time")
    private TimeInfo arrivalTime;
    @SerializedName("departure_time")
    private TimeInfo departureTime;
    @SerializedName("distance")
    private Info distance;
    @SerializedName("duration")
    private Info duration;
    @SerializedName("duration_in_traffic")
    private Info durationInTraffic;
    @SerializedName("end_address")
    private String endAddress;
    @SerializedName("end_location")
    private Coordination endLocation;
    @SerializedName("start_address")
    private String startAddress;
    @SerializedName("start_location")
    private Coordination startLocation;
    @SerializedName("steps")
    private List<Step> stepList;
    @SerializedName("via_waypoint")
    private List<Waypoint> viaWaypointList;

    public TimeInfo getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(TimeInfo arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public TimeInfo getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(TimeInfo departureTime) {
        this.departureTime = departureTime;
    }

    public Info getDistance() {
        return distance;
    }

    public void setDistance(Info distance) {
        this.distance = distance;
    }

    public Info getDuration() {
        return duration;
    }

    public void setDuration(Info duration) {
        this.duration = duration;
    }

    public Info getDurationInTraffic() {
        return durationInTraffic;
    }

    public void setDurationInTraffic(Info durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Coordination getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Coordination endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public Coordination getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Coordination startLocation) {
        this.startLocation = startLocation;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public List<Waypoint> getViaWaypointList() {
        return viaWaypointList;
    }

    public void setViaWaypointList(List<Waypoint> viaWaypointList) {
        this.viaWaypointList = viaWaypointList;
    }

    public ArrayList<LatLng> getDirectionPoint() {
        return DirectionConverter.getDirectionPoint(stepList);
    }

    public ArrayList<LatLng> getSectionPoint() {
        return DirectionConverter.getSectionPoint(stepList);
    }
}
