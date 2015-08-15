package e.feather_con_1.device;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import java.util.UUID;

/**
 * Created by rishabhpoddar on 14/08/15.
 */
public class BluetoothGattCallbackCustom extends BluetoothGattCallback {

    private static final UUID CCCD = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID RX_SERVICE_UUID = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID RX_CHAR_UUID = UUID
            .fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private static final UUID TX_CHAR_UUID = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private DeviceManager deviceManager;

    public BluetoothGattCallbackCustom(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
            deviceManager.device_connected();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            gatt.close();
            deviceManager.device_disconnected();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        BluetoothGattService RxService = gatt.getService(RX_SERVICE_UUID);
        if (RxService == null) {
            return;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
        if (TxChar == null) {
            return;
        }
        gatt.setCharacteristicNotification(TxChar, true);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        byte[] val = characteristic.getValue();
    }
}
