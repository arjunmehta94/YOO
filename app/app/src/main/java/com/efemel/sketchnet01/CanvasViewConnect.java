//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;

public class CanvasViewConnect {
    private LinkedList<Stroke> strokes;
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private int sent;
    private Gson gson;
    private Activity parentActivity;
    private CanvasView parent;

    public CanvasViewConnect(LinkedList<Stroke> s, Context context, CanvasView p) {
        try {
            parent = p;
            strokes = s;
            mSocket = IO.socket("http://175.159.125.175:3000");//change ip!
            mSocket.on("update", onUpdate);
            mSocket.on("new-stroke", onNewStroke);
            mSocket.connect();
            Log.e("Socket", "connected");
            sent = 0;
            gson = new Gson();
            parentActivity = (Activity) context;
            //sendData();
//            mSocket.emit("join-server", parent.getCurrentUserId());//need to get UserID here
//            Log.e("here", parent.getCurrentCanvasId() + "");
//            mSocket.emit("join-room", parent.getCurrentCanvasId());//need to get CanvasID here
            Log.e("here", "success connect");
        } catch(URISyntaxException e) {
            e.printStackTrace();
            Log.e("here", "fail connect");
        }
    }

    public void sendData() {
        Log.e("Sending Data: ", parent.getCurrentUserId());
        mSocket.emit("join-server", parent.getCurrentUserId());//need to get UserID here
        Log.e("here", parent.getCurrentCanvasId() + "");
        mSocket.emit("join-room", parent.getCurrentCanvasId());//need to get CanvasID here
    }

    public void sendStroke(Stroke s) {

        mSocket.emit("new-stroke", gson.toJson(s));

    }

    private Emitter.Listener onUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            //Log.e("Debug", "Woah actually caught something1");

            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("received update", args[0].toString());
                    /*JSONObject object = null;
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

                    Log.e("Debug", "Woah actually caught something2");
                    Stroke s = new Stroke();

                    try {
                        Log.e("Debug", object.getJSONArray("coordinates").toString());
                        for(int i=0; i<object.getJSONArray("coordinates").length(); i++) {
                            s.addCoordinate((float)object.getJSONArray("coordinates").getJSONObject(i).getDouble("X"),
                                    (float)object.getJSONArray("coordinates").getJSONObject(i).getDouble("Y"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    parefdnt.drawNewStroke(s);*/
                }
            });
        }
    };

    private Emitter.Listener onNewStroke = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e("Debug", "Woah actually caught something1");

            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.e("received update", args[0].toString());
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

                    Log.e("Debug", "Woah actually caught something2");
                    try {
                        if(object.getString("userID") != parent.getCurrentUserId()) {//need to compare with current userID.
                            Stroke s = new Stroke(object.getString("userID"));
                            Log.e("Debug", object.getJSONArray("coordinates").toString());
                            for(int i=0; i<object.getJSONArray("coordinates").length(); i++) {
                                s.addCoordinate((float)object.getJSONArray("coordinates").getJSONObject(i).getDouble("X"),
                                        (float)object.getJSONArray("coordinates").getJSONObject(i).getDouble("Y"));
                            }
                            Log.e("Color of stroke", ""+object.getInt("strokeColor"));
                            s.setColor(object.getInt("strokeColor"));

                            parent.drawNewStroke(s);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };

}