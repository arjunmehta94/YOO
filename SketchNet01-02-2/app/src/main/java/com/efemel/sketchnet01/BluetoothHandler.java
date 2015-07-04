package com.efemel.sketchnet01;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by anurag on 16/6/15.
 */
public class BluetoothHandler {
    private static BluetoothHandler instance = null;
    private static Context context;

    private ActivityMediator activityMediator;

    private CanvasActivity parentActivity;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private BluetoothConnectedThread bluetoothConnectedThread;

    private BluetoothConnectThread bluetoothConnectThread;

    private int xCoordinate = 0;
    private int yCoordinate = 0;

    private Stroke thisStroke;

    private BluetoothHandler(CanvasActivity p) {
        //initialize();
        parentActivity = p;
    }

    public static BluetoothHandler getInstance(CanvasActivity p) {
        //parentActivity = p;
        if(instance == null) {
            instance = new BluetoothHandler(p);
        }
        return instance;
    }

    public static BluetoothHandler getInstance() {
        return instance;
    }

    public void initialize() {
        thisStroke = new Stroke("anurag");
        activityMediatorInit();
        bluetoothInit();
    }

    private void activityMediatorInit() {
        activityMediator = ActivityMediator.getInstance();
    }

    private void bluetoothInit() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            parentActivity.startActivityForResult(enableBtIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                bluetoothDevice = device;
            }
        }

        Log.e("inside bluinit:", bluetoothDevice.toString());

        bluetoothConnectThread = new BluetoothConnectThread(bluetoothDevice);
        bluetoothConnectThread.start();

    }

    private class BluetoothConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


        public BluetoothConnectThread(BluetoothDevice bd) {
            BluetoothSocket tmp = null;
            bluetoothDevice = bd;

            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);

            } catch(IOException e) {
                Log.e("Exception", "BluetoothDevice IOException");
            }
            bluetoothSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            Log.e("inside receive", "receiving");
            try {
                Log.e("inside: run", "trying to connect bluSocket");
                bluetoothSocket.connect();
                Log.e("inside: run", "bluSocket connected");
            } catch(IOException e) {
                Log.e("inside: run", "cannot connect");
                try {
                    bluetoothSocket.close();
                } catch(IOException e1) {
                    Log.e("Exception", "closed connection exception");
                }
            }
            bluetoothConnectedThread = new BluetoothConnectedThread(bluetoothSocket);
            //bluetoothConnectedThread.start();
            Log.e("Debug","trying to run on UI thread");
            parentActivity.runOnUiThread(bluetoothConnectedThread);

        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch(IOException e) {
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;

            //Log.e("handling message", "");

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(0, end);
                    Log.e("msg", writeMessage);

                    boolean startFound = false;
                    boolean endFound = false;
                    int beginIndex = -1;
                    int index = 0;
                    int endIndex = -1;
                    try {
                        while(!startFound) {
                            if(writeMessage.charAt(index) == '\n') {
                                startFound = true;
                                beginIndex = index;
                            }
                            index++;
                        }

                        while(!endFound) {
                            if(writeMessage.charAt(index) == '\n') {
                                endFound = true;
                                endIndex = index;
                            }
                            index++;
                        }
                        String finalMessage = writeMessage.substring(beginIndex+1, endIndex);
                        Log.e("the final message is: ", finalMessage);
                        parseFinalMessage(finalMessage);
                        //bluetoothConnectedThread.write();
                    } catch(StringIndexOutOfBoundsException e) {
                        Log.e("sorry", "could not parse message");
                    }

                    //bluetoothConnectedThread.write("K".getBytes());
                    break;
            }
        }
    };

    private void parseFinalMessage(String finalMessage) {
        //int signX = Integer.parseInt(finalMessage.substring(0,1));
        int signX;
        if(finalMessage.substring(0,1).equals("+")) {
            signX = 1;
        } else {
            signX = -1;
        }
        //int finalMessageX = Integer.parseInt(finalMessage.substring(1,5))*signX;
        int signY;
        if(finalMessage.substring(4,5).equals("+")) {
            signY = 1;
        } else {
            signY = -1;
        }

        //int finalMessageY = Integer.parseInt(finalMessage.substring(6))*signY;
        //String x =
        int finalMessageX = java.lang.Integer.parseInt(finalMessage.substring(1,4));
        int finalMessageY = java.lang.Integer.parseInt(finalMessage.substring(5,8));
        //activityMediator.drawNewStrokeOnCanvasView();
        finalMessageX*=signX;
        finalMessageY*=signY;
        Log.e("here: ", "X: "+finalMessageX + "Y: "+finalMessageY);
        //Log.e("Y: ", ""+finalMessageY);
        //xCoordinate += finalMessageX;
        //yCoordinate += finalMessageY;

        if(((xCoordinate + finalMessageX)!=xCoordinate)||((yCoordinate + finalMessageY)!=yCoordinate)) {
            xCoordinate+=finalMessageX;
            yCoordinate+=finalMessageY;
            thisStroke.addCoordinate(xCoordinate,yCoordinate);

            activityMediator.drawNewStrokeOnCanvasView(thisStroke);
        }

        //Log.e("Y: ", ""+finalMessageY);
    }

    private class BluetoothConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public BluetoothConnectedThread(BluetoothSocket bs) {
            bluetoothSocket = bs;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch(IOException e) {
                Log.e("connectedThread", "Cant get streams");
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            Log.e("DEBUG", "started to run");
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while(true) {
                try {
                    bytes += inputStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "\n".getBytes()[0]) {
                            //Log.e("getting it", "here");
                            //handler.obtainMessage(1, begin, i, buffer).sendToTarget();

                            if((buffer[begin]!=0)&&(buffer[begin+1]!=0)) {
                                System.out.println("X: "+ buffer[begin] + "Y: " +buffer[begin+1]);
                                xCoordinate += buffer[begin];
                                yCoordinate += buffer[begin+1];

                                thisStroke.addCoordinate((float)xCoordinate,(float)yCoordinate);

                                activityMediator.drawNewStrokeOnCanvasView(thisStroke);
                            }

                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    //Log.e("not getting shit",":P");
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch(IOException e) {

            }
        }

        public void close() {
            try {
                bluetoothSocket.close();
            } catch(IOException e) {
                Log.e("Exception", "closed connection exception");
            }
        }

    }
}