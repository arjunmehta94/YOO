package e.feather_con_1.device;

import android.util.Log;

/**
 * Created by anurag on 10/8/15.
 */
public class ReadQueueThread implements Runnable{

    MessageBuffer messageBuffer;
    DeviceListenerInterface deviceListenerInterface;

    public ReadQueueThread(MessageBuffer messageBuffer, DeviceListenerInterface deviceListenerInterface) {
        this.messageBuffer = messageBuffer;
        this.deviceListenerInterface = deviceListenerInterface;
    }

    @Override
    public void run() {
        Log.e("readQueueThread", "started run");
        synchronized (messageBuffer) {
            while(true) {
                while (!messageBuffer.isChanged()) {
                    try {
                        messageBuffer.wait();
                    } catch (InterruptedException ignored) {}
                }
                //Log.e("run","message buffer changed");
                //deviceListenerInterface.handleDeviceInput(messageBuffer.deQueueAll());
                messageBuffer.resetIsChanged();
            }
        }
    }
}