
package com.asif.asifroutedirectionlibrary.request;

import com.asif.asifroutedirectionlibrary.DirectionCallback;
import com.asif.asifroutedirectionlibrary.model.Direction;
import com.asif.asifroutedirectionlibrary.network.DirectionConnection;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The class for assign the addition parameter for the request and request execution.
 *
 * @since 1.0.0
 */
public class DirectionRequest {
    private DirectionRequestParam param;

    public DirectionRequest(String apiKey, LatLng origin, LatLng destination, List<LatLng> waypointList) {
        param = new DirectionRequestParam().setApiKey(apiKey).setOrigin(origin).setDestination(destination).setWaypoints(waypointList);
    }

    /**
     * Assign the transport mode of the request.
     *
     * @param transportMode the language value from @see {@link }.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest transportMode(String transportMode) {
        param.setTransportMode(transportMode);
        return this;
    }

    /**
     * Assign the language of the request.
     *
     * @param language the language value from @see {@link }.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest language(String language) {
        param.setLanguage(language);
        return this;
    }

    /**
     * Assign the unit of the request.
     *
     * @param unit the unit value from @see {@link }.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest unit(String unit) {
        param.setUnit(unit);
        return this;
    }

    /**
     * Assign the route restriction to avoid of the request.
     *
     * @param avoid the route restriction to avoid value from @see {@link }.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest avoid(String avoid) {
        String oldAvoid = param.getAvoid();
        if (oldAvoid != null && !oldAvoid.isEmpty()) {
            oldAvoid += "|";
        } else {
            oldAvoid = "";
        }
        oldAvoid += avoid;
        param.setAvoid(oldAvoid);
        return this;
    }

    /**
     * Assign the transit mode of the request.
     *
     * @param transitMode the transit mode value from @see {@link }.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest transitMode(String transitMode) {
        String oldTransitMode = param.getTransitMode();
        if (oldTransitMode != null && !oldTransitMode.isEmpty()) {
            oldTransitMode += "|";
        } else {
            oldTransitMode = "";
        }
        oldTransitMode += transitMode;
        param.setTransitMode(oldTransitMode);
        return this;
    }

    /**
     * Specifies whether require the alternative route result of the request.
     *
     * @param alternative True for include and false for exclude.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest alternativeRoute(boolean alternative) {
        param.setAlternatives(alternative);
        return this;
    }

    /**
     * Assign the departure time of the request.
     *
     * @param time The departure time.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest departureTime(String time) {
        param.setDepartureTime(time);
        return this;
    }

    /**
     * Specifies whether require the optimized waypoint by reorder the waypoint in result.
     *
     * @param optimize True for optimize the waypoint and false for do not.
     * @return This direction request object.
     * @since 1.0.0
     */
    public DirectionRequest optimizeWaypoints(boolean optimize) {
        param.setOptimizeWaypoints(optimize);
        return this;
    }

    /**
     * Require the optimized waypoint by reorder the waypoint in result.
     *
     * @param callback Callback for the direction request.
     * @return The task for direction request.
     * @since 1.0.0
     */
    public DirectionTask execute(final DirectionCallback callback) {
        Call<Direction> direction = DirectionConnection.getInstance()
                .createService()
                .getDirection(param.getOrigin().latitude + "," + param.getOrigin().longitude,
                        param.getDestination().latitude + "," + param.getDestination().longitude,
                        waypointsToString(param.getWaypoints()),
                        param.getTransportMode(),
                        param.getDepartureTime(),
                        param.getLanguage(),
                        param.getUnit(),
                        param.getAvoid(),
                        param.getTransitMode(),
                        param.isAlternatives(),
                        param.getApiKey());

        direction.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                if (callback != null) {
                    callback.onDirectionSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                callback.onDirectionFailure(t);
            }
        });
        return new DirectionTask(direction);
    }

    /**
     * For internal use.
     */
    private String waypointsToString(List<LatLng> waypoints) {
        if (waypoints != null && !waypoints.isEmpty()) {
            StringBuilder string = new StringBuilder(param.isOptimizeWaypoints() ? "optimize:true|" : "");
            string.append(waypoints.get(0).latitude).append(",").append(waypoints.get(0).longitude);
            for (int i = 1; i < waypoints.size(); i++) {
                string.append("|").append(waypoints.get(i).latitude).append(",").append(waypoints.get(i).longitude);
            }
            return string.toString();
        }
        return null;
    }
}
