package com.nuqlis.classmanager;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;



public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = getSharedPreferences(getString(R.string.user_data), 0);
        UserPref user = new UserPref(pref);
        if (user.IsLoggedIn()) {
            Intent i = new Intent(getApplicationContext(), main.class);
            startActivity(i);
            login.this.finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void validateCredentials(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            if ( obj.getString("Status").equals("OK")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT);
                toast.show();

                SharedPreferences pref = getSharedPreferences(getString(R.string.user_data), 0);
                UserPref user = new UserPref(pref);
                user.Login("", "");

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Wrong User name or Password", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch ( JSONException ex) {

        }//
    }

    public void doLogin(View v) {
        EditText username = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
        String uname = username.getText().toString();
        String pwd = password.getText().toString();

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {

            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(String response ) {
                    validateCredentials(response);
                }
            });

            n.execute("http://lcmservicetest.azurewebsites.net/servicecontrol/service.svc/login?username=" + uname + "&password=" + pwd + "&src=03");

            /*
            Intent i = new Intent(getApplicationContext(), main.class);
            startActivity(i);

            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
            */
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
