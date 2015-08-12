package e.feather_con_1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.List;

import e.feather_con_1.Device.Coordinate;
import e.feather_con_1.Device.DeviceListenerInterface;
import e.feather_con_1.Device.DeviceManager;
import e.feather_con_1.Device.DeviceManager1;

public class MainActivity extends FragmentActivity implements DeviceListenerInterface{
    private DeviceManager1 deviceManager1;
    private boolean mReturningWithResult;
    private int mResultCode;
    private int mRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceManager1 = DeviceManager1.getInstance(this);//pass context
        deviceManager1.setDeviceListenerInterface(this);

        if(!deviceManager1.bleConnect()) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onResume(){
//        deviceManager1.startDeviceManager();
//        super.onResume();
//    }
//
//    @Override
//    public void onStart(){
//        deviceManager1.startDeviceManager();
//        super.onStart();
//    }
//    @Override
//    public void onPause(){
//        deviceManager1.endDeviceManager();
//        super.onPause();
//    }
//
//    @Override
//    public void onStop(){
//        deviceManager1.endDeviceManager();
//        super.onStop();
//    }
}
