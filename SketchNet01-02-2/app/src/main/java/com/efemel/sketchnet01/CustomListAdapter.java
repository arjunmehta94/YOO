//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jackychan on 17/5/15.
 */
public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Canvas> canvasItems;

    public CustomListAdapter(Activity activity, List<Canvas> canvasItems) {
        this.activity = activity;
        this.canvasItems = canvasItems;
    }

    @Override
    public int getCount() {
        return canvasItems.size();
    }

    @Override
    public Object getItem(int position) {
        return canvasItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView cname = (TextView) convertView.findViewById(R.id.cname);
        TextView cid = (TextView) convertView.findViewById(R.id.cid);

        // getting the canvasItem data for the row
        Canvas canvasItem = canvasItems.get(position);

        cname.setText(canvasItem.getName());
        cid.setText(canvasItem.getID());

        return convertView;
    }
}
