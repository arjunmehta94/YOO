package com.efemel.sketchnet01;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class CanvasInfoConnect {
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private CanvasInfoActivity parent;
    private String Owner;
    private ArrayList<String> userList;
    //private Activity parentActivity;

    public CanvasInfoConnect(Context context, CanvasInfoActivity p) {
        parent = p;
        userList = new ArrayList<String>();
        //prefs = context.getSharedPreferences("com.efemel.sketchnet01", context.MODE_PRIVATE);
        try {
            mSocket = IO.socket("http://175.159.125.175:3000");
            mSocket.on("canvas-info-ready", onCanvasInfoReady);
            mSocket.connect();

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendDataForInfo(String canvasID) {
        Log.e("CanvasID to be sent", canvasID);
        mSocket.emit("canvas-info", canvasID);
    }

    public void sendDataToLeaveCanvas(String userID, String canvasID) {
        Log.e("User Leaves Canvas", userID);
        JSONObject obj = new JSONObject();
        try{
            obj.put("userID", userID);
            obj.put("canvasID", canvasID);
        } catch(Exception e) {
            e.printStackTrace();
        }

        mSocket.emit("leave-canvas", obj);
    }

    private Emitter.Listener onCanvasInfoReady = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e("Debug", "Woah actually caught something1");

            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("received update", args[0].toString());
                    JSONObject object = null;
                    try {
                        object = (JSONObject) args[0];

                    } catch(Exception e) {
                        try {
                            object = new JSONObject((String) args[0]);
                        } catch(Exception e1) {
                            e1.printStackTrace();
                        }

                        e.printStackTrace();
                    }

                    try {
                        Owner = object.getString("owner");

                        for(int i=0; i<object.getJSONArray("userList").length(); i++) {
                            userList.add(object.getJSONArray("userList").getString(i));
                            Log.e("User " + i + ":", object.getJSONArray("userList").getString(i));
                        }
                        //parent.updateInfo(Owner, userList);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
}
