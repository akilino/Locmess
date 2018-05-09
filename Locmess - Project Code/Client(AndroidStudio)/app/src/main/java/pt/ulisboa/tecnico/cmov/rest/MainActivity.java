package pt.ulisboa.tecnico.cmov.rest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import org.json.simple.JSONObject;

import pt.ulisboa.tecnico.cmov.rest.tool.Action;
import pt.ulisboa.tecnico.cmov.rest.tool.MessageType;

public class MainActivity extends AppCompatActivity {


    private Connection connection = new Connection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        JSONObject json=new JSONObject();
        json.put("username", "arlindo");
        json.put("password", "abc123");
        Action action =new Action(MessageType.CREATE, json);
        Log.d("Test1",json.toJSONString());
        json=connection.execute(action);
        if(json!=null){
            Log.d("Test2",json.toJSONString());
            Toast.makeText(this, action.getType()+" : "+json.toJSONString(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, action.getType()+" : it's already created", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
