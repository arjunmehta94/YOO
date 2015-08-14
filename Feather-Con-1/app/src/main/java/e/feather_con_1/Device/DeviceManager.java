package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
    BluetoothDevice bluetoothDevice;

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

    public void imminentConnectDevice(BluetoothDevice device) {
        this.bluetoothDevice = device;
    }

    public static final UUID CCCD = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID
            .fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_CHAR_UUID = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");


    public final BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);
            Log.i("inside", "onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //Toast.makeText(getActivity(), "phuck", Toast.LENGTH_SHORT).show();
                Log.i("inside inside", "bluetooth profile state connected");
                Log.i("inside inside inside", "" + gatt.discoverServices());
                isConnected = true;

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("inside", "Bluetooth disconnected");
                isConnected = false;
                gatt.close();
                //discoveryList.dismiss();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("inside", "onServicesDiscovered");
            BluetoothGattService RxService = gatt
                    .getService(RX_SERVICE_UUID);
            Log.i("inside", "RxService created");
            if (RxService == null) {

                //showMessage("Rx service not found!");
                Log.i("inside", "Rx service not found!");
                //broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
                return;
            }

            BluetoothGattCharacteristic TxChar = RxService
                    .getCharacteristic(TX_CHAR_UUID);
            Log.i("inside", "TxChar");
            if (TxChar == null) {
                //showMessage("Tx charateristic not found!");
                Log.i("inside", "Tx characteristic not found!");
                //broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
                return;
            }
            Log.i("inside", "before setCharacteristicNotification");
            gatt.setCharacteristicNotification(TxChar, true);

            BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            byte[] val = characteristic.getValue();
            //System.out.println("x: " + val[0]);
            //System.out.println("y: " + val[1]);
            //System.out.println("penupdown: " + val[2]);
            Coordinate coordinate = new Coordinate(val[0],val[1],val[2]);
            //messageBuffer.enQueue(coordinate);
        }
    };
}
