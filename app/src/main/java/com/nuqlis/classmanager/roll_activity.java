package com.nuqlis.classmanager;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private Boolean isConnected;
    private String date;
    private int totalHour;
    private JSONObject userJSon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_activity);

        getSupportActionBar().setHomeButtonEnabled(true);

        progress = new ProgressDialog(this, R.style.ProgressTheme);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            schoolTimeID = b.getString("schoolTimeID");
            setTitle(b.getString("className"));
            this.isConnected = b.getBoolean("isConnected");

            if (!isConnected) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B71C1C")));
            } else {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3e72ff")));
            }
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3e72ff")));
        }
    }

    @Override
    public void onStart(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,  int monthOfYear, int dayOfMonth) {
                            date = year + "/" + (monthOfYear + 1 ) + "/" + dayOfMonth;
                            DatePickerCallback();
                    }

                }, mYear, mMonth, mDay);
        dpd.show();
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "ยกเลิก", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    onBackPressed();
                }
            }
        });

        super.onStart();
    }

    public void DatePickerCallback() {
        if (isConnected) {
            progress.setMessage("กำลังดาว์นโหลดข้อมูล");
            progress.show();

            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String response ) {
                    GetDataCallback(response);
                }
            });

            n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/GetAttendData?schoolTimeID=" + schoolTimeID + "&date=" + date);
        } else {
            Log.d("ROLL", "GET OFFLINE");
            GetOfflineData();
        }
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
                if (isConnected) {
                    userJSon = GetUserData();
                    ShowTotalHourDialog();
                } else {
                    SaveOfflineData();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }

    public void ShowTotalHourDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("จำนวนคาบ");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                totalHour = Integer.parseInt(input.getText().toString());
                SaveOnlineData();
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void SaveOnlineData() {
        try {
            userJSon.put("totalhour", totalHour);

            progress = new ProgressDialog(this, R.style.ProgressTheme);
            progress.setMessage("กำลังบันทึกข้อมูล");
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

            post.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/SaveAttendData?schoolTimeID=" + schoolTimeID + "+&date=" + date + "&hostId=" + hostID + "&staffId=" + staffID + "&data=" + URLEncoder.encode(userJSon.toString(), "UTF-8"));
            Log.d("ROLL", userJSon.toString());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ERROR : 0405", Toast.LENGTH_SHORT);
            progress.dismiss();
        }

    }

    public JSONObject GetUserData() {
        JSONObject obj = new JSONObject();
            try {

                JSONArray data = new JSONArray();
                ListView list = (ListView) findViewById(R.id.student_list);

                int count = list.getAdapter().getCount();
                Log.d("ROLL", "count" + count);
                Log.d("ROLL", "count child" + list.getChildCount());

                for (int i = 0; i < count; i++) {
                    HashMap<String, String> map = (HashMap<String, String>) list.getAdapter().getItem(i);
                    View parentView = getViewByPosition(i, list);
                    Spinner spinner = (Spinner) parentView.findViewById(R.id.attend_spinner);

                    JSONArray att = new JSONArray();
                    Log.d("ROLL-SAVE", spinner.getSelectedItem().toString());
                    if (!spinner.getSelectedItem().toString().equals("มา")){
                        att.put(spinner.getSelectedItem().toString());

                    }

                    JSONObject student = new JSONObject();
                    student.put("cid", map.get("CID"));
                    student.put("attend", att);
                    data.put(student);
                }

                obj.put("listChild", data);


            } catch (Exception e){
                Log.d("ROLL", e.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0404", Toast.LENGTH_SHORT);
                toast.show();
                if(progress.isShowing())  progress.dismiss();
            }

        return obj;
    }

    private void SaveOfflineData(){
        DBHelper db = new DBHelper(this);
        ListView list = (ListView) findViewById(R.id.student_list);

        int count = list.getAdapter().getCount();
        Log.d("ROLL", "count" + count);
        Log.d("ROLL", "count child" + list.getChildCount());

        for (int i = 0; i < count; i++) {
            HashMap<String, String> map = (HashMap<String, String>) list.getAdapter().getItem(i);
            View parentView = getViewByPosition(i, list);
            Spinner spinner = (Spinner) parentView.findViewById(R.id.attend_spinner);

            String att = spinner.getSelectedItem().toString();
            Log.d("ROLL-SAVE", att);
            db.InsertAttendData(map.get("CID"), att, date, schoolTimeID);
        }
        db.close();

        GetOfflineData();
    }

    private void SaveDataCallback(String response){
        //Log.d("ROLL", response);

        try {
            JSONObject obj = new JSONObject(response);
            if ( obj.getString("status").equals("OK")) {
                Toast.makeText(getApplicationContext(), " บันทึกเสร็จสมบูรณ์ ", Toast.LENGTH_SHORT).show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0403", Toast.LENGTH_SHORT);
                toast.show();
                DatePickerCallback();
            }
        } catch ( JSONException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "ERROR : 0402", Toast.LENGTH_LONG);
            toast.show();
        }

        if (progress.isShowing()) progress.dismiss();
    }

    public void GetDataCallback (String response) {
        //Log.d("ROLL", response);
        ListView list = (ListView) findViewById(R.id.student_list);
        try {
            ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>> ();
            JSONObject obj = new JSONObject(response);
            JSONArray d = obj.getJSONObject("data").getJSONArray("listStudent");

            for(int i = 0; i < d.length(); i++){

                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject o = d.getJSONObject(i);
                map.put("isFirst", "1");
                map.put("TITLE", o.getString("name"));
                map.put("CID", o.getString("cid"));
                if ( o.getJSONArray("attend").length() > 0) {
                    map.put("ATTEND" , o.getJSONArray("attend").getString(0));
                } else {
                    map.put("ATTEND" , "มา");
                }
                //Log.d("ROLL", map.get("ATTEND"));
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

    private void GetOfflineData() {
        DBHelper db = new DBHelper(this);
        ArrayList<HashMap<String, String>>  data = db.GetStudentInClass(schoolTimeID);
        ListView list = (ListView) findViewById(R.id.student_list);

        StudentAdapter adapter = new StudentAdapter(this, data);
        list.setAdapter(adapter);
        db.close();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
