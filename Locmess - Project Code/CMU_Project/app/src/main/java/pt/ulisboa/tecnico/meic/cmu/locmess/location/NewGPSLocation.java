package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONObject;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Action;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Connection;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.MessageType;
import pt.ulisboa.tecnico.meic.cmu.locmess.tool.PermissionUtils;

import static pt.ulisboa.tecnico.meic.cmu.locmess.R.id.map;
import static pt.ulisboa.tecnico.meic.cmu.locmess.R.id.textViewRadius;

/**
 * Created by Akilino on 02/04/2017.
 */

public class NewGPSLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener {

    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private RadioGroup radioGroup;
    private RadioButton radioButtonGPS, radioButtonWifi;
    private TextView textViewLat, textViewLon,textViewRadiusSeekBar;
    private EditText editTextLocationName;
    private String marker = "Marker";
    private MarkerOptions markerCreated;
    private SeekBar seekBarRadius;
    private UiSettings uiSettings;
    private int progressRadius;
    private Circle radiusCircle;
    private String temp = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private boolean markerExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_gps);

        Intent intent = getIntent();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.createLocation);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtonGPS = (RadioButton) findViewById(R.id.radioButtonGps);
        radioButtonWifi = (RadioButton) findViewById(R.id.radioButtonWifi);

        editTextLocationName = (EditText) findViewById(R.id.editText_locationName);
        editTextLocationName.setText(intent.getStringExtra("locationName"));
        temp = intent.getStringExtra("locationName");

        textViewLat = (TextView) findViewById(R.id.textView_latitude);
        textViewLon = (TextView) findViewById(R.id.textView_longitude);

        textViewRadiusSeekBar = (TextView) findViewById(R.id.textViewRadiusSeekBar);
        seekBarRadius = (SeekBar) findViewById(R.id.seekBarRadius);
        textViewRadiusSeekBar.setText("Radius: 0m");

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progressRadius = progress;
                textViewRadiusSeekBar.setText("Radius: " + progress + "m");

                if(markerExists){
                    radiusCircle.setRadius(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButtonWifi) {
                    Toast.makeText(NewGPSLocation.this, "Wifi button clicked", Toast.LENGTH_SHORT).show();
                    createWifiActivity();
                }
            }
        });

        editTextLocationName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(markerCreated != null && radiusCircle != null) {
                    LatLng latLng = markerCreated.getPosition();
                    mMap.clear();
                    markerCreated = new MarkerOptions()
                            .position(latLng)
                            .title(s.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    radiusCircle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(progressRadius)
                            .strokeColor(0x99BAFF00)
                            .fillColor(0x30BAFF00)
                            .strokeWidth(5f));

                    Marker locationMarker = mMap.addMarker(markerCreated);
                    locationMarker.showInfoWindow();
                    radiusCircle.setRadius(progressRadius);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}{
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            if(isLocationComplete()) {
                Log.d("NewGPSLocation", "onOptionsItemSelected: It's inside");
                JSONObject json = new JSONObject();
                json.put("name", editTextLocationName.getText().toString());
                json.put("latitude", textViewLat.getText().toString().split(" ")[1]);
                json.put("longitude", textViewLon.getText().toString().split(" ")[1]);
                String radius=textViewRadiusSeekBar.getText().toString();
                json.put("radius", radius.substring(radius.indexOf(" ") + 1, radius.indexOf("m")));
                Action action = new Action(MessageType.createlocation, json);
                json = new Connection().execute(action);

                if(json!=null){
                    Toast.makeText(this, "Location successfuly created!", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(this, "Location can't be created!", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Complete all fields!", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isLocationComplete(){
        if(editTextLocationName.getText().toString().matches("") || textViewLat.getText().toString().matches("Latitude: ") || textViewLon.getText().toString().matches("Longitude: ") || textViewRadiusSeekBar.getText().toString().matches("Radius: 0m")){
            return false;
        }

        return true;
    }

    private void createWifiActivity() {

            finish();
            Intent intent = new Intent(this, NewWiFiLocation.class);
            intent.putExtra("locationName", editTextLocationName.getText().toString());
            startActivity(intent);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapLongClickListener(this);

        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(this, "Long Click", Toast.LENGTH_SHORT).show();

        mMap.clear();

        markerExists = true;

        if(!editTextLocationName.getText().toString().matches(""))
            marker = editTextLocationName.getText().toString();

        markerCreated = new MarkerOptions()
                .position(latLng)
                .title(marker)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        radiusCircle = mMap.addCircle(new CircleOptions()
            .center(latLng)
            .radius(progressRadius)
            .strokeColor(0x99BAFF00)
            .fillColor(0x30BAFF00)
            .strokeWidth(5f));

        radiusCircle.setVisible(true);

        Marker locationMarker = mMap.addMarker(markerCreated);
        locationMarker.showInfoWindow();

        textViewLat.setText("Lat: " + String.valueOf(latLng.latitude));
        textViewLon.setText("Lon: " + String.valueOf( latLng.longitude));

        Toast.makeText(this, latLng.latitude+" "+latLng.longitude, Toast.LENGTH_SHORT).show();
    }

}
