package e.feather_con_1.device;

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
        if (messageQueue.size() < MAX_ENTRIES) {
            messageQueue.add(entry);
        }
        isChanged = true;
        this.notifyAll();
    }

    public synchronized List<E> deQueueAll() {
        List<E> entryList = new LinkedList<E>();
        while (!messageQueue.isEmpty()) {
            entryList.add(messageQueue.poll());
        }
        setIsChangedToFalse();
        return entryList;
    }

    public synchronized void setIsChangedToFalse() {
        isChanged = false;
    }

    public synchronized boolean isChanged() {
        return isChanged;
    }
}