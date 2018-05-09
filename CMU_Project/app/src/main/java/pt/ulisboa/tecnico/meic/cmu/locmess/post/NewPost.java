package pt.ulisboa.tecnico.meic.cmu.locmess.post;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Action;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Connection;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.MessageType;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.GPS;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.LocationActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.Wifi;
import pt.ulisboa.tecnico.meic.cmu.locmess.properties.PropertiesAdapter;
import pt.ulisboa.tecnico.meic.cmu.locmess.properties.Property;
import pt.ulisboa.tecnico.meic.cmu.locmess.tool.StringParser;

/**
 * Created by Akilino on 16/03/2017.
 */

public class NewPost extends AppCompatActivity implements PropertiesAdapter.ItemClickCallback {

    private ArrayList<Property> properties;
    private Toolbar toolbar;
    private EditText fromDateEditText, fromTimeEditText, toDateEditText, toTimeEditText, titleEditText, messageEditText;
    private String filter="Blacklist", mode="Centralized";
    private Calendar calendar;
    private Spinner locationSpinner;
    private PropertiesAdapter adapter;
    private Button addButton;
    private String helper,key,value = null;
    private Property property;

    private static final SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat  timeFormat = new SimpleDateFormat("HH:mm:ss");

    RecyclerView recyclerView;
    String[] keys;
    ArrayList<String> keysList;
    List<String> spinnerArray = new ArrayList<>();
    LocationActivity locationActivity = new LocationActivity();
    ArrayList<Location> locationList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.new_post);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        populateSpinner();

        calendar = Calendar.getInstance();

        fromDateEditText = (EditText) findViewById(R.id.fromDateEditText);
        fromTimeEditText = (EditText) findViewById(R.id.fromTimeEditText);
        toDateEditText = (EditText) findViewById(R.id.toDateEditText);
        toTimeEditText = (EditText) findViewById(R.id.toTimeEditText);
        titleEditText = (EditText) findViewById(R.id.titleTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        populateTime(calendar);

        addButton = (Button) findViewById(R.id.addButton);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewProperties);
        setupRecyclerView();

        keysList = new ArrayList<>();
        keys = getResources().getStringArray(R.array.keys);

        for(int i = 0; i < keys.length; i++)
            keysList.add(keys[i]);

        populateMessage();
        handleCalendar();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel();
        }
    };

    private void handleCalendar(){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewPost.this,date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                helper = "fromDate";
            }
        });

        toDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewPost.this,date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                helper = "toDate";
            }
        });

        fromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(NewPost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 10){
                            fromTimeEditText.setText("0" + hourOfDay + ":" + minute);
                        }else{
                            fromTimeEditText.setText(hourOfDay + ":" + minute);
                        }
                    }
                },hour,minute,true);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

            }
        });

        toTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(NewPost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 10){
                            toTimeEditText.setText("0" + hourOfDay + ":" + minute);
                        }else{
                            toTimeEditText.setText(hourOfDay + ":" + minute);
                        }
                    }
                },hour,minute,true);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

            }
        });
    }

    private void populateMessage(){

        String message = "This is for testing purposes only. Proceed with caution";
        messageEditText.setText(message);
    }

    private void populateTime(Calendar calendar){
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        fromTimeEditText.setText(currentTime);
        fromDateEditText.setText(currentDate);

        try {
            calendar.setTime(dateFormat.parse(currentDate));
            calendar.add(Calendar.DATE,1);
            String nextDayDate = dateFormat.format(calendar.getTime());

            toDateEditText.setText(nextDayDate);
            toTimeEditText.setText(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void populateSpinner(){
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

            for(int i=0, j=0;i<json.size();i++, j++){
                while(json.get("location"+j)==null) {
                    j++;
                }
                String[] result=StringParser.getLocation(json.get("location"+j).toString());

                GPS location = new GPS(result[0], result[1], result[2], result[3]);
                locationList.add(location);
            }

        }


        //TODO descentralized

        for (int i = 0; i < 2; i++) {
            Wifi location = new Wifi("Location Wifi " + i, "Mac Address" + i);
            locationList.add(location);
        }


        for(int i = 0; i < locationList.size(); i++){
            spinnerArray.add(locationList.get(i).locationName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

    }

    public void setupRecyclerView(){
        properties = new ArrayList<>();
        adapter = new PropertiesAdapter(this,properties);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItemClickCallback(this);
    }

    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        deleteItem(viewHolder.getAdapterPosition());
                    }
                };
        return simpleItemTouchCallback;
    }

    private void moveItem(int oldPos, int newPos){
        Property property = properties.get(oldPos);
        properties.remove(oldPos);
        properties.add(newPos,property);
        adapter.notifyItemMoved(oldPos,newPos);
    }

    private void deleteItem(final int position){
        properties.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_create){
            SharedPreferences sharedPref = this.getSharedPreferences("file", Context.MODE_PRIVATE);
            boolean locationExists= sharedPref.getBoolean("logged", false);
            if(locationExists==true){
                String username=sharedPref.getString("username", null);
                String startData= (fromDateEditText.getText()+" "+fromTimeEditText.getText()).trim();
                String endData=(toDateEditText.getText()+" "+toTimeEditText.getText()).trim();
                String title=titleEditText.getText().toString().trim();
                String message=messageEditText.getText().toString().trim();

                String location = locationSpinner.getSelectedItem().toString().trim();
                if(title!=null && message!=null && filter!=null && mode!=null) {

                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("title", title);
                    json.put("message", message);
                    json.put("startDate", startData);
                    json.put("endDate", endData);
                    json.put("location", location);
                    json.put("filter", filter);
                    json.put("mode", mode);


                    JSONArray arrayProperty = new JSONArray();
                    for (int j=0; j<properties.size();j++) {
                        arrayProperty.add(properties.get(j).getKey());
                        arrayProperty.add(properties.get(j).getValue());
                    }
                    json.put("property", arrayProperty.toJSONString());
                    Toast.makeText(NewPost.this, arrayProperty.toJSONString(), Toast.LENGTH_LONG).show();
                    Action action = new Action(MessageType.sendpost, json);
                    json = new Connection().execute(action);
                    if(json!=null){
                        Toast.makeText(this, "Post successfuly created!", Toast.LENGTH_SHORT).show();
                        finish();
                        return super.onOptionsItemSelected(item);
                    }
                }

            }
            Toast.makeText(this, "Fail, try again!", Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isPostComplete(){
        if(titleEditText.getText().toString().matches("")){
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        if(helper.equals("fromDate")){
            fromDateEditText.setText(simpleDateFormat.format(calendar.getTime()));
        }else if(helper.equals("toDate")){
            toDateEditText.setText(simpleDateFormat.format(calendar.getTime()));
        }

    }

    public static ArrayList<String> populateList(int numPosts){
        ArrayList<String> list = new ArrayList<>();

        for(int i = 1; i <= numPosts;i++){
            list.add("Key" + i + ": Value" + i);
        }

        return list;
    }

    public void openDialogAddProperty(View view){
        LayoutInflater li = LayoutInflater.from(NewPost.this);
        final View promptsView = li.inflate(R.layout.dialog_edit_property, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NewPost.this);

        alertDialogBuilder.setView(promptsView);

        final AutoCompleteTextView keyValue = (AutoCompleteTextView) promptsView
                .findViewById(R.id.autoCompleteTextView1);

        final ArrayAdapter<String> completeTextViewAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,keysList);
        keyValue.setAdapter(completeTextViewAdapter);

        final EditText valueValue = (EditText) promptsView
                .findViewById(R.id.valueEditText);

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Add Property")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                key = keyValue.getText().toString();
                                value = valueValue.getText().toString();

                                property = new Property(key, value);

                                if(!keysList.contains(key)){
                                    Toast.makeText(NewPost.this, "bool: " + (!Arrays.asList(keysList).contains(key)), Toast.LENGTH_SHORT).show();
                                    keysList.add(key);
                                    completeTextViewAdapter.notifyDataSetChanged();
                                }

                                Toast.makeText(NewPost.this, "value: " + key + ":" + value, Toast.LENGTH_SHORT).show();
                                properties.add(0,property);
                                adapter.notifyItemInserted(0);
                                //adapter.notifyItemInserted(properties.size());
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

    public void openDialogEditProperty(final int position){
        LayoutInflater li = LayoutInflater.from(NewPost.this);
        final View promptsView = li.inflate(R.layout.dialog_edit_property, null);

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                NewPost.this);

        alertDialogBuilder.setView(promptsView);

        final AutoCompleteTextView keyValue = (AutoCompleteTextView) promptsView
                .findViewById(R.id.autoCompleteTextView1);

        final ArrayAdapter<String> completeTextViewAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,keysList);
        keyValue.setAdapter(completeTextViewAdapter);

        final EditText valueValue = (EditText) promptsView
                .findViewById(R.id.valueEditText);

        keyValue.setText(properties.get(position).getKey());
        valueValue.setText(properties.get(position).getValue());

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Edit Property")
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                key = keyValue.getText().toString();
                                value = valueValue.getText().toString();

                                property = new Property(key, value);

                                if(!keysList.contains(key)){
                                    keysList.add(key);
                                    completeTextViewAdapter.notifyDataSetChanged();
                                }

                                Toast.makeText(NewPost.this, "value: " + key + ":" + value, Toast.LENGTH_SHORT).show();

                                properties.add(position,property);
                                properties.remove(position+1);
                                adapter.notifyDataSetChanged();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    public void openDialogViewProperty(final int position){
        LayoutInflater li = LayoutInflater.from(NewPost.this);
        final View promptsView = li.inflate(R.layout.dialog_view_property, null);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                NewPost.this);

        alertDialogBuilder.setView(promptsView);

        TextView keyValue = (TextView) promptsView
                .findViewById(R.id.textViewDialogViewKey);


        keyValue.setText(properties.get(position).getText());

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Property")
                .setPositiveButton("Edit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                openDialogEditProperty(position);
                            }
                        })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                properties.remove(position);
                                adapter.notifyItemRemoved(position);
                            }
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.whiteListRadioButton:
                if (checked)
                    filter="Whitelist";
                    break;
            case R.id.blackListRadioButton:
                if (checked)
                    filter="Blacklist";
                    break;
            case R.id.decentralizedRadioButton:
                if(checked)
                    mode="Decentralized";
                    break;
            case R.id.centralizedModeRadioButton:
                if(checked)
                    mode="Centralized";
                    break;
        }
    }

    @Override
    public void onItemClick(int p) {
        Log.d("newPost", "onStatusButtonOwnerClicked: it clicked");
        openDialogViewProperty(p);
    }
}

