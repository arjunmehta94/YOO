package com.efemel.sketchnet01;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jackychan on 27/5/15.
 */
public class UserItem {
    private String id, name;
    private int connectionStatus = 0; // 0 for offline; 1 for online
    private Calendar lastSeen;

    public UserItem() {

    }

    public UserItem(String id, String name, int connectionStatus, Calendar lastSeen) {
        this.id = id;
        this.name = name;
        this.connectionStatus = connectionStatus;
        if (lastSeen != null) {
            this.lastSeen = lastSeen;
        } else {
            // default lastSeen value
            Calendar calendar1 = new GregorianCalendar();
            calendar1.set(2015, 4, 28, 12, 50);
            this.lastSeen = calendar1;
        }
    }

    public String getId() {
        return this.id;
    };

    public void setId(String id) {
        this.id = id;
    };

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConnectionStatus() {
        return this.connectionStatus;
    }

    public void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getLastSeenFormatted() {
        SimpleDateFormat ddMMyyyyhhmmFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        return ddMMyyyyhhmmFormat.format(getLastSeen());
    }

    public Date getLastSeen() {
        return this.lastSeen.getTime();
    }

    public void setLastSeen(Calendar lastSeen) {
        this.lastSeen = lastSeen;
    }

}
