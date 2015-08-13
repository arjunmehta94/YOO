package e.feather_con_1.device;

import java.util.List;

/**
 * Created by anurag on 10/8/15.
 */
public interface DeviceListenerInterface {
    void handleDeviceInput(List<Coordinate> coordinates);
}
