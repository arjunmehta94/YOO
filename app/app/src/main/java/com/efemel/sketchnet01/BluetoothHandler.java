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

import java.io.IOException;
import java.io.InputStream;
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

    private Activity parentActivity;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private BluetoothConnectThread bluetoothConnectThread;

    private BluetoothHandler() {
        initialize();
    }

    public BluetoothHandler getInstance() {
        if(instance == null) {
            instance = new BluetoothHandler();
        }
        return instance;
    }

    private void initialize() {
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

        bluetoothConnectThread = new BluetoothConnectThread(bluetoothDevice);
        bluetoothConnectThread.start();

    }

    private class BluetoothConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000- 00805f9b34fb");

        public BluetoothConnectThread(BluetoothDevice bd) {
            BluetoothSocket tmp = null;
            bluetoothDevice = bd;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);

            } catch(IOException e) {
                Log.e("Exception", "BluetoothDevice IOException");
            }
            bluetoothSocket = tmp;
            try {
                tmpIn = bluetoothSocket.getInputStream();
                tmpOut = bluetoothSocket.getOutputStream();
            } catch(IOException e) {

            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                bluetoothSocket.connect();
            } catch(IOException e) {
                try {
                    bluetoothSocket.close();
                } catch(IOException e1) {
                    Log.e("Exception", "closed connection exception");
                }
            }

            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while(true) {
                try {
                    bytes += inputStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i=begin; i<bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            handler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch(IOException e) {
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch(IOException e) {

            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;
            }
        }
    };
}