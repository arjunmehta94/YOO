package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import e.feather_con_1.R;

public class DeviceConnectActivity extends Activity {

    public static final String ARG_ALLOW_TO_AUTO_CONNECT = "allow_to_auto_connect";
    private boolean allow_to_auto_connect;
    private ListView deviceListView;
    private ListAdapterCustom adapterCustom;
    private BluetoothAdapter mBluetoothAdapter;
    DeviceManager deviceManager;
    private static final int REQUEST_BLUETOOTH_CODE = 1;

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
                    startScan();
                } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    return_fail();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allow_to_auto_connect = getIntent().getBooleanExtra(ARG_ALLOW_TO_AUTO_CONNECT, true);
        try {
            deviceManager = DeviceManager.getInstance(this);
            mBluetoothAdapter = deviceManager.getBluetoothAdapter();
        } catch (DeviceManager.BluetoothNotSupportedException e) {
            e.printStackTrace();
            return_fail();
        }
        startConnectProcedure();
    }

    private void startConnectProcedure(){
        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_CODE);
        } else {
            startScan();
        }
    }

    private void startScan() {
        setContentView(R.layout.activity_device_connect);
        adapterCustom = new ListAdapterCustom(this);
        deviceListView = (ListView)findViewById(R.id.list);
        deviceListView.setAdapter(adapterCustom);
        //todo..
    }

    public void rescanClicked(View view) {
        //todo
    }

    public void listItemClicked(BluetoothDevice device) {
        //todo
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_BLUETOOTH_CODE) {
            if(resultCode == Activity.RESULT_CANCELED) {
                return_fail();
            }
        }
    }

    private void return_fail() {
        Intent intent = new Intent();
        intent.putExtra(DeviceManager.CONNECT_RESULT, false);
        this.setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }
}
