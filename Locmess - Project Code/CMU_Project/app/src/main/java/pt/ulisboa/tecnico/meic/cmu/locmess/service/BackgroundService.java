package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.locmess.inbox.InboxActivity;

/**
 * Created by Akilino on 29/04/2017.
 */

public class BackgroundService extends Service{

    private final IBinder mBinder = new LocalBinder();
    private String providerAsync;
    private LocationManager locationManager;
    double lat;
    double lng;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean isGPSTrackingEnabled = false;
    private String provider_info;
    private Location location = null;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int MIN_TIME_WAIT = 4000;
    private final int MIN_DISTANCE = 1;


    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("onLocationChanged", "onLocationChanged");
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            SharedPreferences sharedPref = getSharedPreferences("file", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("location", true);
            editor.putString("latitude", String.valueOf(location.getLatitude()));
            editor.putString("longitude", String.valueOf(location.getLongitude()));
            editor.commit();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("onStatusChanged", provider_info);

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("onProviderEnabled", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("OnProviderDisabled", "OnProviderDisabled");
        }

        public void cancel(){
            locationManager.removeUpdates(locationListener);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BackgroundService.this;
        }
    }

    @Override
    public void onCreate(){
        Log.d("onPreExecute: ", "Working");

        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled) {
            this.isGPSTrackingEnabled = true;

            provider_info = LocationManager.GPS_PROVIDER;

        }else

        if (isNetworkEnabled) {
            this.isGPSTrackingEnabled = true;

            provider_info = LocationManager.NETWORK_PROVIDER;

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "permission not granted");
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("permissions", "permission denied: ");
            return;
        }
        locationManager.requestLocationUpdates(provider_info, MIN_TIME_WAIT, MIN_DISTANCE, locationListener);
        Log.d("onPostExecute: ", "Working");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(InboxActivity.mBroadcastDevices);
                    broadcastIntent.putExtra("Device", "Received Device");
                    sendBroadcast(broadcastIntent);

                    broadcastIntent.setAction(InboxActivity.mBroadcastPosts);
                    broadcastIntent.putExtra("Post", "Received Post");
                    sendBroadcast(broadcastIntent);

                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}