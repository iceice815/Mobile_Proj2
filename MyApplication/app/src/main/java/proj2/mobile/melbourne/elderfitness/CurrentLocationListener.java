package proj2.mobile.melbourne.elderfitness;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by iceice on 1/10/17.
 */

public class CurrentLocationListener implements LocationListener {
    private Location current_location=null;
    public CurrentLocationListener(){}
    @Override
    public void onLocationChanged(Location location) {
        this.current_location=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(Location current_location) {
        this.current_location = current_location;
    }
}
