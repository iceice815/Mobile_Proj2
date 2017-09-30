package proj2.mobile.melbourne.elderfitness;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by iceice on 1/10/17.
 */

public class GetGPS {
    private LocationManager mLocationManager;
    private Context context;
    public GetGPS(LocationManager mLocationManager, Context context){
        this.mLocationManager=mLocationManager;
        this.context = context;
    }
    public void start(CurrentLocationListener location_listener){
        //get locationManager object, bind lisener
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //define current defined Location Provider
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //set return location from GPS every 2 second gap
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, location_listener);
    }

}
