//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

/**
 * Created by anurag on 24/5/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;

import java.net.URISyntaxException;

public class CreateCanvasConnect {
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private CreateCanvasActivity parent;
    private DatabaseHandler db;
    private SharedPreferences prefs;
    private String canvasName;
    private Gson gson;
    //private Activity parentActivity;

    public CreateCanvasConnect(Context context, CreateCanvasActivity p) {
        parent = p;
        db = DatabaseHandler.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(p.getApplicationContext());
        gson = new Gson();
        //prefs = context.getSharedPreferences("com.efemel.sketchnet01", context.MODE_PRIVATE);
        try {
            mSocket = IO.socket("http://175.159.125.175:3000");
            mSocket.on("generated-canvasID", onCanvasID);
            mSocket.connect();

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendData(Canvas c) {
        canvasName = c.getName();

        Log.e("create-canvas", c.CanvasName);
        mSocket.emit("create-canvas", gson.toJson(c));
    }

    private Emitter.Listener onCanvasID = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e("Debug", "Woah actually caught something1");

            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("received update", args[0].toString());
                    String object;
                    try {
                        object = args[0].toString();

                        Log.e("Debug", object);
                        Canvas c = new Canvas(canvasName, object, prefs.getString("userID", "NA"));
                        db.updateCanvasID(c);


                    } catch (Exception e) {
                        try {
                            //object = new JSONObject((String) args[0]);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        e.printStackTrace();
                    }

                }
            });
        }
    };
}
