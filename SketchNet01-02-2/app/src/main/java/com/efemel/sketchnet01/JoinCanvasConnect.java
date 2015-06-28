package com.efemel.sketchnet01;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by anurag on 26/5/15.
 */
public class JoinCanvasConnect {
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private JoinCanvasActivity parent;
    private DatabaseHandler db;
    private SharedPreferences prefs;
    private String canvasName;
    private Gson gson;
    //private Activity parentActivity;

    public JoinCanvasConnect(Context context, JoinCanvasActivity p) {
        parent = p;
        db = DatabaseHandler.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(p.getApplicationContext());
        //gson = new Gson();
        //prefs = context.getSharedPreferences("com.efemel.sketchnet01", context.MODE_PRIVATE);
        try {
            mSocket = IO.socket("http://175.159.125.175:3000");
            mSocket.on("joined-canvas", onJoinedCanvas);
            mSocket.connect();

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String canvasID) {
        //canvasName = c.getName();

        Log.e("join-canvas", canvasID);
        mSocket.emit("join-canvas", canvasID);
    }

    private Emitter.Listener onJoinedCanvas = new Emitter.Listener() {
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
                    try{
                        String CanvasID = object.getString("CanvasID");
                        String CanvasName = object.getString("CanvasName");
                        String Owner = object.getString("Owner");
                        if(CanvasID.equals("NA") || CanvasName.equals("NA") || Owner.equals("NA")) {
                            Log.e("joinFailed", CanvasID + CanvasName + Owner);

                            parent.joinFail();
                        } else {
                            Log.e("joinSuccess", CanvasID + CanvasName + Owner);
                            Canvas c = new Canvas(CanvasName, CanvasID, Owner);
                            db.addCanvas(c);
                            parent.joinSuccess();
                        }


                    } catch(Exception e) {
                        e.printStackTrace();
                    }




                }
            });
        }
    };
}
