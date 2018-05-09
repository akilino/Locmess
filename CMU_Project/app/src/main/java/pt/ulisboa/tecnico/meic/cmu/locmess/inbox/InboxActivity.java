package pt.ulisboa.tecnico.meic.cmu.locmess.inbox;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.meic.cmu.locmess.NotificationActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Action;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Connection;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.MessageType;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.LocationActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.location.NewGPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.login.LoginActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.post.DetailedPost;
import pt.ulisboa.tecnico.meic.cmu.locmess.post.NewPost;
import pt.ulisboa.tecnico.meic.cmu.locmess.post.NotesAdapter;
import pt.ulisboa.tecnico.meic.cmu.locmess.post.Post;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.BackgroundService;
import pt.ulisboa.tecnico.meic.cmu.locmess.storage.FileStorage;
import pt.ulisboa.tecnico.meic.cmu.locmess.tool.StringParser;
import pt.ulisboa.tecnico.meic.cmu.locmess.user.UserProfile;

/**
 * Created by Akilino on 09/03/2017.
 */

public class InboxActivity extends AppCompatActivity implements NotesAdapter.ItemClickCallback, SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener{

    private Collection<SimWifiP2pDevice> devices;

	private boolean mBound=false;
    private static final int PORT=10001;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;

    private int temp = 0;

    public static final String mBroadcastDevices = "broadcast.devices";
    public static final String mBroadcastPosts = "broadcast.post";
    private IntentFilter mIntentFilter;

    private static final String MESSAGE = "MESSAGE";
    private static final String SENDER = "SENDER";
    private static final String LOCATION = "LOCATION";
    private static final String MODE = "MODE";
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";


    private ArrayList<Post> posts=new ArrayList<Post>();
    private Toolbar toolbar;
    private NotesAdapter notesAdapter;
    private FloatingActionButton floatingActionButton,floatingActionButtonCompass,floatingActionButtonPost;
    private String username;

    private FileStorage fileStorage;


    RecyclerView recyclerView;

    Animation FabOpen, FabClose, FabRClockwise, FabRAntiClockwise;
    boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        fileStorage=new FileStorage(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_page);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButtonCompass = (FloatingActionButton) findViewById(R.id.floatingActionButtonCompass);
        floatingActionButtonPost = (FloatingActionButton) findViewById(R.id.floatingActionButtonPost);

        handleFloatingActionButton();

/*
        Post newPost = null;
        try {
            newPost = new Post("Tes98ting", "Testing", "Testing", "Testing", "Testing");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        readPosts(this);
        writePost(this, newPost);
        readPosts(this);
        Log.d("HashMap:", "Size: " + String.valueOf(postHashMap.size()));
*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            if(mSrvSocket!=null)
                mSrvSocket.close();
            if(mCliSocket!=null)
                mCliSocket.close();
            mBound = false;
            mCliSocket=null;
            mSrvSocket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
        Intent stopIntent = new Intent(InboxActivity.this,
                BackgroundService.class);
        stopService(stopIntent);

    }



    @Override
    public void onResume(){
        super.onResume();
        posts=new ArrayList<>();
        setupRecyclerView();



        SimWifiP2pSocketManager.Init(getApplicationContext());

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastDevices);
        mIntentFilter.addAction(mBroadcastPosts);

        mIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        mIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
        registerReceiver(mReceiver, mIntentFilter);

        Intent intent1 = new Intent(getApplicationContext(), SimWifiP2pService.class);
        getApplicationContext().bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);

        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
			mBound = true;
            if(mChannel!=null&&mManager!=null)
                Log.d("Test", "WifiDirect channel works: "+mManager.toString()+ " : "+mChannel.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
			mBound = false;
        }
    };


    private void setupRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPref = this.getSharedPreferences("file", Context.MODE_PRIVATE);
        boolean logged= sharedPref.getBoolean("logged", false);

        if(logged==true){
            String username=sharedPref.getString("username", null);
            String lat=sharedPref.getString("latitude", null);
            String lon=sharedPref.getString("longitude", null);
            posts = getPostList(username, lat, lon);
            notesAdapter = new NotesAdapter(this, posts);
            recyclerView.setAdapter(notesAdapter);
            notesAdapter.setItemClickCallback(this);
        }

    }

    private ArrayList<Post> getPostList(String username, String lat, String lon){
        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("latitude", lat);
        json.put("longitude", lon);

        Action action = new Action(MessageType.checkpost, json);
        json = new Connection().execute(action);
        if(json!=null){
            for(int i=0; json.get("post"+i)!=null;i++) {

                String[] result= StringParser.getPost(json.get("post"+i).toString());
                try {
                    posts.add(new Post(result[0].trim(), result[1].trim(), result[5].trim(), result[2].trim(), result[7].trim()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return posts;
    }

    private void handleFloatingActionButton(){
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabRAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen){
                    closeActionButtons();

                    isOpen = false;
                }else{
                    openActionButtons();

                    isOpen = true;
                }
            }
        });

        floatingActionButtonCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActionButtons();
                isOpen = false;
                addLocation();
            }
        });

        floatingActionButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActionButtons();
                isOpen = false;
                createPost();
            }
        });
    }

    public void closeActionButtons(){
        floatingActionButtonPost.startAnimation(FabClose);
        floatingActionButtonCompass.startAnimation(FabClose);
        floatingActionButton.startAnimation(FabRAntiClockwise);

        floatingActionButtonPost.setClickable(false);
        floatingActionButtonCompass.setClickable(false);
    }

    public void openActionButtons(){
        floatingActionButtonPost.startAnimation(FabOpen);
        floatingActionButtonCompass.startAnimation(FabOpen);
        floatingActionButton.startAnimation(FabRClockwise);

        floatingActionButtonPost.setClickable(true);
        floatingActionButtonCompass.setClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_logout){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.logout);
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //locationService.stopUsingGPS();
                            logout();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }else if(id == R.id.action_profile){
            Intent intent = new Intent(InboxActivity.this, UserProfile.class);
            intent.putExtra("username",username);
            InboxActivity.this.startActivity(intent);
        }else if(id == R.id.action_locations){
            Intent intent = new Intent(InboxActivity.this, LocationActivity.class);
            InboxActivity.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        SharedPreferences sharedPref = getSharedPreferences("file", Context.MODE_PRIVATE);
        String username=sharedPref.getString("username", null);
        String sessionid=sharedPref.getString("sessionid", null);
        JSONObject json=new JSONObject();
        json.put("username", username);
        json.put("sessionid", sessionid);
        Action action =new Action(MessageType.logout, json);
        json=new Connection().execute(action);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("logged", false);
        editor.putString("username", null);
        editor.putString("sessionid", null);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        Toast.makeText(this, "You are now logged out!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }


    public void createPost(){
        Intent intent = new Intent(InboxActivity.this, NewPost.class);
        InboxActivity.this.startActivity(intent);
    }

    public void addLocation(){
        Intent intent = new Intent(InboxActivity.this, NewGPSLocation.class);
        InboxActivity.this.startActivity(intent);
    }

    public void openDialog(final int position, int layout) {
        LayoutInflater li = LayoutInflater.from(InboxActivity.this);
        final View promptsView = li.inflate(layout, null);


        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                InboxActivity.this);

        alertDialogBuilder.setView(promptsView);

        //TODO change the onClick so that deletes/unpost the post

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(temp == 1){
                                    Log.d("Owner Click: ", "onClick: Success");
                                    unpostPost(position);
                                }else{
                                    deletePost(position);
                                    Log.d("User Click: ", "onClick: Success");
                                }

                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void unpostPost(int position){
        //TODO
        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("message", posts.get(position).getMessage());
        json.put("title", posts.get(position).getTitle());
        json.put("location", posts.get(position).getLocation());

        Action action = new Action(MessageType.deletepost, json);
        json = new Connection().execute(action);
        if(json!=null){
            for(int i=0; json.get("post"+i)!=null;i++) {
                String[] result= StringParser.getPost(json.get("post"+i).toString());
                try {
                    posts.add(new Post(result[0], result[1], result[5], result[2], result[7]));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        posts.remove(position);
        notesAdapter.notifyItemRemoved(position);
    }

    public void deletePost(int position){
        //TODO delete local post, not from the server.
    }

    @Override
    public void onStatusButtonOwnerClicked(int p) {

        temp = 1;
        openDialog(p,R.layout.dialog_warning_unpost_post);

    }

    @Override
    public void onViewButtonOwnerClicked(int p) {
        Intent intent = new Intent(this, DetailedPost.class);

        Bundle extras = new Bundle();
        extras.putString(MESSAGE,posts.get(p).getMessage());
        extras.putString(SENDER,posts.get(p).getUser());
        extras.putString(LOCATION,posts.get(p).getLocation());
        extras.putString(MODE,posts.get(p).getMode());

        intent.putExtra(BUNDLE_EXTRAS,extras);
        startActivity(intent);
    }

    @Override
    public void onViewButtonUserClicked(int p) {
        Intent intent = new Intent(this, DetailedPost.class);

        Bundle extras = new Bundle();
        extras.putString(MESSAGE,posts.get(p).getMessage());
        extras.putString(SENDER,posts.get(p).getUser());
        extras.putString(LOCATION,posts.get(p).getLocation());
        extras.putString(MODE,posts.get(p).getMode());

        intent.putExtra(BUNDLE_EXTRAS,extras);
        startActivity(intent);
    }

    @Override
    public void onStatusButtonUserClicked(int p) {

        temp = 0;
        openDialog(p,R.layout.dialog_warning_delete_post);

    }

    public void termite(View view) {

        if(mChannel!=null) {
            mManager.requestPeers(mChannel, (SimWifiP2pManager.PeerListListener) view.getContext());
            mManager.requestGroupInfo(mChannel, (SimWifiP2pManager.GroupInfoListener) view.getContext());
        }
		NotificationActivity.showNotification(this);

    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        	// This action is triggered when the Termite service changes state:
        	// - creating the service generates the WIFI_P2P_STATE_ENABLED event
        	// - destroying the service generates the WIFI_P2P_STATE_DISABLED event

            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
        		Toast.makeText(getApplicationContext(), "WiFi Direct enabled",
        				Toast.LENGTH_SHORT).show();
            } else {
        		Toast.makeText(getApplicationContext(), "WiFi Direct disabled",
        				Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

        	Toast.makeText(getApplicationContext(), "Peer list changed",
    				Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {

        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	ginfo.print();
    		Toast.makeText(getApplicationContext(), "Network membership changed",
    				Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	ginfo.print();
    		Toast.makeText(getApplicationContext(), "Group ownership changed",
    				Toast.LENGTH_SHORT).show();
        }
            else if (action.equals(mBroadcastDevices)) {
                Log.d("Receiving Device", "Received in main: "+intent.getStringExtra("Device").toString());
            } else if (action.equals(mBroadcastPosts)) {
                Log.d("Receiving Post", "Received in main: "+intent.getStringExtra("Post").toString());
            }

        }
    };


    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        // compile list of network members

        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
            if(mBound) {
                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device.getVirtIp().trim());
            }
        }

    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        //TODO, to much garbage
        boolean exist=false;
        if(devices!=null){
            if(devices.equals(peers.getDeviceList())){
                return;
            }
            for (SimWifiP2pDevice device : peers.getDeviceList()) {
                for(SimWifiP2pDevice device2 : devices){
                    if(device.equals(device2)) {
                        exist=true;
                        break;
                    }
                }
                if(!exist){
                    Log.d("TestExist", "não existe");
					if(mBound);
                    //new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device.getVirtIp());
                }
                exist=false;

            }
            devices=peers.getDeviceList();
        }else{

            for (SimWifiP2pDevice device : peers.getDeviceList()) {
				if(mBound);
                //new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device.getVirtIp());
            }
            devices=peers.getDeviceList();
        }
    }

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                mSrvSocket = new SimWifiP2pSocketServer(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            fileStorage.receivePost(values[0]);
            Log.d("Received", "Received Posts: "+values[0]);
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0], PORT);
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                Log.d("Erro", "Out: "+result);
            }else {
                new SendCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    public class SendCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... msg) {
            try {
                mCliSocket.getOutputStream().write(fileStorage.getPost().getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

    }
}