package com.efemel.sketchnet01;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by anurag on 3/6/15.
 */
public class Participant {
    private String UserID = null;
    private String CanvasID = null;
    private int Online = 0;
    private Calendar lastSeen;

    public Participant() {

    }

    public Participant(String u, String c, int o, Calendar l) {
        UserID = u;
        CanvasID = c;
        Online = o;
        if(l!=null) {
            lastSeen = l;
        } else {
            Calendar calendar1 = new GregorianCalendar();
            calendar1.set(2015, 4, 28, 12, 50);
            lastSeen = calendar1;
        }
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String u) { UserID = u; }

    public String getCanvasID() {
        return CanvasID;
    }

    public int getConnectionStatus() {//three states for online: -1: no internet, 0: offline, 1: online
        return Online;
    }

    public void setConnectionStatus(int o) {
        Online = o;
    }

    public String getLastSeenFormatted() {
        SimpleDateFormat ddMMyyyyhhmmFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        return ddMMyyyyhhmmFormat.format(getLastSeen());
    }

    public Date getLastSeen() {
        return lastSeen.getTime();
    }

    public void setLastSeen(Calendar l) {
        lastSeen = l;
    }
}
