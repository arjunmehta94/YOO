package e.feather_con_1.device;


/**
 * Created by anurag on 10/8/15.
 */
public class ReadQueueThread extends WaitableRunnable {

    final MessageBuffer messageBuffer;

    public ReadQueueThread(MessageBuffer messageBuffer, WaitableThreadManager manager) {
        super(manager);
        this.messageBuffer = messageBuffer;
    }

    @Override
    protected void doJob() {
        while (!kill_signal_sent()) {
            synchronized (messageBuffer) {
                while (!messageBuffer.isChanged()) {
                    try {
                        messageBuffer.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                messageBuffer.deQueueAll();
                //deviceListenerInterface.handleDeviceInput(messageBuffer.deQueueAll());
            }
        }
    }

}