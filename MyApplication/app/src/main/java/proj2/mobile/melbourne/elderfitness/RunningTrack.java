package proj2.mobile.melbourne.elderfitness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import proj2.mobile.melbourne.elderfitness.data.RecordTrack;
import proj2.mobile.melbourne.elderfitness.util.ClockCount;
import proj2.mobile.melbourne.elderfitness.util.CurrentLocationListener;
import proj2.mobile.melbourne.elderfitness.util.GetGPS;
import proj2.mobile.melbourne.elderfitness.util.InitializeTable;

import static android.hardware.SensorManager.getAltitude;
import static proj2.mobile.melbourne.elderfitness.util.DistanceCalculation.getDistanceFromLocations;
/**
 * Created by iceice on 9/12/17.
 */
public class RunningTrack extends AppCompatActivity implements OnMapReadyCallback, InitializeTable {

    private MobileServiceClient mClient;
    private MobileServiceTable<RecordTrack> mRecordTable;

    private GoogleMap mMap;

    private ToggleButton mToggle;
    private Button mRecordButton;
    private Button mEmergencyButton;
    private TextView mText;
    private TextView mClockCount;
    private TextView mDistance;

    private double upAltitude;
    private double downAltitude;
    private String username;
    private String emgergency_number;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3 = new Timer();
    private static final int DANGEROUS_DISTANCE=30;
    private int temp_distance;
    private SensorManager mSensorManager;
    private LocationManager mLocationManager;
    private Sensor mPressure;
    private SensorEventListener mPressureListener;
    private SmsManager mSmsManager;

    ArrayList<Location> locations = new ArrayList<Location>();
    private ClockCount clockCount = new ClockCount();

    private CurrentLocationListener currentLocationListener =new CurrentLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_track);

        mText = (TextView) findViewById(R.id.NameID);
        mToggle = (ToggleButton) findViewById(R.id.ToggleID);
        mRecordButton = (Button) findViewById(R.id.RecordID);
        mEmergencyButton = (Button) findViewById(R.id.EmergencyID);
        mClockCount = (TextView)findViewById(R.id.ClockCountID);
        mDistance = (TextView)findViewById(R.id.DistanceID);

        //create sensorManager object from system service
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //use the baromeer sensor
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);


        //get database and table instance
        init_table();
        //initilize google map
        init_Map();

        //set text with username
        Intent rec_intent = getIntent();
        username = rec_intent.getStringExtra("username");
        emgergency_number = rec_intent.getStringExtra("Emergency_Number");
        mText.setText(username);

        ToogleLisener listener = new ToogleLisener();
        mToggle.setOnCheckedChangeListener(listener);
        //Onclick event for jump to Datavirtualization activity
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goto_data = new Intent(getApplicationContext(), DataVirtualization.class);
                goto_data.putExtra("username",username);
                startActivity(goto_data);
            }
        });
        //Onclick event for send message to their family member
        //if emergency issues happen.
        GetGPS getGPS = new GetGPS(mLocationManager, getApplicationContext());
        getGPS.start(currentLocationListener);
        mEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_sms_to_familymember();
            }
        });



    }
    private void send_sms_to_familymember(){
         //send sms
        if(currentLocationListener.getCurrent_location()!=null){
            //mLocationManager.removeUpdates(currentLocationListener);
            //PendingIntent pi = PendingIntent.getActivity(RunningTrack.this, 0, new Intent(), 0);
            String content = "Your family member meets emergency situation at " + get_address(getApplicationContext(), currentLocationListener.getCurrent_location().getLatitude(), currentLocationListener.getCurrent_location().getLongitude())
                    + ". He/She need your help";
            try {
                mSmsManager = SmsManager.getDefault();
                mSmsManager.sendTextMessage(emgergency_number, null, content, null, null);
                Toast.makeText(RunningTrack.this, "Sent!", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(RunningTrack.this, "Failed!", Toast.LENGTH_SHORT).show();

            }
        }


    }


    private class ToogleLisener implements CompoundButton.OnCheckedChangeListener{
        NewLocationListener location_listener =new NewLocationListener();
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                mToggle.setChecked(true);
                //initialize location
                iniLocation();
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RunningTrack.this)
                        .setTitle("Goal")
                        .setIcon(R.drawable.goal)
                        .setMessage("Running for better life and better future!");
                setPositiveButtonForGoal(builder)
                        .create()
                        .show();
                get_barometer();
                get_GPS(location_listener);
                start_clock_count(timer1);
                start_distance_count(timer2);
                start_auto_safety(timer3);
            }
            else{
                mToggle.setChecked(false);
                //display a AlertDialog to ask user whether upload data to real-time database
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RunningTrack.this)
                        .setTitle("Synchronization")
                        .setIcon(R.drawable.submittool)
                        .setMessage("Do you submit your walking data to Azure database?");

                setPositiveButton(builder);
                setNegativeButton(builder)
                        .create()
                        .show();
                mLocationManager.removeUpdates(location_listener);
                stop_clock_count(timer1);
                stop_distance_count(timer2);
                stop_auto_safety(timer3);
            }
        }
    }
    private void start_auto_safety(Timer timer3){

        timer3.schedule(new TimerTask() {
            @Override
            public void run() {
                int current_distance = getDistanceFromLocations(locations);
                if(current_distance-temp_distance<=DANGEROUS_DISTANCE){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send_sms_to_familymember();
                        }
                    });
                }
                temp_distance =current_distance;
            }
        },0,180000);
    }
    private void stop_auto_safety(Timer timer3){
        timer3.cancel();
    }
    private void stop_distance_count(Timer timer2){
        timer2.cancel();
        mDistance.setText("0 km");
    }
    private void start_distance_count(Timer timer2){
        timer2.schedule(new TimerTask() {
            double dis=0;
            @Override
            public void run() {
                int distance = getDistanceFromLocations(locations);
                dis = (double)distance/1000;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDistance.setText(String.format("%.1f", dis)+" km");
                    }
                });

            }
        },0,1000);


    }
    private void stop_clock_count(Timer timer ){
        timer.cancel();
        clockCount.clear();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mClockCount.setText(clockCount.getTime());
            }
        });
    }
    private void start_clock_count(Timer timer){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clockCount.start();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mClockCount.setText(clockCount.getTime());
                    }
                });

            }
        },0,1000);


    }
    private void get_GPS(NewLocationListener location_listener){
        //get locationManager object, bind lisener
         mLocationManager = (LocationManager) RunningTrack.this.getSystemService(Context.LOCATION_SERVICE);
        //define current defined Location Provider
        if (ActivityCompat.checkSelfPermission(RunningTrack.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunningTrack.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    /**
     * track location in google map based on GPS sensor.
     */
    public class NewLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            goTonLocationZoom(lat,lng,16);
            iniLocation();
            locations.add(location);
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
    }
    /**
     * the button in dialog for telling user to comfirm his goal
     */
    private android.support.v7.app.AlertDialog.Builder setPositiveButtonForGoal(android.support.v7.app.AlertDialog.Builder builder){
        return builder.setPositiveButton("Confirm",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

    }
    /**
     * the button in dialog for submitting data to real-time database
     */
    private android.support.v7.app.AlertDialog.Builder setPositiveButton(android.support.v7.app.AlertDialog.Builder builder) {

        return builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //draw walk route on google map
                draw_lines();
                //sen data to Azure database of cloud server
                add_data_to_cloud();
                Toast.makeText(RunningTrack.this, "Submiting...", Toast.LENGTH_SHORT).show();


            }
        });

    }
    /**
     * the button in dialog for submitting data to azure database
     */

    private android.support.v7.app.AlertDialog.Builder setNegativeButton(android.support.v7.app.AlertDialog.Builder builder) {
        //invoke setNegativeButton method for add “cancell" event
        return builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upAltitude=0;
                downAltitude=0;
                locations.clear();
                Toast.makeText(RunningTrack.this, "Deleting...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * add data to Azure cloud server
     */

    public void add_data_to_cloud(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String date_to_string = simpleDateFormat.format(date);
        final RecordTrack recordTrack = new RecordTrack();
        int distance = getDistanceFromLocations(locations);
        int flights_climbed = (int)((upAltitude + downAltitude)/3.3);
        double calories = (upAltitude + downAltitude) * 1.1 + distance * 0.065;

        recordTrack.setmUsername(username);
        recordTrack.setmDate(date_to_string);
        recordTrack.setmDistance(distance);
        recordTrack.setmFlightsClimbed(flights_climbed);
        recordTrack.setmColories(calories);
        //This is important since data should be clear after update to database;

        if(mClient ==null){
            return;
        }
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mRecordTable.insert(recordTrack).get();
                    /**
                     * fuck that shit!!!!!! Toast shoud not directly used in asyc job
                     * It should revoke runOnUiThread method, otherwise the app shut down!!!!
                     */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RunningTrack.this, "Data Successfully submited!", Toast.LENGTH_SHORT).show();
                        }
                    });
                     } catch (Exception e){
                    createAndShowDialog(e,"Error");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RunningTrack.this, "Unable to save data to azure database", Toast.LENGTH_LONG).show();

                        }
                    });

                     }
                return null;
            }
        }.execute();

        locations.clear();
        upAltitude=0;
        downAltitude=0;

    }
    /**
     * press stop and draw line on google map
     */
    private void draw_lines() {
        PolylineOptions options = new PolylineOptions()
                .color(Color.RED)
                .width(10);
        for (int i = 0; i < locations.size(); i++) {
            LatLng ll = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
            options.add(ll);
        }
        mMap.addPolyline(options);

    }
    /**
     * start to record data use GPS and Barometer sensor
     */

    private void get_barometer(){
         //define air pressure lisener
        mPressureListener = new SensorEventListener() {
            int cnt = 0;
            double tempAltitude = 0;

            @Override
            public void onSensorChanged(SensorEvent event) {
                //return from Barometer sensor values[0]: Atmospheric pressure in hPa (millibar)
                float p = event.values[0];
                double Altitude = (double) getAltitude((float) 1013.25, p);
                if (cnt == 0) {
                    tempAltitude = Altitude;
                    cnt++;
                }
                if ((Altitude - tempAltitude) >= 2) {
                    tempAltitude = Altitude;
                    upAltitude=upAltitude+2;
                }
                if ((Altitude - tempAltitude) <= -2) {
                    tempAltitude = Altitude;
                    downAltitude=downAltitude+2;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

    }

    /**
     *  zooming the current location and update
     */
    private void goTonLocationZoom(double lat, double lng, int zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    /**
     * initialize the currently location on google map
     */
    private void iniLocation(){
        if (ActivityCompat.checkSelfPermission(RunningTrack.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RunningTrack.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }



    private void init_Map(){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapID);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }


    /*********************************************************************************
     ********************************you can ignore it********************************
     * *******************************************************************************
     */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mPressureListener, mPressure,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

//
    public String get_address(Context ctx, double lat, double lng){
        String full_adrress = null;
        try{
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                String addr = address.getAddressLine(0);
                String area = address.getLocality();
                String city = address.getAdminArea();
                String country = address.getCountryName();
                String postalcode =address.getPostalCode();
                full_adrress=addr+", "+area+", "+city+", "+country+", "+postalcode;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return full_adrress;
    }
    @Override
    public void init_table() {
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://elderfitness.azurewebsites.net",
                    this);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mRecordTable = mClient.getTable(RecordTrack.class);
        }catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }


}
