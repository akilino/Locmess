package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

/**
 * Created by Akilino on 07/04/2017.
 */

// problema: quando se sai do mainpage, o BackGroundService dá erro de não conseguir dar bound e faz unbound
//           neste caso o NewWifiDirect também não consegue dar bound ao BackGroundService

// solução: vou tentar criar dois services, um para cada BroadCastReceiver

public class NewWiFiLocation extends AppCompatActivity{


    private Toolbar toolbar;
    private ArrayList<String> customNetworks;
    private LinearLayout verticalLayout,customVerticalLayout;
    private LayoutParams params;
    private RadioGroup radioGroup;
    private RadioButton radioButtonWifi,radioButtonGPS;
    private EditText editTextLocationName;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_wifi);

        Intent intent = getIntent();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.createLocation);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        verticalLayout = (LinearLayout) findViewById(R.id.switchLayout);
        customVerticalLayout = (LinearLayout) findViewById(R.id.customSwitchLayout);

        params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        customNetworks = new ArrayList<>();

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtonGPS = (RadioButton) findViewById(R.id.radioButtonGps);
        radioButtonWifi = (RadioButton) findViewById(R.id.radioButtonWifi);

        editTextLocationName = (EditText) findViewById(R.id.editText_locationName);
        editTextLocationName.setText(intent.getStringExtra("locationName"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.radioButtonGps) {
                    Toast.makeText(NewWiFiLocation.this, "GPS button clicked", Toast.LENGTH_SHORT).show();
                    createGPSActivity();
                }
            }
        });

    }

    @Override
    public void onResume(){
        //TODO
        //addAvailableNetworks();
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void createGPSActivity(){
        finish();
        Intent intent = new Intent(this, NewGPSLocation.class);
        intent.putExtra("locationName",editTextLocationName.getText().toString());
        startActivity(intent);
    }

    public void openDialogAddNetwork(View view){
        LayoutInflater li = LayoutInflater.from(NewWiFiLocation.this);
        final View promptsView = li.inflate(R.layout.dialog_add_network, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NewWiFiLocation.this);

        alertDialogBuilder.setView(promptsView);


        final EditText ssidValue = (EditText) promptsView
                .findViewById(R.id.networkSSIDEditText);

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Add Network")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                addCustomNetwork(ssidValue.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_location,menu);
        return true;
    }

    private void addCustomNetwork(String networkName){
        customNetworks.add(networkName);

        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setTag("horizontalLayout");

        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10,20,10,20);

        if(customNetworks.size() <= 1){
            params.setMargins(10,25,10,0);
            TextView textCustomNetwork = new TextView(this);
            textCustomNetwork.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            textCustomNetwork.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            textCustomNetwork.setText("Custom Networks");

            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setTag("deleteButton");
            deleteButton.setOnClickListener(click);
            deleteButton.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            horizontalLayout.addView(textCustomNetwork);
            horizontalLayout.addView(deleteButton);
            customVerticalLayout.addView(horizontalLayout,layoutParams);
        }

        Switch switchTag = new Switch(this);
        switchTag.setText(networkName);
        switchTag.setId(customNetworks.size());
        switchTag.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        switchTag.setLayoutParams(params);
        customVerticalLayout.addView(switchTag);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addAvailableNetworks(String nome, String ip){
        params.setMargins(10, 15, 10, 15);
        Switch switchTag = new Switch(this);
        switchTag.setText("Wifi name: " + nome);
        switchTag.setTag("wifi ip: " + ip);
        switchTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        switchTag.setLayoutParams(params);
        verticalLayout.addView(switchTag);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDialog();
        }
    };

    public void removeCustomNetworks(){
        customVerticalLayout.removeAllViews();
        customNetworks.clear();
    }

    public void openDialog(){
        LayoutInflater li = LayoutInflater.from(NewWiFiLocation.this);
        final View promptsView = li.inflate(R.layout.dialog_warning_delete, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NewWiFiLocation.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                removeCustomNetworks();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_create){
            Toast.makeText(this, "Location successfuly created!", Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
