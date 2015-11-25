package com.nuqlis.classmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MorningFragment extends Fragment {
    View v;
    ListView list;
    private ProgressDialog progress;
    private boolean isConnected;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.classroom_layout, container, false);


        return v;
    }

    @Override
    public void onStart(){
        SharedPreferences pref = this.getActivity().getSharedPreferences(getString(R.string.user_data), 0);
        UserPref user = new UserPref(pref);
        isConnected = user.IsConnected();

        if (isConnected) {
            progress = new ProgressDialog(this.getActivity(), R.style.ProgressTheme);
            progress.setMessage("กำลังดึงข้อมูล");
            progress.show();

            String staffID = user.GetStaffID();
            String hostID = user.GetHostID();

            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(String response ) {
                    GetDataCallback(response);
                }
            });

            // n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/GetTeacherCourse?year=2015&semester=1&staffId="+ staffID +"&hostId=" + hostID);
        } else {
            GetOfflineData();
        }
        super.onStart();
    }

    public void GetDataCallback (String response) {
        //Log.d("CLASS", response);
        try {
            ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>> ();
            JSONObject obj = new JSONObject(response);
            JSONArray d = obj.getJSONArray("data");

            DBHelper db = new DBHelper(this.getContext());
            db.DeleteAllClassroom();

            for(int i = 0; i < d.length(); i++){
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject o = d.getJSONObject(i);
                map.put("TITLE", o.getString("course"));
                map.put("DESC", o.getString("schoolTimeID"));
                data.add(map);
                db.InsertClassroom(o.getString("schoolTimeID"), o.getString("course"));
            }
            db.close();

            list=(ListView)v.findViewById(R.id.class_list);

            ClassAdapter adapter = new ClassAdapter(this.getActivity(), data);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("CLASS", "click " + parent.getItemAtPosition(position));
                    HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);

                    Intent i = new Intent(view.getContext(), roll_activity.class);
                    i.putExtra("schoolTimeID", map.get("DESC"));
                    i.putExtra("className", map.get("TITLE") );
                    i.putExtra("isConnected", true);
                    startActivity(i);
                }
            });

            //Toast toast = Toast.makeText(this.getContext(), obj.getString("status"), Toast.LENGTH_SHORT);
            //toast.show();
        } catch ( JSONException ex) {
            Toast toast = Toast.makeText(this.getContext(), "ERROR : 0301", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (progress.isShowing()) progress.dismiss();
    }

    public void GetOfflineData() {
        DBHelper db = new DBHelper(this.getContext());
        ArrayList<HashMap<String, String>>  data = db.GetAllClass();
        list=(ListView)v.findViewById(R.id.class_list);

        ClassAdapter adapter = new ClassAdapter(this.getActivity(), data);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CLASS", "click " + parent.getItemAtPosition(position));
                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

                Intent i = new Intent(view.getContext(), roll_activity.class);
                i.putExtra("schoolTimeID", map.get("DESC"));
                i.putExtra("className", map.get("TITLE"));
                i.putExtra("isConnected", false);
                startActivity(i);
            }
        });
        db.close();
    }
}
