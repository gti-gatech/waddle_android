
package com.asif.asifroutedirectionlibrary.request;

import com.google.android.gms.maps.model.LatLng;

/**
 * The class for assign the origin position.
 *
 * @since 1.0.0
 */
public class DirectionOriginRequest {
    private String apiKey;

    public DirectionOriginRequest(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Assign the origin coordination of the request.
     *
     * @param origin The latitude and longitude of origin position
     * @return The destination request object.
     * @since 1.0.0
     */
    public DirectionDestinationRequest from(LatLng origin) {
        return new DirectionDestinationRequest(apiKey, origin);
    }
}
