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

public class StudentAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
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

        if(convertView==null)
            vi = inflater.inflate(R.layout.student_row, null);

        TextView title = (TextView)vi.findViewById(R.id.student_title); // title
        final Spinner spinner = (Spinner) vi.findViewById(R.id.attend_spinner);

        HashMap<String, String> d = data.get(position);


        title.setText(d.get("TITLE"));

        if (!d.get("isFirst").equals("0")) {
            String att = d.get("ATTEND");

            switch (att) {
                case "มา" :
                    spinner.setSelection(0);
                    break;
                case "ลา" :
                    spinner.setSelection(1);
                    break;
                case "ป่วย" :
                    spinner.setSelection(2);
                    break;
                case "ขาด" :
                    spinner.setSelection(3);
                    break;
            }
            data.get(position).put("isFirst", "0");
        }

        return vi;
    }
}
