package com.nuqlis.classmanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class main extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private ProgressDialog progress;
    private ArrayList<String> schoolTimeList;
    private int schoolTimeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        SharedPreferences pref = this.getSharedPreferences(getString(R.string.user_data), 0);
        UserPref user = new UserPref(pref);
        user.SetConnectivity(isConnected);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), isConnected);



        if (isConnected) {
            progress = new ProgressDialog(this, R.style.ProgressTheme);
            progress.setMessage("กำลังดาว์นโหลดข้อมูล");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();

            PreFetchData();
        }
    }

    public void PreFetchData () {
        SharedPreferences pref = this.getSharedPreferences(getString(R.string.user_data), 0);
        UserPref user = new UserPref(pref);

        String staffID = user.GetStaffID();
        String hostID = user.GetHostID();
        Log.d("MAIN", user.IsConnected() + "");
        if (user.IsConnected()) {
            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(String response ) {
                    ClassroomDataCallback(response);
                }
            });
            n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/GetTeacherCourse?year=2015&semester=1&staffId="+ staffID +"&hostId=" + hostID);
        }
    }

    private void ClassroomDataCallback(String response) {
        Log.d("MAIN", response);
        try {
            JSONObject obj = new JSONObject(response);
            if(obj.getString("status").equals("OK")) {
                JSONArray data = obj.getJSONArray("data");

                DBHelper db = new DBHelper(this);

                db.DeleteAllClassroom();
                db.DeleteAllStudent();

                schoolTimeList = new ArrayList<String>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject o = data.getJSONObject(i);
                    db.InsertClassroom(o.getString("schoolTimeID"), o.getString("course"));
                    schoolTimeList.add(o.getString("schoolTimeID"));
                }
                Log.d("COUNT", db.CountClassroom() + "");
                db.close();
                progress.setMax(schoolTimeList.size());
                FetchStudentData();
            }
        } catch (Exception e) {
            if (progress != null) {
                progress.dismiss();
            }
            Log.d("MAIN", e.getMessage());
            Toast.makeText(getApplicationContext(), "ERROR : 0201", Toast.LENGTH_SHORT).show();
        }
    }

    private void FetchStudentData () {
        if (schoolTimeIndex > schoolTimeList.size() - 1) {
            if (progress != null) {
                progress.dismiss();
                DBHelper db = new DBHelper(this);
                Log.d("COUNT", db.CountStudent() + "");
            }
        } else {
            String schoolTimeID = schoolTimeList.get(schoolTimeIndex);
            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(String response ) {
                    FetchStudentDataCallback(response);
                }
            });
            n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/GetAttendData?schoolTimeID=" + schoolTimeID + "&date=2015/09/03");
        }
    }

    private void FetchStudentDataCallback (String response) {
        Log.d("Student", response);
        try {
            JSONObject obj = new JSONObject(response);
            if(obj.getString("status").equals("OK")) {
                JSONObject d = obj.getJSONObject("data");
                JSONArray data = d.getJSONArray("listStudent");
                Log.d("LIST", data.toString());

                DBHelper db = new DBHelper(this);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject o = data.getJSONObject(i);
                    db.InsertStudent(o.getString("cid"), o.getString("name"), o.getInt("number"), o.getInt("studentId"), o.getString("gender"), schoolTimeList.get(schoolTimeIndex));
                    Log.d("MAIN", o.getString("cid"));
                }
                db.close();
                FetchStudentData();
            } else {
                Toast.makeText(getApplicationContext(), "ERROR : 0202", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MAIN", e.getMessage());
        }

        int percentage = schoolTimeIndex + 1 ;
        schoolTimeIndex++;
        progress.setProgress(percentage);
        FetchStudentData();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch ( position){
            case 0:
                fragment = new sync_fragment();
                mTitle = "ซิงค์ข้อมูล";
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                break;
            case 1:
                fragment = new classroom_fragment();
                mTitle = "ห้องเรียน";
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                break;
            case 2:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                SharedPreferences pref = getSharedPreferences(getString(R.string.user_data), 0);
                                UserPref user = new UserPref(pref);

                                user.ClearUserPref();
                                Intent i = new Intent(getApplicationContext(), login.class);
                                startActivity(i);
                                main.this.finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemedDialog);

                builder.setMessage(" ต้องการออกจากระบบ ?").setPositiveButton("ใช่", dialogClickListener)
                        .setNegativeButton("ไม่ใช่", dialogClickListener).show();
            default:
                fragment = new sync_fragment();
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


}
