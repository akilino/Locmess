package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
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

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

import static pt.ulisboa.tecnico.meic.cmu.locmess.R.id.map;
import static pt.ulisboa.tecnico.meic.cmu.locmess.R.id.map_information;

/**
 * Created by Akilino on 16/03/2017.
 */

public class DetailedLocation extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private MarkerOptions markerCreated;
    private Circle radiusCircle;

    private static final String LOCATION = "LOCATION";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String RADIUS = "RADIUS";
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";

    private double lat = 0;
    private double lng = 0;
    private double radius = 0;
    private String location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_location);

        Bundle extras = getIntent().getBundleExtra(BUNDLE_EXTRAS);
        location = extras.getString(LOCATION);
        lat = Double.parseDouble(extras.getString(LATITUDE));
        lng = Double.parseDouble(extras.getString(LONGITUDE));
        radius = Double.parseDouble(extras.getString(RADIUS));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map_information);
        mapFragment.getMapAsync(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(location);
        toolbar.setTitleTextColor(Color.WHITE);

        //TODO, add map to this activity, when the view button is pressed (at the Location Activity)
    }

    private void moveToMarker(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,17));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(lng,lat);

        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        mMap.clear();

        markerCreated = new MarkerOptions()
                .position(latLng)
                .title(location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        radiusCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(0x99BAFF00)
                .fillColor(0x30BAFF00)
                .strokeWidth(5f));

        radiusCircle.setVisible(true);

        Marker locationMarker = mMap.addMarker(markerCreated);
        locationMarker.showInfoWindow();

        moveToMarker(latLng);
    }
}
