package e.feather_con_1.Device;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by anurag on 10/8/15.
 */
public class MessageBuffer<E> {
    private Queue<E> messageQueue;
    private volatile boolean isChanged = false;
    int MAX_ENTRIES = 2048;

    public MessageBuffer() {
        messageQueue = new LinkedList<E>();
    }

    public synchronized void enQueue(E entry) {
        //Log.e("enQueue","coordinate enqueued");
        if (messageQueue.size() < MAX_ENTRIES) {
            messageQueue.add(entry);
        }
        isChanged = true;
        this.notifyAll();
    }




    public synchronized List<E> deQueueAll() {
        //Log.e("deQueueAll","deQueueing");
        List<E> entryList = new LinkedList<E>();
        System.out.println(messageQueue.size());
        while(!messageQueue.isEmpty()) {
            entryList.add(messageQueue.poll());
        }
        return entryList;
    }

    public synchronized void resetIsChanged() {
        isChanged = false;
    }

    public synchronized boolean isChanged() {
        return  isChanged;
    }
}