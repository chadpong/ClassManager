package com.nuqlis.classmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class sync_fragment extends Fragment {
    View v;
    Button btn;
    ProgressDialog progress;
    ArrayList<HashMap<String,Object>> unsyncRows;
    Integer totalUnsync = 0;
    Integer sync_loop = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.sync_layout, container, false);

        DBHelper db = new DBHelper(this.getContext());
        totalUnsync = db.UnsyncCount();

        TextView text = (TextView) v.findViewById(R.id.sync_count);
        text.setText(totalUnsync + "");

        btn = (Button) v.findViewById(R.id.syncButton);
        btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                Context ctx = getActivity().getApplicationContext();

                ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

                    /*
                    progress = new ProgressDialog(ctx, R.style.ProgressTheme);
                    progress.setMessage("กำลังอัพโหลด");
                    progress.show();
                    */

                    DBHelper db = new DBHelper(ctx);
                    unsyncRows = db.GetUnsyncRow();

                    NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                        @Override
                        public void onTaskCompleted(String response ) {
                            callback(response);
                        }
                    });

                    sync_loop = 0;

                    HashMap<String, Object> map = unsyncRows.get(sync_loop);
                    String cid = ((String)map.get("CID"));
                    String schoolTimeID = map.get("schoolTimeID").toString();
                    String date = map.get("attendDate").toString();
                    String att = map.get("attendType").toString();
                    int totalHour = Integer.parseInt(map.get("count").toString());

                    SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.user_data), 0);
                    UserPref user = new UserPref(pref);

                    String staffID = user.GetStaffID();
                    String hostID = user.GetHostID();

                    try {
                        JSONObject data = new JSONObject();
                        data.put("totalhour", totalHour);

                        JSONArray listChild = new JSONArray();
                        JSONObject child = new JSONObject();
                        child.put("cid", cid);

                        JSONArray attend = new JSONArray();
                        String[] split = att.split(",");

                        for(int i = 0; i < split.length; i++) {
                            attend.put(split[i]);
                        }

                        child.put("attend", attend);
                        listChild.put(child);
                        data.put("listChild", listChild);

                        Log.e("LOG LOG LOG", "http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/SaveAttendData?schoolTimeID=" + schoolTimeID + "+&date=" + date + "&hostId=" + hostID + "&staffId=" + staffID + "&data=" + URLEncoder.encode(data.toString(), "UTF-8"));
                        n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/SaveAttendData?schoolTimeID=" + schoolTimeID + "+&date=" + date + "&hostId=" + hostID + "&staffId=" + staffID + "&data=" + URLEncoder.encode(data.toString(), "UTF-8"));

                    } catch (Exception e) {
                        Toast.makeText(ctx, "ERROR : 0602", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ctx, "ไม่มีการเชื่อมต่ออินเตอร์เน็ต", Toast.LENGTH_SHORT).show();
                }
                //http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/SaveAttendData?schoolTimeID=43756&date=2015/09/06&hostId=3720140208&staffId=201403&data={%22totalhour%22:%222%22,%22listChild%22:[{%22cid%22:%224241430000000%22,%22attend%22:[%22%E0%B8%A5%E0%B8%B2%22,%22%E0%B8%A5%E0%B8%B2%22]}]}
            }
        });

        return v;
    }

    private void callback (String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if ( obj.getString("status").equals("OK")) {
                HashMap<String, Object> map = unsyncRows.get(sync_loop);
                DBHelper db = new DBHelper(getActivity().getApplicationContext());
                db.UpdateSyncedRow(map.get("CID").toString(), map.get("schoolTimeID").toString());
            }

        } catch ( JSONException ex) {
            Log.e("LOG LOG LOG", response);
            Toast.makeText(getActivity().getApplicationContext(), "ERROR : 0601 ", Toast.LENGTH_SHORT).show();
        }

        sync_loop++;

        if(sync_loop < totalUnsync) {
            NetUtils n = new NetUtils(new NetUtils.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(String response ) {
                    callback(response);
                }
            });

            HashMap<String, Object> map = unsyncRows.get(sync_loop);
            String cid = map.get("CID").toString();
            String schoolTimeID = map.get("schoolTimeID").toString();
            String date = map.get("attendDate").toString();
            String att = map.get("attendType").toString();
            int totalHour = Integer.parseInt(map.get("count").toString());

            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.user_data), 0);
            UserPref user = new UserPref(pref);

            String staffID = user.GetStaffID();
            String hostID = user.GetHostID();

            try {
                JSONObject data = new JSONObject();
                data.put("totalhour", totalHour);

                JSONArray listChild = new JSONArray();
                JSONObject child = new JSONObject();
                child.put("cid", cid);

                JSONArray attend = new JSONArray();
                String[] split = att.split(",");

                for(int i = 0; i < split.length; i++) {
                    attend.put(split[i]);
                }

                child.put("attend", attend);
                listChild.put(child);
                data.put("listChild", listChild);

                n.execute("http://newtestnew.azurewebsites.net/ServiceControl/Service.svc/SaveAttendData?schoolTimeID=" + schoolTimeID + "+&date=" + date + "&hostId=" + hostID + "&staffId=" + staffID + "&data=" + URLEncoder.encode(data.toString(), "UTF-8"));

            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "ERROR : 0603", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(getActivity().getApplicationContext(), "การอัพโหลดเสร็จสมบูรณ์", Toast.LENGTH_SHORT).show();
            if (progress != null) progress.dismiss();
        }

    }


}
