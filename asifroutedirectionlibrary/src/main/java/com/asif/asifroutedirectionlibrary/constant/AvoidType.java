
package com.asif.asifroutedirectionlibrary.constant;

/**
 * Route restriction to avoid.
 *
 * @since 1.0.0
 */
public class AvoidType {
    /**
     * Indicates that the calculated route should avoid toll roads/bridges.
     *
     * @since 1.0.0
     */
    public static final String TOLLS = "tolls";
    /**
     * Indicates that the calculated route should avoid highways.
     *
     * @since 1.0.0
     */
    public static final String HIGHWAYS = "highways";
    /**
     * Indicates that the calculated route should avoid ferries.
     *
     * @since 1.0.0
     */
    public static final String FERRIES = "ferries";
    /**
     * Indicates that the calculated route should avoid indoor
     * steps for walking and transit directions.
     *
     * @since 1.0.0
     */
    public static final String INDOOR = "indoor";
}
