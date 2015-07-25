//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Set;


public class CanvasActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private TextView mToolbarTitle;
    public CanvasView canvasView;
    private DatabaseHandler db;
    private ConnectionHandler connectionHandler;
    private BluetoothHandler bluetoothHandler;
    private ActivityMediator activityMediator;
    //private CanvasConnect connect;
//    private SharedPreferences mPrefs;

    private String userID;
    private String canvasID;
    private String canvasTitle;
    private String btAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHandler.getInstance(this.getApplicationContext());

        //connect = new CanvasConnect(this.getApplicationContext(), this);
        setContentView(R.layout.activity_canvas);
        initialize();
    }

    private void initialize() {

        processIntent();//change to DB!
        initializeCanvasTitleButton();
        initializeCanvasView();
        initializePenOptionsButton();
        initializePenColorButton();
        initializeBluetoothButton();
        serverConnectionInit();
        activityMediatorInit();

        //connect.sendJoinRoomData(userID, canvasID);

        Toast.makeText(CanvasActivity.this,
                "userID: " + userID + ", canvasID: " + canvasID, Toast.LENGTH_SHORT).show();
//        mPrefs = getPreferences(MODE_PRIVATE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //Log.e("request code", resultCode + "");
        BluetoothDevice dev = null;
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){

                Set<BluetoothDevice> pairedDevices = bluetoothHandler.getBluetoothAdapter().getBondedDevices();
                if(pairedDevices.size() > 0) {
                    for(BluetoothDevice device : pairedDevices) {
                        // need to identify the correct device
                        // if device == correct bluetooth device, then do:
                        if(device.getAddress() == btAddress){
                            dev = device;
                        }
                    }
                }
                if (dev == null){
                    // perform discovery and pairing
                }
                // start bluetooth thread here
                bluetoothHandler.connectBluetooth(dev);
            }
            else{
                Log.e("error", "bluetooth not enabled");
            }
        }
    }

    private void serverConnectionInit() {
        connectionHandler = ConnectionHandler.getInstance();
        connectionHandler.executeCommand("join-room", canvasID);
    }

    private void activityMediatorInit() {
        activityMediator = ActivityMediator.getInstance();
        activityMediator.setCurrentActivity("CanvasActivity");
        activityMediator.setCanvasActivity(this);

    }

    private void initializePenColorButton() {
        final FloatingActionButton penColorButton = (FloatingActionButton) findViewById(R.id.pen_color_floating_action_button);

        penColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPenColorDialog();
            }
        });
    }

    private void initializeBluetoothButton() {
        final FloatingActionButton bluetoothButton = (FloatingActionButton) findViewById(R.id.bluetooth_floating_action_button);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConfig();
            }
        });
    }

    private void bluetoothConfig() {
        bluetoothHandler = BluetoothHandler.getInstance(this);
        bluetoothHandler.initialize();
    }

    private void createPenColorDialog() {
        // Create Object of Dialog class
        final Dialog penColorDialog = new Dialog(this);
        // Set GUI of penColorDialog screen
        penColorDialog.setContentView(R.layout.pen_color_dialog);
        penColorDialog.setTitle("Choose color");

        // Init button of penColorDialog GUI
//        Button loginButton = (Button) penColorDialog.findViewById(R.id.login_button);
//        final EditText usernameInput = (EditText)penColorDialog.findViewById(R.id.username_field);

        // Attached listener for penColorDialog GUI button
/*
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameInput.getText().toString().trim().length() > 0) {


                    userID = usernameInput.getText().toString().trim();
                    // Validate Your penColorDialog credential here than display message

                    ();
                    //prefs.edit().putString("userID", userID);
                    //prefs.edit().commit();
                    Toast.makeText(MainActivity.this,
                            "Login successful. Welcome, " + userID + "!", Toast.LENGTH_SHORT).show();

                    // Redirect to dashboard / home screen.
                    penColorDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please enter userID", Toast.LENGTH_SHORT).show();

                }
            }
        });

        penColorDialog.setCanceledOnTouchOutside(false);
*/

        penColorDialog.show();

        initializeColorButtons(penColorDialog);

    }

    private void initializeColorButtons(final Dialog penColorDialog) {
        AppCompatButton redButton = (AppCompatButton) penColorDialog.findViewById(R.id.red_color_button);
        // redTint hex color code = #FFFF0000 = 0xffff0000 (Alpha-Red-Blue-Green: #AA RR GG BB)
        ColorStateList redTint = new ColorStateList(new int[][]{new int[0]}, new int[]{0xffff0000});
        redButton.setSupportBackgroundTintList(redTint);

        AppCompatButton orangeButton = (AppCompatButton) penColorDialog.findViewById(R.id.orange_color_button);
        // orangeTint hex color code = #FFFFA500 = 0xffffa500 (Alpha-Red-Blue-Green: #AA RR GG BB)
        ColorStateList orangeTint = new ColorStateList(new int[][]{new int[0]}, new int[]{0xffffa500});
        orangeButton.setSupportBackgroundTintList(orangeTint);

        AppCompatButton greenButton = (AppCompatButton) penColorDialog.findViewById(R.id.green_color_button);
        // orangeTint hex color code = #FFFFA500 = 0xffffa500 (Alpha-Red-Blue-Green: #AA RR GG BB)
        ColorStateList greenTint = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff008000});
        greenButton.setSupportBackgroundTintList(greenTint);

        AppCompatButton blueButton = (AppCompatButton) penColorDialog.findViewById(R.id.blue_color_button);
        // orangeTint hex color code = #FFFFA500 = 0xffffa500 (Alpha-Red-Blue-Green: #AA RR GG BB)
        ColorStateList blueTint = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff0000ff});
        blueButton.setSupportBackgroundTintList(blueTint);

        // Color Buttons onClickListeners
        AppCompatButton blackButton = (AppCompatButton) penColorDialog.findViewById(R.id.black_color_button);

        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatingActionButton penColorButton = (FloatingActionButton) findViewById(R.id.pen_color_floating_action_button);
                penColorButton.setColorNormal(Color.parseColor("#000000"));

                final FloatingActionButton penOptionsButton = (FloatingActionButton) findViewById(R.id.action_pen_options);
                penOptionsButton.setColorNormal(Color.parseColor("#000000"));

                final CanvasView canvasView = (CanvasView) findViewById(R.id.canvas_view);
                canvasView.setStrokeColor(Color.parseColor("#000000"));

                penColorDialog.dismiss();


            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatingActionButton penColorButton = (FloatingActionButton) findViewById(R.id.pen_color_floating_action_button);
                penColorButton.setColorNormal(Color.parseColor("#FF0000"));

                final FloatingActionButton penOptionsButton = (FloatingActionButton) findViewById(R.id.action_pen_options);
                penOptionsButton.setColorNormal(Color.parseColor("#FF0000"));

                final CanvasView canvasView = (CanvasView) findViewById(R.id.canvas_view);
                canvasView.setStrokeColor(Color.parseColor("#FF0000"));

                penColorDialog.dismiss();


            }
        });

        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatingActionButton penColorButton = (FloatingActionButton) findViewById(R.id.pen_color_floating_action_button);
                penColorButton.setColorNormal(Color.parseColor("#FFA500"));

                final FloatingActionButton penOptionsButton = (FloatingActionButton) findViewById(R.id.action_pen_options);
                penOptionsButton.setColorNormal(Color.parseColor("#FFA500"));

                final CanvasView canvasView = (CanvasView) findViewById(R.id.canvas_view);
                canvasView.setStrokeColor(Color.parseColor("#FFA500"));

                penColorDialog.dismiss();


            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatingActionButton penColorButton = (FloatingActionButton) findViewById(R.id.pen_color_floating_action_button);
                penColorButton.setColorNormal(Color.parseColor("#008000"));

                final FloatingActionButton penOptionsButton = (FloatingActionButton) findViewById(R.id.action_pen_options);
                penOptionsButton.setColorNormal(Color.parseColor("#008000"));

                final CanvasView canvasView = (CanvasView) findViewById(R.id.canvas_view);
                canvasView.setStrokeColor(Color.parseColor("#008000"));

                penColorDialog.dismiss();


            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatingActionButton penColorButton = (FloatingActionButton) findViewById(R.id.pen_color_floating_action_button);
                penColorButton.setColorNormal(Color.parseColor("#0000FF"));

                final FloatingActionButton penOptionsButton = (FloatingActionButton) findViewById(R.id.action_pen_options);
                penOptionsButton.setColorNormal(Color.parseColor("#0000FF"));

                final CanvasView canvasView = (CanvasView) findViewById(R.id.canvas_view);
                canvasView.setStrokeColor(Color.parseColor("#0000FF"));

                penColorDialog.dismiss();


            }
        });

    }

    private void initializePenOptionsButton() {
        final FloatingActionsMenu penOptionsMenu = (FloatingActionsMenu) findViewById(R.id.pen_options_floating_actions_menu);

        final FloatingActionButton penOptionsButton = (FloatingActionButton) findViewById(R.id.action_pen_options);
        penOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (penOptionsMenu.isExpanded()) {
                    penOptionsMenu.collapse();
                } else {
                    penOptionsMenu.expand();
                }
            }
        });
    }


    private void initializeCanvasView() {
        canvasView = (CanvasView) findViewById(R.id.canvas_view);

        Log.e("userID for canvasView", userID);


        canvasView.setCurrentUserId(this.userID);
        canvasView.setCurrentCanvasId(this.canvasID);

        //canvasView.getExport().sendData();
    }

    private void initializeCanvasTitleButton() {

        Button canvasTitleButton = (Button) findViewById(R.id.canvas_title);
        canvasTitleButton.setText(canvasTitle);
        canvasTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CanvasInfoActivity.class);
                intent.putExtra("canvasID", canvasID);
                intent.putExtra("canvasTitle", canvasTitle);
                startActivity(intent);
            }
        });
    }

    private void processIntent() {
        Intent intent = getIntent();

        userID = intent.getStringExtra("userID");
        Log.e("UserID: is ", userID);
        if(intent.getStringExtra("canvasID") != null) {
            canvasID = intent.getStringExtra("canvasID");
            Log.e("CanvasID: is ", canvasID);
        } else {
            canvasID = "Canvas -1";
        }

        canvasTitle = intent.getStringExtra("canvasTitle");
        Log.e("Canvas ID: ", canvasTitle);
    }

    /*private void initializeToolbar() {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        *//* set the toolbar attributes*//*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        *//* set toolbar title attributes*//*
        mToolbarTitle.setText(canvasTitle);
    }
*/
    /*@Override
    protected void onResume() {
        super.onResume();

        Gson gson = new Gson();
        String json = mPrefs.getString("strokes", "");
        Log.e("json string onResume", json);

        Type type  = new TypeToken<LinkedList<Stroke>>(){}.getType();
        LinkedList<Stroke> strokes = new Gson().fromJson(json, type);

//        canvasView.setAllStrokes(strokes);

    }*/
/*
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(canvasView.getAllStrokes());
        Log.e("json string onPause", json);
        prefsEditor.putString("strokes", json);
        prefsEditor.commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_canvas, menu);
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

    @Override
    public void onBackPressed() {
        //connect.sendLeaveRoomData();
        connectionHandler.executeCommand("leave-room", null);
        NavUtils.navigateUpFromSameTask(this);
    }

    public String getCurrentUserID() {
        return this.userID;
    }

    public String getCurrentCanvasID(){
        return this.canvasID;
    }
}
