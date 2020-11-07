
package com.asif.asifroutedirectionlibrary;


import com.asif.asifroutedirectionlibrary.model.Direction;

/**
 * Interface for the response from the direction request of the Google Direction API.
 *
 * @since 1.0.0
 */
public interface DirectionCallback {
    /**
     * Retrieve the response from direction request successfully.
     *
     * @param direction The direction result from the Google Direction API
     * @since 1.0.0
     */
    void onDirectionSuccess(Direction direction);

    /**
     * Retrieve the response from direction request with error result.
     *
     * @param t A throwable from the response of Google Direction API.
     * @since 1.0.0
     */
    void onDirectionFailure(Throwable t);
}
