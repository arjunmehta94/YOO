package e.feather_con_1.Device;

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
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by anurag on 6/8/15.
 */
public class DeviceManager {
    private static DeviceManager deviceManager;

    private DiscoveryList discoveryList;
    private static final String discoveryListTag = "DiscoveryListTag";
    private boolean LOLLIPOP;

    private BluetoothAdapter mBluetoothAdapter;
    private static Context mContext;

    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;

    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private Handler mHandler;

    private DeviceManager() {
        LOLLIPOP = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;
        if(LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice btDevice = result.getDevice();
                    if (btDevice != null){
                        //bleDeviceList.add(btDevice.getName());
                        Log.i("callbackType", String.valueOf(callbackType));
                        Log.i("result", result.toString());
                        discoveryList.add(btDevice);
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    Log.e("here", "output");
//                        for (ScanResult sr : results) {
//                            Log.e("here", "output");
//                            Log.e("ScanResult - Results", sr.toString());
//                        }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    Log.e("Scan Failed", "Error Code: " + errorCode);
                }
            };
        } else {
            mLeScanCallback = new BluetoothAdapter.LeScanCallback()
            {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,byte[] scanRecord)
                {

                discoveryList.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (device != null) {
                            //bleDeviceList.add(device.getName());
                            //Log.e("device", device.getName());
                            discoveryList.add(device);
                        }

//                    mLeDeviceListAdapter.addDevice(device);
//
//                    mLeDeviceListAdapter.notifyDataSetChanged();

                    }
                });
                }
            };
        }
    }

    public static DeviceManager getInstance() {
        return deviceManager;
    }

    public static DeviceManager getInstance(Context c) {
        if(deviceManager == null) {
            deviceManager = new DeviceManager();
        }
        mContext = c;
        return deviceManager;
    }

    public boolean bleConnect() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            return false;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        //if it is enabled, why disable it?
        if (mBluetoothAdapter.isEnabled()){
            Log.e("already enabled", ":?");
            mBluetoothAdapter.disable();

            while(mBluetoothAdapter.isEnabled()) {
                try {
                    Thread.sleep(100);
                } catch(Exception e) {}
            }
        }



        //why check if mBluetoothAdapter is null?
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableBtIntent, 100);

            discoveryList = new DiscoveryList();
            discoveryList.show(((FragmentActivity) mContext).getSupportFragmentManager(), discoveryListTag);

            //startDiscovery();
        }

        mHandler = new Handler();

        return true;
    }

    public void startDiscovery() {
        if (LOLLIPOP) {//API>21
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }
        if (discoveryList != null){
            scanLeDevice(mBluetoothAdapter.isEnabled());
        }
    }

    int timer = 0;



    private void scanLeDevice(boolean enabled) {

        if(enabled) {

            Log.e("started scanning","here");
            if (mHandler == null){
                mHandler = new Handler();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (LOLLIPOP) {
                        if (mBLEScanner == null){
                            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                        }
                        mBLEScanner.stopScan(mScanCallback);
                    } else {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    try {
                        Log.e("scan sleeping","here");
                        if(timer<12) {
                            Thread.sleep(3000);
                            scanLeDevice(true);
                        } else {
                            return;
                        }
                        timer++;
                    } catch (Exception e) {}
                }
            }, 2000);

            if(LOLLIPOP) {
                if (mBLEScanner == null){
                    mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }
                mBLEScanner.startScan(filters, settings, mScanCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            Log.e("not enabled","not enabled");
            if (LOLLIPOP) {
                if (mBLEScanner == null){
                    mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }
                mBLEScanner.stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    public void establishConnection(BluetoothDevice bluetoothDevice) {
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(mContext, false, bleGattCallback);
    }

    public static final UUID CCCD = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID
            .fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_CHAR_UUID = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");


    private final BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);
            Log.i("inside", "onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED){
                //Toast.makeText(getActivity(), "phuck", Toast.LENGTH_SHORT).show();
                Log.i("inside inside", "bluetooth profile state connected");
                Log.i("inside inside inside", "" + gatt.discoverServices());
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
            System.out.println("x: " + val[0]);
            System.out.println("y: " + val[1]);
            System.out.println("penupdown: " + val[2]);
        }
    };
}
