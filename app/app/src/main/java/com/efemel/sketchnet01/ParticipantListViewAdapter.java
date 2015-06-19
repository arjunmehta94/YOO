package com.efemel.sketchnet01;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jackychan on 27/5/15.
 */
public class ParticipantListViewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater layoutInflater;
    //private List<UserItem> userItems;
    private List<Participant> participantList;

    public ParticipantListViewAdapter(Activity activity, List<Participant> participantList) {
        this.activity = activity;
        //this.userItems = userItems;
        this.participantList = participantList;
    }

    @Override
    public int getCount() {
        return participantList.size();
    }

    @Override
    public Object getItem(int position) {
        return participantList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.user_list_item, null);
        }

        TextView usernameTextView = (TextView) convertView.findViewById(R.id.username_textview);
        TextView connectionStatusTextView = (TextView) convertView.findViewById(R.id.connection_status_textview);
        ImageView onlineIndicatorImageView = (ImageView) convertView.findViewById(R.id.online_indicator_imageview);

        Participant participant = participantList.get(position);

        usernameTextView.setText(participant.getUserID());

        if (participant.getConnectionStatus() == 1) {
            connectionStatusTextView.setText("online");
            onlineIndicatorImageView.setVisibility(View.VISIBLE);
        } else if (participant.getConnectionStatus() == 0) {
            connectionStatusTextView.setText("Last seen: " + participant.getLastSeenFormatted());
            onlineIndicatorImageView.setVisibility(View.GONE);
        }
        return convertView;
    }

}
