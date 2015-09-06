package com.nuqlis.classmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chadpong on 5/9/2558.
 */
public class DrawerAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<DrawerModel> data;
    private static LayoutInflater inflater=null;

    public DrawerAdapter(Activity a, ArrayList<DrawerModel> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.drawer_layout, null);

        TextView title = (TextView)vi.findViewById(R.id.drawer_layout_label); // title
        ImageView image = (ImageView)vi.findViewById(R.id.drawer_layout_image); // artist name

        DrawerModel d = data.get(position);

        title.setText(d.GetTitle());
        image.setImageResource(d.GetIconID());
        return vi;
    }
}
