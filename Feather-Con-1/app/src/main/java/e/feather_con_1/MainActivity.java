package e.feather_con_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import e.feather_con_1.device.DeviceData;
import e.feather_con_1.device.DeviceListenerInterface;
import e.feather_con_1.device.DeviceManager;

public class MainActivity extends Activity implements DeviceListenerInterface{

    private static final int REQUEST_CODE = 1;
    DeviceManager deviceManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            deviceManager = DeviceManager.getInstance(this, this);
        } catch (DeviceManager.BluetoothNotSupportedException e) {
            e.printStackTrace();
            Log.e("inside", "bluetooth not supported");
        }
        if(deviceManager!=null) {
            if(savedInstanceState!=null) {
                deviceManager.onRestoreInstanceState(savedInstanceState);
            }
            deviceManager.autoScanOrReconnect(REQUEST_CODE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(deviceManager!=null) {
            deviceManager.onSavedInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                boolean success = data.getBooleanExtra(DeviceManager.CONNECT_RESULT, false);
                if(!success) {
                    Toast.makeText(this, "no device is connected", Toast.LENGTH_SHORT).show();
                } else {
                    String mac_address = data.getStringExtra(DeviceManager.CONNECT_MAC_ADDRESS);
                    deviceManager.establishNewConnection(mac_address);
                }
            }
        }
    }

    public void connectToDevice(View view) {
        if(deviceManager!=null) {
            deviceManager.startConnectionProcedure(false, REQUEST_CODE);
        }
    }

    @Override
    public void onDestroy() {
        deviceManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void device_connected(boolean connected_to_old_device) {
        if(!connected_to_old_device) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "connected!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void device_disconnected() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "not connected!!!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void process_device_input(List<DeviceData> dataList) {
        for(DeviceData data : dataList) {
            System.out.println(data);
        }
    }

    public void disconnect(View view) {
        if(deviceManager!=null) {
            deviceManager.disconnectDevice();
        }
    }
}
