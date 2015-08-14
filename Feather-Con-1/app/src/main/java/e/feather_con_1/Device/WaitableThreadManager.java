package e.feather_con_1.device;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rishabhpoddar on 10/08/15.
 */
public class WaitableThreadManager {
    private final List<WeakReference<WaitableThread>> threads = new LinkedList<>();

    public void add_to_list(WaitableThread thread) {
        synchronized (threads) {
            threads.add(new WeakReference<>(thread));
        }
    }

    public void wait_for_threads_to_finish() {
        while (true) {
            WeakReference<WaitableThread> threadWeakReference;
            synchronized (threads) {
                if (threads.isEmpty()) {
                    break;
                }
                threadWeakReference = threads.remove(0);
            }
            WaitableThread thread = threadWeakReference.get();
            if (thread == null) {
                continue;
            }
            try {
                thread.join_calling_thread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
