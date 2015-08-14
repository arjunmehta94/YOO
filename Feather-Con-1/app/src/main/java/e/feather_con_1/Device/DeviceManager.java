package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by arjunmehta94 on 8/12/15.
 */
public class DeviceManager {
    Activity activity;
    private static DeviceManager deviceManagerInstance;
    private BluetoothAdapter mBluetoothAdapter;
    public static final String CONNECT_RESULT = "connect_result";
    public static final String CONNECT_MAC_ADDRESS = "mac_address";
    private volatile boolean isConnected = false;
    private static final String SAVED_INSTANCE_IS_CONNECTED = "is_connected";
    private static final String SAVED_INSTANCE_MAC_ADDRESS = "mac_address";
    private String mac_address = "";
    DeviceListenerInterface deviceListenerInterface;
    private BluetoothGatt gatt;
    private boolean connected_to_old_device = false;

    private DeviceManager(final Activity activity, DeviceListenerInterface deviceListenerInterface)
            throws BluetoothNotSupportedException {
        this.deviceListenerInterface = deviceListenerInterface;
        this.activity = activity;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null ||
                !activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new BluetoothNotSupportedException();
        }
    }

    public static DeviceManager getInstance(Activity activity, DeviceListenerInterface deviceListenerInterface)
            throws BluetoothNotSupportedException {
        if (deviceManagerInstance == null) {
            deviceManagerInstance = new DeviceManager(activity, deviceListenerInterface);
        }
        return deviceManagerInstance;
    }

    public synchronized void startConnectionProcedure(boolean allowAutoConnect, int REQUEST_CODE) {
        if (isConnected()) {
            return;
        }
        Intent intent = new Intent(activity, DeviceConnectActivity.class);
        intent.putExtra(DeviceConnectActivity.ARG_ALLOW_TO_AUTO_CONNECT, allowAutoConnect);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public synchronized void onDestroy() {
        //todo..
        deviceListenerInterface = null;
        disconnectDevice();
        deviceManagerInstance = null;
    }

    public static class BluetoothNotSupportedException extends Exception {
    }

    public synchronized void onSavedInstanceState(Bundle out) {
        out.putBoolean(SAVED_INSTANCE_IS_CONNECTED, isConnected);
        out.putString(SAVED_INSTANCE_MAC_ADDRESS, mac_address);
    }

    public synchronized void onRestoreInstanceState(Bundle in) {
        isConnected = in.getBoolean(SAVED_INSTANCE_IS_CONNECTED);
        mac_address = in.getString(SAVED_INSTANCE_MAC_ADDRESS);
    }

    public synchronized void establishNewConnection(String mac_address) throws IllegalArgumentException {
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(mac_address);
        gatt = bluetoothDevice.connectGatt(activity, false, new BluetoothGattCallbackCustom(this));
        connected_to_old_device = this.mac_address.compareTo(mac_address)==0;
        this.mac_address = mac_address;
        setIsConnected(true);
    }

    public synchronized void re_establishConnection() {
        establishNewConnection(mac_address);
    }

    public synchronized void disconnectDevice() {
        if (isConnected()) {
            gatt.disconnect();
            gatt = null;
            this.mac_address = "";
        }
    }

    public synchronized void autoScanOrReconnect(int REQUEST_CODE) {
        if (isConnected()) {
            re_establishConnection();
        } else {
            startConnectionProcedure(true, REQUEST_CODE);
        }
    }

    public synchronized void device_connected() {
        if (deviceListenerInterface != null) {
            deviceListenerInterface.device_connected(connected_to_old_device);
        }
    }

    public synchronized void device_disconnected() {
        setIsConnected(false);
        if (deviceListenerInterface != null) {
            deviceListenerInterface.device_disconnected();
        }
    }

    public synchronized boolean isConnected() {
        return isConnected;
    }

    private synchronized void setIsConnected(boolean bool) {
        isConnected = bool;
    }

}
