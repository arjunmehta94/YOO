package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by arjunmehta94 on 8/12/15.
 */
public class DeviceManager {
    Activity activity;
    private static DeviceManager deviceManagerInstance;
    private BluetoothAdapter mBluetoothAdapter;
    public static final String CONNECT_RESULT = "connect_result";
    private boolean isConnected = false;    //todo: when connected, make this true and false otherwise
    private static final String SAVED_INSTANCE_IS_CONNECTED = "is_connected";

    private boolean IS_LOLLIPOP_OR_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;

    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private DeviceManager(final Activity activity) throws BluetoothNotSupportedException {
        this.activity = activity;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null ||
                !activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new BluetoothNotSupportedException();
        }
    } 

    public BluetoothAdapter getBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    public static DeviceManager getInstance(Activity activity) throws BluetoothNotSupportedException {
        if(deviceManagerInstance==null) {
            deviceManagerInstance = new DeviceManager(activity);
        }
        return deviceManagerInstance;
    }

    public void startConnectionProcedure(boolean allowAutoConnect, int REQUEST_CODE) {
        if(allowAutoConnect && isConnected) {
            return;
        }
        Intent intent = new Intent(activity, DeviceConnectActivity.class);
        intent.putExtra(DeviceConnectActivity.ARG_ALLOW_TO_AUTO_CONNECT, allowAutoConnect);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public void onDestroy() {
        //todo..
        deviceManagerInstance = null;
    }

    public static class BluetoothNotSupportedException extends Exception {}

    public void onSavedInstanceState(Bundle out) {
        out.putBoolean(SAVED_INSTANCE_IS_CONNECTED, isConnected);
    }

    public void onRestoreInstanceState(Bundle in) {
        isConnected = in.getBoolean(SAVED_INSTANCE_IS_CONNECTED);
    }
}
