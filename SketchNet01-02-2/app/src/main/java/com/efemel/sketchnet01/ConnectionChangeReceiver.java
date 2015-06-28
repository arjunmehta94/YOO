package com.efemel.sketchnet01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static boolean isConnected;

    public ConnectionChangeReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //ConnectionHandler.getInstance().online(isConnected);

        if(isConnected) {
            //Log.e("inside: ", "isConnected. Send Data!");
            ConnectionHandler.getInstance().sendData();
        }
    }

    public boolean isOnline() {
        return isConnected;
    }
}
