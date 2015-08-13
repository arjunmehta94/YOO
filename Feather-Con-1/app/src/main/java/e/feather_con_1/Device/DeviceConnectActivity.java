package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import e.feather_con_1.BuildConfig;
import e.feather_con_1.R;

public class DeviceConnectActivity extends Activity {

    public static final String ARG_ALLOW_TO_AUTO_CONNECT = "allow_to_auto_connect";
    private boolean allow_to_auto_connect;
    private ListAdapterCustom adapterCustom;
    private BluetoothAdapter mBluetoothAdapter;
    DeviceManager deviceManager;
    private static final int REQUEST_BLUETOOTH_CODE = 1;
    private boolean IS_LOLLIPOP_OR_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private ScanCallbackCustom mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    public WeakReference<StopScanThread> stopScanRunnableWeakReference;
    public WeakReference<Thread> stopScanThreadWeakReference;

    private int number_of_times_startScan_is_called = 0;    //todo:remove ultimately

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    initialiseViewAndStartScan();
                } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    return_fail();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
        }
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

    private void startConnectProcedure() {
        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_CODE);
        } else {
            initialiseViewAndStartScan();
        }
    }

    private void initialiseViewAndStartScan() {
        number_of_times_startScan_is_called++;
        if (BuildConfig.DEBUG && number_of_times_startScan_is_called != 1) {
            throw new AssertionError();
        }   //todo: remove ultimately
        setContentView(R.layout.activity_device_connect);
        adapterCustom = new ListAdapterCustom(this);
        ((ListView) findViewById(R.id.list)).setAdapter(adapterCustom);
        startScan();
    }

    public void rescanClicked(View view) {
        startScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BLUETOOTH_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
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
    public void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        joinWithStopScanningThread();
        super.onDestroy();
    }

    private void joinWithStopScanningThread() {
        if (stopScanRunnableWeakReference != null && stopScanThreadWeakReference != null) {
            StopScanThread runnable = stopScanRunnableWeakReference.get();
            if (runnable != null) {
                runnable.wakeUpPlease();
            }
            Thread thread = stopScanThreadWeakReference.get();
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startScan() {
        show_progressbar();
        Log.e("started scanning", "here");
        if (IS_LOLLIPOP_OR_ABOVE) {
            if (mScanCallback == null) {
                mScanCallback = new ScanCallbackCustom(this);
            }
            mBluetoothAdapter.getBluetoothLeScanner().startScan(new ArrayList<ScanFilter>(), new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), mScanCallback);
        } else {
            if (mLeScanCallback == null) {
                mLeScanCallback = new LeScanCallbackCustom(this);
            }
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }

        StopScanThread stopScanThread = new StopScanThread(this);
        stopScanRunnableWeakReference = new WeakReference<>(stopScanThread);
        Thread thread = new Thread(stopScanThread);
        stopScanThreadWeakReference = new WeakReference<>(thread);
        thread.start();

    }

    private static class ScanCallbackCustom extends ScanCallback {

        DeviceConnectActivity activity;

        public ScanCallbackCustom(DeviceConnectActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice btDevice = result.getDevice();
            if (btDevice != null) {
                activity.adapterCustom.add(btDevice);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
            activity.return_fail();
        }
    }

    private static class LeScanCallbackCustom implements BluetoothAdapter.LeScanCallback {

        DeviceConnectActivity activity;

        public LeScanCallbackCustom(DeviceConnectActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        activity.adapterCustom.add(device);
                    }
                }
            });
        }
    }

    private static class StopScanThread implements Runnable {
        private final Object lock = new Object();
        private DeviceConnectActivity activity;
        private boolean wakeUp;
        private static final long SCAN_PERIOD = 5000;  //todo: change to 30,000;

        public StopScanThread(DeviceConnectActivity activity) {
            this.wakeUp = false;
            this.activity = activity;
        }

        @Override
        public void run() {
            boolean natural_wakeup;
            synchronized (lock) {
                long startTime = System.currentTimeMillis();
                while (true) {
                    try {
                        lock.wait(SCAN_PERIOD);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    natural_wakeup = (System.currentTimeMillis() - startTime) >= SCAN_PERIOD;
                    if (wakeUp || natural_wakeup) {
                        break;
                    }
                }
            }
            if (activity.IS_LOLLIPOP_OR_ABOVE) {
                activity.mBluetoothAdapter.getBluetoothLeScanner().stopScan(activity.mScanCallback);
            } else {
                activity.mBluetoothAdapter.stopLeScan(activity.mLeScanCallback);
            }
            if (natural_wakeup) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!activity.isDestroyed() && !activity.isFinishing()) {
                            activity.show_rescan_button();
                        }
                    }
                });
            }
        }

        public void wakeUpPlease() {
            synchronized (lock) {
                wakeUp = true;
                lock.notifyAll();
            }
        }
    }

    private void show_rescan_button() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.scanButton).setVisibility(View.VISIBLE);
    }

    private void show_progressbar() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.scanButton).setVisibility(View.GONE);
    }

    public void listItemClicked(BluetoothDevice device) {
        establishConnection(device);
    }

    public void establishConnection(BluetoothDevice bluetoothDevice) {
        deviceManager.imminentConnectDevice(bluetoothDevice);
        Intent intent = new Intent();
        intent.putExtra(DeviceManager.CONNECT_RESULT, true);
        this.setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
