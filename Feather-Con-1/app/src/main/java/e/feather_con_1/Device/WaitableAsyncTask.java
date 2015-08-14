package e.feather_con_1.device;

import android.os.AsyncTask;

/**
 * Created by rishabhpoddar on 10/08/15.
 */
public abstract class WaitableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
        implements WaitableThread {

    private final Object lock;
    private boolean isDead = false;
    private boolean kill_signal_sent = false;

    public WaitableAsyncTask(WaitableThreadManager manager) {
        manager.add_to_list(this);
        lock = new Object();
    }

    @Override
    protected final Result doInBackground(Params... params) {
        Result r = backgroundJob(params);
        synchronized (lock) {
            isDead = true;
            lock.notifyAll();
        }
        return r;
    }

    @Override
    protected final void onPostExecute(Result result) {
        synchronized (lock) {
            if (!kill_signal_sent) {
                postJob(result);
            }
        }
    }

    protected abstract Result backgroundJob(Params... params);

    protected void postJob(Result result) {
    }

    @Override
    public final void join_calling_thread() throws InterruptedException {
        synchronized (lock) {
            kill_signal_sent = true;
            while (!isDead) {
                lock.wait();
            }
        }
    }

    protected final boolean kill_signal_sent() {
        return kill_signal_sent;
    }

}
