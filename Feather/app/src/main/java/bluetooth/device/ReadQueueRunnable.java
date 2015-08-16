package bluetooth.device;


/**
 * Created by anurag on 10/8/15.
 */
public class ReadQueueRunnable implements Runnable {

    private final MessageBuffer<DeviceData> messageBuffer;
    private DeviceManager deviceManager;
    private boolean kill_signal_sent;

    public ReadQueueRunnable(MessageBuffer<DeviceData> messageBuffer, DeviceManager deviceManager) {
        this.messageBuffer = messageBuffer;
        this.deviceManager = deviceManager;
        this.kill_signal_sent = false;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (messageBuffer) {
                while (!messageBuffer.isChanged() && !kill_signal_sent) {
                    try {
                        messageBuffer.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (kill_signal_sent) {
                break;
            }
            deviceManager.dataReadFromReadThread(messageBuffer.deQueueAll());
        }
    }

    public void kill() {
        kill_signal_sent = true;
        synchronized (messageBuffer) {
            messageBuffer.notifyAll();
        }
    }

}