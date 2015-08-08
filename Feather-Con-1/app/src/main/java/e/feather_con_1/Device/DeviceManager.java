package e.feather_con_1.Device;

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
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anurag on 6/8/15.
 */
public class DeviceManager {
    private static DeviceManager deviceManager;

    private DiscoveryList discoveryList;
    private static final String discoveryListTag = "DiscoveryListTag";
    private boolean LOLLIPOP;

    private BluetoothAdapter mBluetoothAdapter;
    private static Context mContext;

    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;

    private DeviceManager() {
        LOLLIPOP = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static DeviceManager getInstance() {
        return deviceManager;
    }

    public static DeviceManager getInstance(Context c) {
        if(deviceManager == null) {
            deviceManager = new DeviceManager();
        }
        mContext = c;
        return deviceManager;
    }

    public boolean bleConnect() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            return false;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        //if it is enabled, why disable it?
        if (mBluetoothAdapter.isEnabled()){
            Log.e("already enabled", ":?");
            mBluetoothAdapter.disable();

            while(mBluetoothAdapter.isEnabled()) {
                try {
                    Thread.sleep(100);
                } catch(Exception e) {}
            }
        }

        //why check if mBluetoothAdapter is null?
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableBtIntent, -5);

            while(!mBluetoothAdapter.isEnabled()) {
                try {
                    Thread.sleep(100);
                } catch(Exception e) {}
            }
            startDiscovery();
        }
        return true;
    }

    private void startDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//API>21
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }

        discoveryList = new DiscoveryList();
        discoveryList.show(((FragmentActivity) mContext).getSupportFragmentManager(), discoveryListTag);

        if (discoveryList != null){
            scanLeDevice(mBluetoothAdapter.isEnabled());
        }
    }

    private void scanLeDevice(boolean enabled) {
        if (mBLEScanner == null){
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        if(enabled) {
            if(LOLLIPOP) {
                mBLEScanner.startScan(filters, settings, mScanCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            Log.e("not enabled","not enabled");
            if (LOLLIPOP) {
                mBLEScanner.stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,byte[] scanRecord)
        {

            discoveryList.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        //bleDeviceList.add(device.getName());
                        //Log.e("device", device.getName());
                        discoveryList.add(device);
                    }


//                    mLeDeviceListAdapter.addDevice(device);
//
//                    mLeDeviceListAdapter.notifyDataSetChanged();

                }
            });
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice btDevice = result.getDevice();
            if (btDevice != null){
                //bleDeviceList.add(btDevice.getName());
                Log.i("callbackType", String.valueOf(callbackType));
                Log.i("result", result.toString());
                discoveryList.add(btDevice);
            }
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


}
