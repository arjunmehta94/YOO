package com.efemel.sketchnet01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CanvasInfoActivity extends ActionBarActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    //private CanvasInfoConnect connect;
    private String canvasID;
    private String canvasTitle;
    private String userID;
    private ConnectionHandler connectionHandler;
    private ActivityMediator activityMediator;

    private SharedPreferences prefs;
    private DatabaseHandler db;

    private Button leaveCanvas;

    //private List<UserItem> userItems = new ArrayList<>();
    private List<Participant> participantList = new ArrayList<Participant>();
    //private ListView userListView;
    private ListView participantListView;
    private ParticipantListViewAdapter adapter;

    private String canvasOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_info);
        initialize();
    }

    private void initialize() {
        dataStorageInit();
        processIntent();
        activityMediatorInit();
        initializeToolbar();
        initializeCanvasInfo();
        initializeLeaveCanvasButton();
        initializeParticipantListView();
        serverConnectionInit();
    }

    private void activityMediatorInit() {
        activityMediator = ActivityMediator.getInstance();
        activityMediator.setCurrentActivity("CanvasInfoActivity");
        activityMediator.setCanvasInfoActivity(this);
    }

    private void dataStorageInit() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        db = DatabaseHandler.getInstance(this.getApplicationContext());
    }

    private void processIntent() {
        Intent intent = getIntent();
        canvasID = intent.getStringExtra("canvasID");
        Log.e("Canvas ID from intent:", canvasID);
        canvasTitle = intent.getStringExtra("canvasTitle");
        userID = prefs.getString("userID", null);
    }

    private void serverConnectionInit() {
        connectionHandler = ConnectionHandler.getInstance();
        connectionHandler.executeCommand("canvas-info", canvasID);
    }

    private void initializeCanvasInfo() {
        TextView canvasTitleTextView = (TextView) findViewById(R.id.canvas_title);
        TextView canvasIDTextView = (TextView) findViewById(R.id.canvas_id);

        canvasTitleTextView.setText(canvasTitle);
        canvasIDTextView.setText(canvasID);
    }

    private void updateCanvasInfo() {
        TextView canvasOwnerTextView = (TextView) findViewById(R.id.owner_textview);
        canvasOwnerTextView.setText("Created by " + canvasOwner);
        adapter.notifyDataSetChanged();

    }

    private void initializeParticipantListView() {
        participantListView = (ListView) findViewById(R.id.participant_list_view);
        adapter = new ParticipantListViewAdapter(this, participantList);
        participantListView.setAdapter(adapter);
        registerForContextMenu(participantListView);
    }

    private void initializeLeaveCanvasButton() {
        leaveCanvas = (Button) findViewById(R.id.leave_canvas_button);
        final CanvasInfoActivity c = this;
        leaveCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //connect.sendDataToLeaveCanvas(userID, canvasID);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("userID", userID);
                    obj.put("canvasID", canvasID);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                connectionHandler.executeCommand("leave-canvas", obj);
                db.deleteCanvas(canvasID);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

//                startActivity(intent);
                NavUtils.navigateUpFromSameTask(c);
            }
        });
    }

    private void initializeToolbar() {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_canvas_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //TODO: Fix userList so that it returns an array of strings of users on canvas
    //TODO: Later change it so that userList returns a list of userObjects containing all user info (id, name, status, last seen)
    public void updateInfo() {
        String owner = db.getCanvas(canvasID).getOwner();
        Log.e("Owner is: ", owner);
        canvasOwner = owner;

        List<Participant> participants = db.getAllParticipants(canvasID);
        for(int i=0; i<participants.size(); i++) {
            participantList.add(participants.get(i));
            Log.e("participant: ", participants.get(i).getUserID());
        }

        Log.e("updating", "canvasinfo");
        updateCanvasInfo();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.participant_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(participantList.get(info.position).getUserID());
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);

        String menuItemName = menuItems[menuItemIndex]; // the name of the action selected, e.g. Remove
        String listItemName = participantList.get(info.position).getUserID();

        Toast.makeText(CanvasInfoActivity.this,
                "Selected " + menuItemName + " for " + listItemName, Toast.LENGTH_SHORT).show();

        //  onDestroyContextMenu();
        return true;
    }
}
