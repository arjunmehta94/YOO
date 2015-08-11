package e.feather_con_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import e.feather_con_1.Device.Coordinate;
import e.feather_con_1.Device.DeviceListenerInterface;
import e.feather_con_1.Device.DeviceManager;

public class MainActivity extends FragmentActivity implements DeviceListenerInterface{
    private DeviceManager deviceManager;
    private boolean mReturningWithResult;
    private int mResultCode;
    private int mRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceManager = DeviceManager.getInstance(this);//pass context
        deviceManager.setDeviceListenerInterface(this);

        if(!deviceManager.bleConnect()) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            DialogFragment f = (DialogFragment) getSupportFragmentManager().findFragmentByTag("DiscoveryListTag");
            if (resultCode == Activity.RESULT_OK) {
                Log.e("clicked OK", "clicked OK");
                if (f != null) {
                    deviceManager.startDiscovery();
                }
            } else {
                f.dismissAllowingStateLoss();
            }
        }
    }

    long prevTime = 0;
    @Override
    public void handleDeviceInput(List<Coordinate> coordinates) {
        //todo
        for(Coordinate coordinate : coordinates) {
            System.out.println(coordinate.toString());
        }
        long currentTime = System.currentTimeMillis();

        System.out.println("timeDiff: " + ((Long) currentTime - prevTime));
        prevTime = currentTime;
    }
}
