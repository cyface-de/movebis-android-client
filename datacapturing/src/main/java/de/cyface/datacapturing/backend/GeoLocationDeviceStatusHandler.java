package de.cyface.datacapturing.backend;

import java.util.ArrayList;
import java.util.Collection;

import android.location.LocationManager;

/**
 * Abstract base class for classes informing the system about the current state of the geo location device. It reacts to
 * fix events and if those events occur often enough it tells its <code>CapturingProcessListener</code>s about the state
 * change.
 *
 * @author Klemens Muthmann
 * @version 2.0.2
 * @since 1.0.0
 */
abstract class GeoLocationDeviceStatusHandler {
    /**
     * Interval in which location updates need to occur for the device to consider itself having a fix. Reasoning behind
     * this number is the following: Usually the geo location device provides updates every second, give or take a few
     * milliseconds. According to sampling theorem we could guarantee updates every 2 seconds if a proper fix is
     * available.
     */
    private static final int MAX_TIME_SINCE_LAST_SATELLITE_UPDATE = 2000;

    /**
     * <code>true</code> if the service has a geo location fix; <code>false</code> otherwise.
     */
    private boolean hasGeoLocationFix;
    /**
     * Time of last location update. This is required to check whether fixes occur as often as desired.
     */
    private long timeOfLastLocationUpdate;
    /**
     * The <code>List</code> of listeners to inform about geo location updates.
     */
    private final Collection<CapturingProcessListener> listener = new ArrayList<>();

    /**
     * The <code>LocationManager</code> used to get geo location status updates.
     */
    final LocationManager locationManager;

    /**
     * Creates a new completely initialized <code>GeoLocationDeviceStatusHandler</code>.
     *
     * @param locationManager The <code>LocationManager</code> used to get geo location status updates.
     */
    GeoLocationDeviceStatusHandler(final LocationManager locationManager) {
        if (locationManager == null) {
            throw new IllegalArgumentException("Illegal argument: locationManager was null!");
        }

        this.locationManager = locationManager;
    }

    /**
     * Adds all the listeners from the provided <code>List</code> to this objects list of listeners that are informed
     * about geo location device status updates.
     *
     * @param listener A <code>List</code> of listeners that are interested of geo location status changes.
     */
    void setDataCapturingListener(final Collection<CapturingProcessListener> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Illegal argument: listener was null!");
        }

        this.listener.addAll(listener);
    }

    /**
     * @return <code>true</code> if the service has a geo location fix; <code>false</code> otherwise.
     */
    boolean hasLocationFix() {
        return hasGeoLocationFix;
    }

    /**
     * Resets the time for the last location update to a new value.
     * 
     * @param timeOfLastLocationUpdate The new time of a last location update.
     */
    void setTimeOfLastLocationUpdate(final long timeOfLastLocationUpdate) {
        this.timeOfLastLocationUpdate = timeOfLastLocationUpdate;
    }

    /**
     * Tells the system that this <code>GeoLocationDeviceStatusHandler</code> is going down and no longer interested
     * about geo location device status updates. This method should be called when the system shuts down to free up
     * resources.
     */
    abstract void shutdown();

    /**
     * Called each time the service receives an update from the geo location satellites.
     */
    void handleSatelliteStatusChange() {
        // If time of last location update was less then 2 seconds we still have a fix.
        long timePassedSinceLastSatelliteUpdate = System.currentTimeMillis() - timeOfLastLocationUpdate;
        hasGeoLocationFix = timePassedSinceLastSatelliteUpdate < MAX_TIME_SINCE_LAST_SATELLITE_UPDATE;
    }

    /**
     * Android provides a special callback for the first geo location fix. This is handled by this method.
     */
    void handleFirstFix() {
        hasGeoLocationFix = true;
        handleLocationFixEvent();
    }

    /**
     * Informs all listeners if fix has been lost or is still available.
     */
    private void handleLocationFixEvent() {
        if (hasGeoLocationFix) {
            for (CapturingProcessListener listener : this.listener) {
                listener.onLocationFix();
            }
        } else {
            for (CapturingProcessListener listener : this.listener) {
                listener.onLocationFixLost();
            }
        }
    }
}
