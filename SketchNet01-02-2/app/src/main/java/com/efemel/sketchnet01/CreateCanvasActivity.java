//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CreateCanvasActivity extends ActionBarActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private String canvasNameInput;
    private ConnectionHandler connectionHandler;
    private SharedPreferences prefs;
    private DatabaseHandler db;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("inside: ", "CreateCanvasActivity");
        setContentView(R.layout.activity_create_canvas);
        initialize();
    }

    private void initialize() {
        Log.e("inside: ", "CCA-initialize()");
        dataStorageInit();
        serverConnectionInit();
        toolbarInit();

        userID = prefs.getString("userID", "NA");

        final EditText canvasNameInputEditText = (EditText) findViewById(R.id.canvas_name_input);
        Button createCanvasButton = (Button) findViewById(R.id.create_canvas_button);
        final CreateCanvasActivity temp = this;
        createCanvasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasNameInput = canvasNameInputEditText.getText().toString().trim();
                Canvas c = new Canvas(canvasNameInput, userID);
                db.addCanvas(c);
                //connect.sendData(c)
                connectionHandler.executeCommand("create-canvas", c);
                // modify and add functionality here
                NavUtils.navigateUpFromSameTask(temp);
            }
        });
    }

    private void dataStorageInit() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        db = DatabaseHandler.getInstance(this.getApplicationContext());
    }

    private void serverConnectionInit() {
        //connect = new CreateCanvasConnect(this.getApplicationContext(), this);
        connectionHandler = ConnectionHandler.getInstance();
    }

    private void toolbarInit() {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_canvas, menu);
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
