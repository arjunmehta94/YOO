package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.ListView;

/**
 * Created by arjunmehta94 on 8/12/15.
 */
public class DeviceManager {
    Activity activity;
    private static DeviceManager deviceManagerInstance;
    private BluetoothAdapter mBluetoothAdapter;
    public static final String CONNECT_RESULT = "connect_result";


    private DeviceManager(Activity activity) throws BluetoothNotSupportedException {
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
        Intent intent = new Intent(activity, DeviceConnectActivity.class);
        intent.putExtra(DeviceConnectActivity.ARG_ALLOW_TO_AUTO_CONNECT, allowAutoConnect);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public void onDestroy() {
        //todo..
        deviceManagerInstance = null;
    }

    public static class BluetoothNotSupportedException extends Exception {}
}
