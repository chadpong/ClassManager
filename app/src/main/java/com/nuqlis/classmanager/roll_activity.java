package com.nuqlis.classmanager;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class roll_activity extends AppCompatActivity {
    private String schoolTimeID;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_activity);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3e72ff")));

        Bundle b = getIntent().getExtras();
        if (b != null) {
            schoolTimeID = b.getString("schoolTimeID");

            setTitle(b.getString("className"));
        }
    }

    @Override
    public void onStart(){
        progress = new ProgressDialog(this, R.style.ProgressTheme);
        progress.setMessage("กำลังดึงข้อมูล");
        progress.show();

        NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(String response ) {
                GetDataCallback(response);
            }
        });

        n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/GetAttendData?schoolTimeID=" + schoolTimeID + "&date=2015/09/03");
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_roll_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_save_roll:
                SaveAttendData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void SaveAttendData() {
        progress = new ProgressDialog(this, R.style.ProgressTheme);
        progress.setMessage("กำลังบันทึกข้อมูล");

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,  int monthOfYear, int dayOfMonth) {
                        try {
                            progress.show();
                            NetUtils post = new NetUtils(new NetUtils.OnTaskCompleted() {
                                @Override
                                public void onTaskCompleted(String response) {
                                    SaveDataCallback(response);
                                }
                            });

                            SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.user_data), 0);
                            UserPref user = new UserPref(pref);

                            String staffID = user.GetStaffID();
                            String hostID = user.GetHostID();


                            JSONObject obj = new JSONObject();
                            obj.put("totalhour", 1);

                            JSONArray data = new JSONArray();

                            ListView list = (ListView) findViewById(R.id.student_list);
                            int count = list.getAdapter().getCount();

                            for (int i = 0; i < count; i++) {
                                HashMap<String, String> map = (HashMap<String, String>) list.getAdapter().getItem(i);
                                Spinner spinner = (Spinner) list.getChildAt(i).findViewById(R.id.attend_spinner);

                                JSONArray att = new JSONArray();
                                if (!spinner.getSelectedItem().toString().equals("มา")){
                                    att.put(spinner.getSelectedItem().toString());
                                }

                                JSONObject student = new JSONObject();
                                student.put("cid", map.get("CID"));
                                student.put("attend", att);
                                data.put(student);
                            }

                            obj.put("listChild", data);

                            String date = year + "/" + (monthOfYear + 1 ) + "/" + dayOfMonth;
                            post.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/SaveAttendData?schoolTimeID=" + schoolTimeID + "+&date=" + date + "&hostId=" + hostID + "&staffId=" + staffID + "&data=" + URLEncoder.encode(obj.toString(), "UTF-8"));
                            Log.d("ROLL", obj.toString());
                            Log.d("ROLL", date);

                        } catch (Exception e){
                            Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0404", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                }, mYear, mMonth, mDay);
        dpd.show();

    }

    private void SaveDataCallback(String response){
        Log.d("ROLL", response);

        try {
            JSONObject obj = new JSONObject(response);
            if ( obj.getString("status").equals("OK")) {
                Toast.makeText(getApplicationContext(), " บันทึกเสร็จสมบูรณ์ ", Toast.LENGTH_SHORT).show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0403", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch ( JSONException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0402", Toast.LENGTH_LONG);
            toast.show();
        }
        if (progress.isShowing()) progress.dismiss();
    }

    public void GetDataCallback (String response) {

        ListView list = (ListView) findViewById(R.id.student_list);
        try {

            ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>> ();
            JSONObject obj = new JSONObject(response);
            JSONArray d = obj.getJSONObject("data").getJSONArray("listStudent");

            for(int i = 0; i < d.length(); i++){

                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject o = d.getJSONObject(i);
                map.put("TITLE", o.getString("name"));
                map.put("CID", o.getString("cid"));
                data.add(map);
            }

            StudentAdapter adapter = new StudentAdapter(this, data);
            list.setAdapter(adapter);


            //Toast toast = Toast.makeText(this, obj.getString("status"), Toast.LENGTH_SHORT);
            //toast.show();
        } catch ( JSONException ex) {
            Toast toast = Toast.makeText(this, "ERROR : 0401", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (progress.isShowing()) progress.dismiss();
    }

}
