//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private ArrayList<Canvas> canvasItems = new ArrayList<Canvas>();
    private ListView listView;
    private CustomListAdapter listAdapter;
    private DatabaseHandler db;//need to change to singleton
    private ConnectionHandler connection;

    private ActivityMediator activityMediator;

    private SharedPreferences prefs;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //creates instance MainActivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("inside: ", "main activity");
        initialize();                                   // all initialized methods
        //checkDBCanvas();
    }

    @Override
    protected void onResume() {                         // Resumes activity
        super.onResume();
        checkDBCanvas();
    }

    private void initialize() {
        Log.e("inside: ", "initialize function");
        serverConnectionInit();                         // Initializes server connection
        dataStorageInit();                              //     "       database
        toolBarInit();                                  // UI
        listViewInit();                                 // UI
        floatingActionButtonsInit();                    // UI
        activityMediatorInit();                         // monitors all activities and classes
        if(userID == null) {
            loginDialogInit();                          // prompts for userID if not present
        } else {
            connection.executeCommand("join-server", userID);  // if present, establish connection with server
        }
    }

    private void activityMediatorInit() {
        activityMediator = ActivityMediator.getInstance();
        activityMediator.setCurrentActivity("MainActivity");
        activityMediator.setMainActivity(this);                //sets the mainActivity object in activitymediator
                                                               //to "this" MainActivity
    }

    private void serverConnectionInit() {
        connection = ConnectionHandler.getInstance(this.getApplicationContext(), this);//initializes connection with
                                                                //server
    }

    private void dataStorageInit() {
        db = DatabaseHandler.getInstance(this.getApplicationContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID = prefs.getString("userID", null);
    }

    private void floatingActionButtonsInit() {
        final FloatingActionButton createCanvasFloatingActionButton = (FloatingActionButton) findViewById(R.id.create_canvas_floating_action_button);
        createCanvasFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // modify and add functionality here

                Intent intent = new Intent(getApplicationContext(), CreateCanvasActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionButton joinCanvasFloatingActionButton = (FloatingActionButton) findViewById(R.id.join_canvas_floating_action_button);
        joinCanvasFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinCanvasActivity.class);

                startActivity(intent);
            }

        });
    }

    private void listViewInit() {//to be modified later
        listView = (ListView) findViewById(R.id.list_view);
        listAdapter = new CustomListAdapter(this, canvasItems);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), CanvasActivity.class);
                Canvas c = db.getCanvasFromName(canvasItems.get(position).getName());
                Log.e("Name : " + c.getName(), "ID: " + c.getID());
                intent.putExtra("userID", userID);
                intent.putExtra("canvasID", c.getID());
                intent.putExtra("canvasTitle", c.getName());

                startActivity(intent);
            }
        });
    }

    private void toolBarInit() {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loginDialogInit() {
        final Dialog login = new Dialog(this);
        // Set GUI of login screen
        login.setContentView(R.layout.login_dialog);
        login.setTitle("Login to Sketchnet");

        // Init button of login GUI
        Button loginButton = (Button) login.findViewById(R.id.login_button);
        final EditText usernameInput = (EditText)login.findViewById(R.id.username_field);

        // Attached listener for login GUI button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameInput.getText().toString().trim().length() > 0) {


                    userID = usernameInput.getText().toString().trim();
                    connection.executeCommand("join-server", userID);
                    savePreferences();

                    Toast.makeText(MainActivity.this,
                            "Login successful. Welcome, " + userID + "!", Toast.LENGTH_SHORT).show();

                    // Redirect to dashboard / home screen.
                    login.dismiss();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please enter userID", Toast.LENGTH_SHORT).show();

                }
            }
        });

        login.setCanceledOnTouchOutside(false);

        login.show();
    }


    private void savePreferences() {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("userID", userID);
        prefsEditor.commit();
    }

    public  void checkDBCanvas() {
        Log.e("inside: ", "checkDBCanvas");
        List<Canvas> canvases = db.getAllCanvases();
        canvasItems.clear();
        //canvasItems = new ArrayList<Canvas>();
        for(Canvas canvas : canvases)
            canvasItems.add(canvas);
        listAdapter.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();
    }

    private void addNewCanvasItemToListView(String canvasTitle, String id) {
        Canvas canvasItem = new Canvas();
        canvasItem.setName(canvasTitle);
        canvasItem.setID("ID: " + id);
        canvasItems.add(canvasItem);

        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
