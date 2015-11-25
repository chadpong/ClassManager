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

    private int totalHour = 1;

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

    public void SetSpinnerVisibility(int c) {
        totalHour = c;
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
        final Spinner spinner2 = (Spinner) vi.findViewById(R.id.attend_spinner2);
        final Spinner spinner3 = (Spinner) vi.findViewById(R.id.attend_spinner3);
        final Spinner spinner4 = (Spinner) vi.findViewById(R.id.attend_spinner4);
        final Spinner spinner5 = (Spinner) vi.findViewById(R.id.attend_spinner5);

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




        String att1 = d.get("ATTEND0");
        switch (att1) {
            case "มา" :
                spinner.setSelection(0, true);
                break;
            case "ลา" :
                spinner.setSelection(1,  true);
                break;
            case "ป่วย" :
                spinner.setSelection(2,  true);
                break;
            case "ขาด" :
                spinner.setSelection(3, true);
                break;
        }

        if (d.get("ATTEND1") != null) {
            String att2 =  d.get("ATTEND1");
            switch (att2) {
                case "มา" :
                    spinner2.setSelection(0, true);
                    break;
                case "ลา" :
                    spinner2.setSelection(1,  true);
                    break;
                case "ป่วย" :
                    spinner2.setSelection(2,  true);
                    break;
                case "ขาด" :
                    spinner2.setSelection(3, true);
                    break;
            }
        }

        if (d.get("ATTEND2") != null) {
            String att3 =  d.get("ATTEND2");
            switch (att3) {
                case "มา" :
                    spinner3.setSelection(0, true);
                    break;
                case "ลา" :
                    spinner3.setSelection(1,  true);
                    break;
                case "ป่วย" :
                    spinner3.setSelection(2,  true);
                    break;
                case "ขาด" :
                    spinner3.setSelection(3, true);
                    break;
            }
        }

        if (d.get("ATTEND3") != null) {
            String att4 =  d.get("ATTEND3");
            switch (att4) {
                case "มา" :
                    spinner4.setSelection(0, true);
                    break;
                case "ลา" :
                    spinner4.setSelection(1,  true);
                    break;
                case "ป่วย" :
                    spinner4.setSelection(2,  true);
                    break;
                case "ขาด" :
                    spinner4.setSelection(3, true);
                    break;
            }
        }

        if (d.get("ATTEND4") != null) {
            String att5 =  d.get("ATTEND4");
            switch (att5) {
                case "มา" :
                    spinner5.setSelection(0, true);
                    break;
                case "ลา" :
                    spinner5.setSelection(1,  true);
                    break;
                case "ป่วย" :
                    spinner5.setSelection(2,  true);
                    break;
                case "ขาด" :
                    spinner5.setSelection(3, true);
                    break;
            }
        }

        title.setText(d.get("TITLE"));

        switch (totalHour) {
            case 1:
                spinner2.setVisibility(View.GONE);
                spinner3.setVisibility(View.GONE);
                spinner4.setVisibility(View.GONE);
                spinner5.setVisibility(View.GONE);
                break;
            case 2:
                spinner3.setVisibility(View.GONE);
                spinner4.setVisibility(View.GONE);
                spinner5.setVisibility(View.GONE);
                break;
            case 3:
                spinner4.setVisibility(View.GONE);
                spinner5.setVisibility(View.GONE);
                break;
            case 4:
                spinner5.setVisibility(View.GONE);
                break;
        }



        return vi;
    }
}
