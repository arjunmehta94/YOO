package com.efemel.sketchnet01;

import android.content.Context;
import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;

/**
 * Created by anurag on 26/5/15.
 */
public class CanvasConnect {
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private CanvasActivity parent;

    public CanvasConnect(Context context, CanvasActivity p) {
        parent = p;
        try {
            mSocket = IO.socket("http://175.159.125.175:3000");
            mSocket.connect();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendJoinRoomData(String UserID, String CanvasID) {
        mSocket.emit("join-server", UserID);
        mSocket.emit("join-room", CanvasID);
    }

    public void sendLeaveRoomData() {
        //Log.e("create-canvas", c.CanvasName);
        mSocket.emit("leave-room", "");
    }
}
