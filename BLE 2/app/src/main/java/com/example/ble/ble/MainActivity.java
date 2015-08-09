package com.example.ble.ble;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends FragmentActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String dialogBoxTag = "thistag";
//    private static final int SCAN_PERIOD = 10000;
//    private List<String> bleDeviceList;
//    private ArrayAdapter<String> bleDeviceListAdapter;
//    private ListView bleDeviceListView;
//    private Dialog bleDeviceDialog;
    private BluetoothAdapter mBluetoothAdapter;
//    private BluetoothLeScanner mBLEScanner;
//    private ScanSettings settings;
//    private Handler mHandler;
//    private List<ScanFilter> filters;
    private Button button;
    private boolean btnState; //true if off, false if on
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        button = (Button)findViewById(R.id.button);
        btnState = true;

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        /*if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/

//        Use this check to determine whether BLE is supported on the device. Then
//        you can selectively disable BLE-related features.

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    public void click(View v){
        if (btnState){
            btnState = false;
            button.setText(R.string.button_off);
            // Ensures Bluetooth is available on the device and it is enabled. If not,
            // displays a dialog requesting user permission to enable Bluetooth.
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            DeviceSearchDialog d = new DeviceSearchDialog();
            d.show(getSupportFragmentManager(), dialogBoxTag);

        }
        else{
            mBluetoothAdapter.disable();
            btnState = true;
            button.setText(R.string.button_on);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == Activity.RESULT_OK){
                Fragment f = getSupportFragmentManager().findFragmentByTag(dialogBoxTag);
                if (f != null && f instanceof DeviceSearchDialog){
                    ((DeviceSearchDialog) f).scanLeDevice(true);
                }

            }
        }
    }
}
