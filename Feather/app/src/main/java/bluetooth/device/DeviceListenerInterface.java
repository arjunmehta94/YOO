package bluetooth.device;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by anurag on 10/8/15.
 */
public interface DeviceListenerInterface {
    void device_connected(boolean connected_to_old_device, BluetoothDevice device);

    void device_disconnected();

    void process_device_input(List<DeviceData> data);
}
