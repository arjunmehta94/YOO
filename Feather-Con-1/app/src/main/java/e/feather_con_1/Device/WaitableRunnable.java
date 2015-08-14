package e.feather_con_1.device;

/**
 * Created by rishabhpoddar on 10/08/15.
 */
public abstract class WaitableRunnable implements Runnable, WaitableThread {
    private boolean has_ended = false;
    private boolean kill_signal_sent = false;
    private final Object lock;

    public WaitableRunnable(WaitableThreadManager manager) {
        manager.add_to_list(this);
        lock = new Object();
    }

    @Override
    public final void run() {
        doJob();
        synchronized (lock) {
            has_ended = true;
            lock.notifyAll();
        }
    }

    public void join_calling_thread() throws InterruptedException {
        synchronized (lock) {
            kill_signal_sent = true;
            while (!has_ended) {
                lock.wait();
            }
        }
    }

    protected abstract void doJob();

    protected final boolean kill_signal_sent() {
        return kill_signal_sent;
    }
}
