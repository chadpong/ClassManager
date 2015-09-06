package com.nuqlis.classmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;



public class login extends AppCompatActivity {
    private ProgressDialog progress;
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

    public void DoLogin(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        EditText username = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
        String uname = username.getText().toString();
        String pwd = password.getText().toString();

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            progress = new ProgressDialog(this, R.style.ProgressTheme);
            progress.setMessage("กำลังเข้าสู่ระบบ");
            progress.show();

            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(String response ) {
                    validateCredentials(response);
                }
            });

            n.execute("http://lcmservicetest.azurewebsites.net/servicecontrol/service.svc/login?username=" + uname + "&password=" + pwd + "&src=03");

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateCredentials(String jsonString) {

        try {
            JSONObject obj = new JSONObject(jsonString);
            if ( obj.getString("Status").equals("OK")) {
                SharedPreferences pref = getSharedPreferences(getString(R.string.user_data), 0);
                UserPref user = new UserPref(pref);

                user.Login(obj.getJSONObject("data").getString("StaffID"), obj.getJSONObject("data").getString("HostID"));

                Intent i = new Intent(getApplicationContext(), main.class);
                startActivity(i);
                login.this.finish();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "ชื่อผู้ใช้หรือรหัสผ่านผิดพลาด", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch ( JSONException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0101", Toast.LENGTH_LONG);
            toast.show();
        }
        if (progress != null) {
            progress.dismiss();
        }
    }
}
