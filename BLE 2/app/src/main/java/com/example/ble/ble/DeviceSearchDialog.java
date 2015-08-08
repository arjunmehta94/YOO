package com.example.ble.ble;

import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DeviceSearchDialog extends DialogFragment {

    ListView list;
    ListAdapterCustom adapterCustom;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private static final int SCAN_PERIOD = 10000;
    private ScanCallback mScanCallback; // callback for API level >= Build.VERSION_CODES.LOLLIPOP
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_CustomDialog);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();

        }

        //Initialize Handler
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_search_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = (ListView)view.findViewById(R.id.list);
        if(adapterCustom==null) {
            adapterCustom = new ListAdapterCustom(getActivity(), this);
        }
        list.setAdapter(adapterCustom);


        //scanLeDevice(true);

    }

    private static class ListAdapterCustom extends BaseAdapter {

        DeviceSearchDialog parentFragment;
        Context context;
        List<BluetoothDevice> lst;
        public ListAdapterCustom(Context context, DeviceSearchDialog parentFragment) {
            this.parentFragment = parentFragment;
            this.context = context;
            lst = new LinkedList<>();
        }

        @Override
        public int getCount() {
            return lst.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return lst.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean add(BluetoothDevice device){
            boolean b = !lst.contains(device) && lst.add(device);
            if (b){
                this.notifyDataSetChanged();
            }
            return b;
        }

        public boolean contains(BluetoothDevice device){
            if(!lst.contains(device)){
                return false;
            }
            return true;
        }

        public void removeAll(){
            if(lst.size() > 0){
                for (int i = 0; i<lst.size();i++){
                    lst.remove(i);
                }
            }
        }
        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final BluetoothDevice curr_device = getItem(position);
            View v = convertView;
            if(v==null) {
                v = ((LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).
                        inflate(R.layout.devicerow, parent, false);
            }
            ((TextView)v.findViewById(R.id.device_name)).setText(curr_device.getName());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentFragment.bluetoothDeviceCLicked(curr_device);
                }
            });
            return  v;
        }
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.i("Scanning", "scanning");
            if (mHandler == null){
                mHandler = new Handler();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        if (mBLEScanner == null){
                            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                        }
                        mBLEScanner.stopScan(mScanCallback);
                    }
                }
            }, SCAN_PERIOD);
            Log.i("finished", "handler finished");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                if (mBLEScanner == null) {
                    mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }

                mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {

                        BluetoothDevice btDevice = result.getDevice();
                        if (btDevice != null && !adapterCustom.contains(btDevice)){
                            //bleDeviceList.add(btDevice.getName());
                            Log.i("callbackType", String.valueOf(callbackType));
                            Log.i("result", result.toString());
                            adapterCustom.add(btDevice);
                        }
                         //connectToDevice(btDevice);
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        Log.e("here", "output");
//                        for (ScanResult sr : results) {
//                            Log.e("here", "output");
//                            Log.e("ScanResult - Results", sr.toString());
//                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        Log.e("Scan Failed", "Error Code: " + errorCode);
                    }
                };




                mBLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                if (mBLEScanner == null){
                    mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }
                mBLEScanner.stopScan(mScanCallback);
            }
        }
    }

 // callback for API level < Build.VERSION_CODES.LOLLIPOP

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,byte[] scanRecord)
        {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null && !adapterCustom.contains(device)) {
                        //bleDeviceList.add(device.getName());
                        //Log.e("device", device.getName());
                        adapterCustom.add(device);
                    }


//                    mLeDeviceListAdapter.addDevice(device);
//
//                    mLeDeviceListAdapter.notifyDataSetChanged();

                }
            });
        }
    };

    private void bluetoothDeviceCLicked(BluetoothDevice device) {
        //todo..
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        adapterCustom.removeAll();
        adapterCustom.notifyDataSetChanged();
    }

}
