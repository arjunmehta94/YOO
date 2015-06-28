package com.efemel.sketchnet01;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by anurag on 27/5/15.
 */

public class ConnectionHandler {
    private static ConnectionHandler instance = null;
    private static Context context;
    private static String IP_ADDRESS = "http://192.168.1.5:3000";
    private static Socket socket;
    private Gson gson;

    private ActivityMediator activityMediator;

    private Activity parentActivity;

    private static int insertIndex = 0;
    private static int retrieveIndex = 0;

    private static SharedPreferences prefs;
    private static DatabaseHandler db;

    private static String userID;

    private ConnectionHandler(Context c, Activity p) {
        Log.e("inside: ", "ConnectionHandler constructor");
        context = c;
        parentActivity = p;
        gson = new Gson();


        initialize();
        try {
            socketInit();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        dataStorageInit();
        activityMediatorInit();
        userID = prefs.getString("userID", null);
    }

    private void activityMediatorInit() {
        activityMediator = ActivityMediator.getInstance();
    }

    private void dataStorageInit() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        db = DatabaseHandler.getInstance(context);
    }

    public static ConnectionHandler getInstance(Context c, Activity p) {
        if(instance == null) {
            instance = new ConnectionHandler(c, p);
        }
        Log.e("inside: ", "ConnectionHandler getInstance()");
        return instance;
    }

    public static ConnectionHandler getInstance() {
        return instance;
    }

    private void socketInit() throws URISyntaxException{
        Log.e("inside: ", "socketInit()");
        socket = IO.socket(IP_ADDRESS);
        socket.on("update", onUpdate);
        socket.on("generated-canvasID", onCanvasID);
        socket.on("joined-canvas", onJoinedCanvas);
        socket.on("canvas-info-ready", onCanvasInfoReady);
        socket.on("new-stroke", onNewStroke);
        socket.on("leave-room", onLeftRoom);
        socket.connect();
    }

    public void executeCommand(String command, Object data) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString("Command " + insertIndex, command);
        e.commit();
        e.putString("Data " + insertIndex, gson.toJson(data));
        e.commit();
        insertIndex++;
        if(isOnline()) {//send data immediately
            sendData();
        }
    }

    public void sendData() {
        Log.e("inside: ", "sendData()");
        Log.e(""+insertIndex, ""+retrieveIndex);
        while(retrieveIndex < insertIndex) {
            Log.e(prefs.getString("Command " + retrieveIndex,"fail"), prefs.getString("Data " + retrieveIndex, "fail"));
            socket.emit(prefs.getString("Command " + retrieveIndex,"fail"), prefs.getString("Data " + retrieveIndex, "fail"));
            prefs.edit().remove("Command " + retrieveIndex);
            prefs.edit().commit();
            prefs.edit().remove("Data " + retrieveIndex);
            prefs.edit().commit();
            retrieveIndex++;
        }
    }

    private boolean isOnline()
    {
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e) {
            return false;
        }
    }


    private Emitter.Listener onUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onUpdate: ", args[0].toString());
                }
            });
        }
    };

    private Emitter.Listener onCanvasID = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onCanvasID: ", args[0].toString());
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

                        Log.e("createSuccess", CanvasID + CanvasName + Owner);
                        Canvas c = new Canvas(CanvasName, CanvasID, Owner);
                        db.updateCanvasID(c);
                        activityMediator.updateMainActivityList();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onJoinedCanvas = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onJoinedCanvas", args[0].toString());
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
                            activityMediator.checkJoinCanvas("fail");
                        } else {
                            Log.e("joinSuccess", CanvasID + CanvasName + Owner);
                            Canvas c = new Canvas(CanvasName, CanvasID, Owner);
                            db.addCanvas(c);
                            activityMediator.checkJoinCanvas("success");
                        }


                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onCanvasInfoReady = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onCanvasInfoReady", args[0].toString());
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
                    String Owner;
                    //ArrayList<String> userList = new ArrayList<String>();
                    try {
                        Owner = object.getString("owner");
                        for(int i=0; i<object.getJSONArray("userList").length(); i++) {
                            //userList.add(object.getJSONArray("userList").getString(i));
                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(df.parse("06-06-2015 07:24 pm"));
                            Log.e("userList", object.getJSONArray("userList").getString(i));
                            Log.e("online", "" + object.getJSONArray("online").getInt(i));
                            Participant p = new Participant(object.getJSONArray("userList").getString(i),
                                                            object.getString("CanvasID"),
                                                            object.getJSONArray("online").getInt(i),
                                                            cal);
                            db.addParticipant(p);
                        }
                        //invoke method in CanvasInfoActivity to show all participants and owners
                        activityMediator.updateParticipantList();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onNewStroke = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                        if(!object.getString("userID").equals(userID)) {//need to compare with current userID.
                            Stroke s = new Stroke(object.getString("userID"));
                            Log.e("Debug", object.getJSONArray("coordinates").toString());
                            for(int i=0; i<object.getJSONArray("coordinates").length(); i++) {
                                s.addCoordinate((float)object.getJSONArray("coordinates").getJSONObject(i).getDouble("X"),
                                        (float)object.getJSONArray("coordinates").getJSONObject(i).getDouble("Y"));
                            }
                            Log.e("Color of stroke", ""+object.getInt("strokeColor"));
                            s.setColor(object.getInt("strokeColor"));

                            //parent.drawNewStroke(s);
                            activityMediator.drawNewStrokeOnCanvasView(s);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onLeftRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onLeftRoom", args[0].toString());
                }
            });
        }
    };
}
