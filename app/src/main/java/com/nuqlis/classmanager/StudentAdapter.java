package com.nuqlis.classmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private ArrayList<HashMap<Integer, Boolean>> spinnerIndex;
    private static LayoutInflater inflater=null;

    public StudentAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView==null) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.student_row, null);
        }

        final HashMap<String, String>  d = data.get(position);
        TextView title = (TextView)vi.findViewById(R.id.student_title); // title
        final Spinner spinner = (Spinner) vi.findViewById(R.id.attend_spinner);

        /*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("POSITION", id + " " + getItemId(position));
                parent.getSelectedItem();

                Log.d("ONCLICK", "position : " + position + " " + parent.getSelectedItem().toString());
                d.put("ATTEND", spinner.getSelectedItem().toString());
                data.set(position, d);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        */




        String att = d.get("ATTEND");
        switch (att) {
            case "มา" :
                Log.d("STUDENT", d.get("ATTEND") + " 0");
                spinner.setSelection(0, true);
                break;
            case "ลา" :
                Log.d("STUDENT", d.get("ATTEND") + " 1");
                spinner.setSelection(1,  true);
                break;
            case "ป่วย" :
                Log.d("STUDENT", d.get("ATTEND") + " 2");
                spinner.setSelection(2,  true);
                break;
            case "ขาด" :
                Log.d("STUDENT", d.get("ATTEND") + " 3");
                spinner.setSelection(3, true);
                break;
        }


        title.setText(d.get("TITLE"));





        return vi;
    }
}
