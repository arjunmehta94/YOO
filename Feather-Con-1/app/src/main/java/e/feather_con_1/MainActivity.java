package e.feather_con_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import e.feather_con_1.device.DeviceManager;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    DeviceManager deviceManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            deviceManager = DeviceManager.getInstance(this);
        } catch (DeviceManager.BluetoothNotSupportedException e) {
            e.printStackTrace();
            Log.e("inside", "bluetooth not supported");
        }
        if(deviceManager!=null) {
            deviceManager.startConnectionProcedure(true, REQUEST_CODE);
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
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        if(deviceManager!=null) {
            deviceManager.onRestoreInstanceState(inState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                boolean success = data.getBooleanExtra(DeviceManager.CONNECT_RESULT, false);
                if(!success) {
                    Toast.makeText(this, "no device is connected", Toast.LENGTH_SHORT).show();
                } else {
                    //todo..
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


    //todo: what happens when you are already connected to a device and you press connect or auto connect is started????
}
