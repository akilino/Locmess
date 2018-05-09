package pt.ulisboa.tecnico.meic.cmu.locmess.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;

import pt.ulisboa.tecnico.meic.cmu.locmess.inbox.InboxActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.register.RegisterActivity;
import pt.ulisboa.tecnico.meic.cmu.locmess.user.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Action;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Connection;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.MessageType;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText username;
    private EditText password;
    private User user;
    private String name;
    private String pass;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login);
        toolbar.setTitleTextColor(Color.WHITE);

        sharedPref = this.getSharedPreferences("file", Context.MODE_PRIVATE);
        boolean logged= sharedPref.getBoolean("logged", false);
        if(logged==true){
            String username=sharedPref.getString("username", null);
            Intent intent = new Intent(this, InboxActivity.class);
            Toast.makeText(this, "You are now logged in!", Toast.LENGTH_SHORT).show();
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }
    }

    /*
    public void login(View view){
        Intent intent = new Intent(this, InboxActivity.class);
        intent.putExtra("username", "Sheng");
        startActivity(intent);
    }
*/


    public void login(View view) throws UnsupportedEncodingException {
        username = (EditText) findViewById(R.id.editText_username_login);
        password = (EditText) findViewById(R.id.editText_password_login);

        authenticateUser();
    }


    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /*
    public boolean authenticateUser(){
        Toast.makeText(this, "You are now logged in!", Toast.LENGTH_SHORT).show();
        username.getText().clear();
        password.getText().clear();
        Intent intent = new Intent(this, InboxActivity.class);
        startActivity(intent);
        return true;
    }

    */


    public void authenticateUser() throws UnsupportedEncodingException {
        if (username.getText().toString().matches("") || password.getText().toString().matches("")) {
            showAlert("Enter your credentials.");
            return;
        }
        JSONObject json=new JSONObject();
        json.put("username", username.getText().toString());
        json.put("password", password.getText().toString());
        Action action =new Action(MessageType.login, json);
        json=new Connection().execute(action);
        if(json!=null) {
            if(!json.get("sessionid").equals("")) {
                Intent intent = new Intent(this, InboxActivity.class);
                Toast.makeText(this, "You are now logged in!", Toast.LENGTH_SHORT).show();
                user = new User(json.get("username").toString(), json.get("password").toString().getBytes("UTF-8"));
                //TODO service backgroud thread
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
                sharedPref = this.getSharedPreferences("file", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("logged", true);
                editor.putString("username", json.get("username").toString());
                editor.putString("sessionid", json.get("sessionid").toString());
                editor.commit();
                finish();
                return;
            }
            showAlert("User already logged in!");
            return;
        }
        showAlert("Credentials error.");
        return;
    }


    public void showAlert(String message) {

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();

    }

}
