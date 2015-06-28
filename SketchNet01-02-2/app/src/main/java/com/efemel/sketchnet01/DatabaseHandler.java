//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by anurag on 24/5/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler instance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SketchNetDB";
    private static final String TABLE_CANVAS = "Canvas";
    private static final String TABLE_PARTICIPANTS = "Participants";

    private static final String KEY_CANVAS_NAME = "CanvasName";
    private static final String KEY_CANVAS_ID = "CanvasID";
    private static final String KEY_CANVAS_OWNER = "Owner";

    private static final String KEY_USER_ID = "UserID";
    private static final String KEY_ONLINE = "Online";
    private static final String KEY_LAST_SEEN = "LastSeen";

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("Constructed DB", ":)");
    }

    public static DatabaseHandler getInstance(Context context) {
        if(instance == null) {
            Log.e("check null", "yes");
            instance = new DatabaseHandler(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CANVAS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CANVAS + " ("
                + KEY_CANVAS_NAME + " TEXT," + KEY_CANVAS_ID + " TEXT,"
                + KEY_CANVAS_OWNER + " TEXT" + ")";

        String CREATE_USERLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PARTICIPANTS + " ("
                + KEY_CANVAS_ID + " TEXT," + KEY_USER_ID + " TEXT," + KEY_ONLINE + " NUMBER," + KEY_LAST_SEEN
                + " TEXT" + ")";
        db.execSQL(CREATE_CANVAS_TABLE);
        db.execSQL(CREATE_USERLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CANVAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);

        // Create tables again
        onCreate(db);
    }

    public void addCanvas(Canvas canvas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CANVAS_NAME, canvas.getName());
        values.put(KEY_CANVAS_ID, canvas.getID());
        values.put(KEY_CANVAS_OWNER, canvas.getOwner());

        db.insert(TABLE_CANVAS, null, values);
        db.close();
    }

    public Canvas getCanvas(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CANVAS, new String[] {KEY_CANVAS_NAME,
                        KEY_CANVAS_ID, KEY_CANVAS_OWNER}, KEY_CANVAS_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Canvas canvas = new Canvas(cursor.getString(0),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return canvas;
    }

    public Canvas getCanvasFromName(String CanvasName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CANVAS, new String[] {KEY_CANVAS_NAME,
                        KEY_CANVAS_ID, KEY_CANVAS_OWNER}, KEY_CANVAS_NAME + "=?",
                new String[] { CanvasName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Canvas canvas = new Canvas(cursor.getString(0),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return canvas;

    }

    public List<Canvas> getAllCanvases() {
        List<Canvas> contactList = new ArrayList<Canvas>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CANVAS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Canvas canvas = new Canvas();
                    canvas.setID(cursor.getString(1));
                    canvas.setName(cursor.getString(0));
                    canvas.setOwner(cursor.getString(2));
                    // Adding contact to list
                    contactList.add(canvas);
                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


        // return contact list
        return contactList;
    }

    public int updateCanvasID(Canvas canvas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CANVAS_ID, canvas.getID());

        // updating row
        return db.update(TABLE_CANVAS, values, KEY_CANVAS_NAME + " = ?",
                new String[] { String.valueOf(canvas.getName()) });
    }

    public boolean deleteCanvas(String canvasID) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_CANVAS, KEY_CANVAS_ID + " = ?", new String[] {canvasID}) > 0;
    }

    public void addParticipant(Participant participant) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT  * FROM " + TABLE_PARTICIPANTS + " WHERE " + KEY_CANVAS_ID + " = " + "\""+ participant.getCanvasID() + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            ContentValues values = new ContentValues();
            Log.e("CanvasID: ", participant.getCanvasID());
            values.put(KEY_USER_ID, participant.getUserID());
            values.put(KEY_CANVAS_ID, participant.getCanvasID());
            values.put(KEY_ONLINE, participant.getConnectionStatus());
            values.put(KEY_LAST_SEEN, participant.getLastSeenFormatted());

            db.insert(TABLE_PARTICIPANTS, null, values);
            db.close();
            cursor.close();
        } else {// update last seen
            cursor.close();
        }
    }

    public List<Participant> getAllParticipants(String CanvasID) {
        Log.e("inside: ", "getAllParticipants");
        List<Participant> participantList = new ArrayList<Participant>();

        String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANTS + " WHERE " + KEY_CANVAS_ID + " = " + "\""+CanvasID + "\";";

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("here", "1");
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("here", "2");

        // looping through all rows and adding to list
        try {
            Log.e("here", "3");
            if (cursor.moveToFirst()) {
                do {
                    Log.e("inside: ", "loop trying to parse last seen");
                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(3)));
                    Participant participant = new Participant(cursor.getString(1), cursor.getString(0), cursor.getInt(2), cal);
                    participantList.add(participant);
                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            Log.e("inside", "caught exception");
            e.printStackTrace();
        }


        // return contact list
        return participantList;
    }

}
