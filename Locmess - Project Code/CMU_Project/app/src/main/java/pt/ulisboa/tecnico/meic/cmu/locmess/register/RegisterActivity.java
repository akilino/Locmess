package pt.ulisboa.tecnico.meic.cmu.locmess.register;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Action;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.Connection;
import pt.ulisboa.tecnico.meic.cmu.locmess.connection.MessageType;
import pt.ulisboa.tecnico.meic.cmu.locmess.user.User;

/**
 * Created by Akilino on 09/03/2017.
 */

public class RegisterActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private Button register;
    private EditText username;
    private EditText password;
    private EditText retypePassword;
    private Map<String, String> userMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();
    }

    public void initialize(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.button_register);
        toolbar.setTitleTextColor(Color.WHITE);

        register = (Button) findViewById(R.id.button_register_register);
        username = (EditText) findViewById(R.id.editText_username_register);
        password = (EditText) findViewById(R.id.editText_password_register);
        retypePassword = (EditText) findViewById(R.id.editText_password_retype_register);
    }

    public void registerButton(View view) throws UnsupportedEncodingException {

        if(verifyRegistration()){
            finish();
        }
    }
    public void cancel(View view){

        finish();
    }

    public boolean verifyRegistration() throws UnsupportedEncodingException {
        boolean isUsernameValid = false;
        boolean isPasswordValid = false;

        if (!username.getText().toString().matches("") )
            isUsernameValid = true;

        if (password.getText().toString().matches(retypePassword.getText().toString()) && !password.getText().toString().matches(""))
            isPasswordValid = true;
            if (isPasswordValid && isUsernameValid) {
                JSONObject json=new JSONObject();
                json.put("username", username.getText().toString());
                json.put("password", password.getText().toString());
                Action action =new Action(MessageType.create, json);
                json=new Connection().execute(action);
                if(json!=null) {
                    User user = new User(json.get("username").toString(), json.get("password").toString().getBytes("UTF-8"));
                    Toast.makeText(this, "You have been successfully registered!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!isPasswordValid && !isUsernameValid) {
                Toast.makeText(this, "Please, enter your details first.", Toast.LENGTH_SHORT).show();
                return false;
            }else if(password.getText().toString().matches("")) {
                Toast.makeText(this, "Please, enter a password.", Toast.LENGTH_SHORT).show();
                return false;
            }else if(retypePassword.getText().toString().matches("")){
                Toast.makeText(this, "Please, retype your password.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!isPasswordValid) {
                Toast.makeText(this, "The password does not match.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!isUsernameValid) {
                Toast.makeText(this, "Please, enter your username", Toast.LENGTH_SHORT).show();
                return false;
            }
            return false;
    }
}

