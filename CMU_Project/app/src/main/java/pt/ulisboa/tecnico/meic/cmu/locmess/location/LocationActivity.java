package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Action;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Connection;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.MessageType;
import pt.ulisboa.tecnico.meic.cmu.locmess.tool.StringParser;

/**
 * Created by Akilino on 06/04/2017.
 */

public class LocationActivity extends AppCompatActivity implements LocationAdapter.ItemClickCallback {

    private Toolbar toolbar;
    private ArrayList<Location> locations;
    private LocationAdapter adapter;
    public ArrayList<Location> list;
    private static final String LOCATION = "LOCATION";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String RADIUS = "RADIUS";
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.locations);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLocation);

        SharedPreferences sharedPref = this.getSharedPreferences("file", Context.MODE_PRIVATE);
        locations = populateView();

        adapter = new LocationAdapter(this, locations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItemClickCallback(this);
    }

    public ArrayList<Location> populateView() {
        list = new ArrayList<>();
        SharedPreferences sharedPref = this.getSharedPreferences("file", Context.MODE_PRIVATE);
        boolean locationExists= sharedPref.getBoolean("location", false);
        if(locationExists==true){
            String lat=sharedPref.getString("latitude", null);
            String lon=sharedPref.getString("longitude", null);
            JSONObject json = new JSONObject();
            json.put("latitude", lat);
            json.put("longitude", lon);
            Action action = new Action(MessageType.checklocation, json);
            json = new Connection().execute(action);
            JSONArray msg;
            for(int i=0, j=0;i<json.size();i++, j++){
                while(json.get("location"+j)==null) {
                    j++;
                }
                String[] result=StringParser.getLocation(json.get("location"+j).toString());
                //Toast.makeText(this,result[0]+" : "+result[3], Toast.LENGTH_SHORT).show();

                GPS location = new GPS(result[0], result[1], result[2], result[3]);
                list.add(location);
            }

        }

        for (int i = 0; i < 2; i++) {
            Wifi location = new Wifi("Location Wifi " + i, "Mac Address" + i);
            list.add(location);
        }
        return list;
    }

    @Override
    public void onItemClick(int p) {
        Toast.makeText(this, "Clicked on primary position " + p, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, DetailedLocation.class);

        if(list.get(p) instanceof GPS){
            GPS gpsLocation = (GPS) list.get(p);

            Bundle extras = new Bundle();
            extras.putString(LOCATION,gpsLocation.getName());
            extras.putString(LATITUDE, gpsLocation.getLat());
            extras.putString(LONGITUDE,gpsLocation.getLon());
            extras.putString(RADIUS,gpsLocation.getRadius());
            intent.putExtra(BUNDLE_EXTRAS,extras);

        }
        startActivity(intent);
    }

    @Override
    public void onSecondaryIconClick(int p) {
        Toast.makeText(this, "Clicked on secondary position " + p, Toast.LENGTH_SHORT).show();
        openDialog(p);
    }

    public void removeLocation(int position){
        //TODO

        locations.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void openDialog(final int position) {
        LayoutInflater li = LayoutInflater.from(LocationActivity.this);
        final View promptsView = li.inflate(R.layout.dialog_warning_delete_location, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LocationActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeLocation(position);
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

}
