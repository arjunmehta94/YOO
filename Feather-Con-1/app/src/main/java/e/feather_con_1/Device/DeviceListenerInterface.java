package e.feather_con_1.device;

/**
 * Created by anurag on 10/8/15.
 */
public interface DeviceListenerInterface {
    void device_connected(boolean connected_to_old_device);
    void device_disconnected();
}
