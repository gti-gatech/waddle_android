package com.asif.asifroutedirectionlibrary.request;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for assign the waypoints and destination position.
 *
 * @since 1.0.0
 */
public class DirectionDestinationRequest {
    private String apiKey;
    private LatLng origin;
    private List<LatLng> waypointList;

    public DirectionDestinationRequest(String apiKey, LatLng origin) {
        this.apiKey = apiKey;
        this.origin = origin;
    }

    /**
     * Assign the waypoint position of the request
     *
     * @param waypoint The latitude and longitude of waypoint position.
     * @return This destination request object.
     * @since 1.0.0
     */
    public DirectionDestinationRequest and(LatLng waypoint) {
        if (waypointList == null) {
            waypointList = new ArrayList<>();
        }
        waypointList.add(waypoint);
        return this;
    }

    /**
     * Assign the waypoint position of the request as a list.
     *
     * @param waypointList The list of latitude and longitude of waypoint position.
     * @return This destination request object.
     * @since 1.0.0
     */
    public DirectionDestinationRequest and(List<LatLng> waypointList) {
        if (this.waypointList == null) {
            this.waypointList = new ArrayList<>();
        }
        this.waypointList.addAll(waypointList);
        return this;
    }

    /**
     * Assign the destination position of the request.
     *
     * @param destination The latitude and longitude of destination position.
     * @return The direction request object.
     * @since 1.0.0
     */
    public DirectionRequest to(LatLng destination) {
        return new DirectionRequest(apiKey, origin, destination, waypointList);
    }
}
