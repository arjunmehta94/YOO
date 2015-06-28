package com.efemel.sketchnet01;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by anurag on 26/5/15.
 */
public class JoinCanvasActivity extends ActionBarActivity{
    private android.support.v7.widget.Toolbar mToolbar;
        private String canvasIdInput;
        private ConnectionHandler connectionHandler;
        private ActivityMediator activityMediator;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_join_canvas);
            //connect = new JoinCanvasConnect(this.getApplicationContext(), this);
            initialize();
        }

        private void initialize() {
            toolBarInit();
            activityMediatorInit();
            serverConnectionInit();

            final EditText canvasIdInputEditText = (EditText) findViewById(R.id.canvas_id_input);

            Button joinCanvasButton = (Button) findViewById(R.id.join_canvas_button);
            joinCanvasButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    canvasIdInput = canvasIdInputEditText.getText().toString().trim();

                    //connect.sendData(canvasIdInput);
                    connectionHandler.executeCommand("join-canvas", canvasIdInput);
                    Toast.makeText(JoinCanvasActivity.this,
                            "Joining " + canvasIdInput, Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void activityMediatorInit() {
            activityMediator = ActivityMediator.getInstance();
            activityMediator.setJoinCanvasActivity(this);
            activityMediator.setCurrentActivity("JoinCanvasActivity");
        }

        public void serverConnectionInit() {
            connectionHandler = ConnectionHandler.getInstance();
        }

        public void joinFail() {
            Toast.makeText(JoinCanvasActivity.this,
                    "Failed to Join. Wrong Canvas ID", Toast.LENGTH_SHORT).show();
        }

        public void joinSuccess() {
            Toast.makeText(JoinCanvasActivity.this,
                    "Success in joining Canvas", Toast.LENGTH_SHORT).show();

            try {
                Thread.sleep(1500);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            NavUtils.navigateUpFromSameTask(this);
        }

        private void toolBarInit() {
            mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_join_canvas, menu);
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
