package e.feather_con_1.Device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by arjunmehta94 on 8/12/15.
 */
public class DeviceManager {
    Context context;
    private static DeviceManager deviceManagerInstance;
    private BluetoothAdapter mBluetoothAdapter;

    private DeviceManager(Context context, int REQUEST_CODE) throws UnsupportedOperationException {
        this.context = context;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null ||
                !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new UnsupportedOperationException();
        }
    } 

    public static DeviceManager getInstance(Context context) throws UnsupportedOperationException {
        if(deviceManagerInstance==null) {
            int code = 0; //random just delete
            deviceManagerInstance = new DeviceManager(context, code);
        }
        return deviceManagerInstance;
    }

    public void startConnectionProcedure(boolean allowAutoConnect) {

    }
}
