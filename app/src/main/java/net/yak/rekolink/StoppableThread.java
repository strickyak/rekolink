package net.yak.rekolink;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

abstract public class StoppableThread extends Thread {
    private BlockingQueue<Integer> stopper;  // Null until time to stop.
    private String name;

    abstract public void step();  // Subclasses define step, not run.

    abstract public void shutdown();  // Invoked on subclass when stopped

    public StoppableThread(String name) {
        super(name);
        this.name = name;
        Log.d("PTT", "Constructing StopThr: " + name);
    }

    @Override
    public void start() {
        Log.d("PTT", "Starting StopThr: " + name);
        super.start();
    }

    @Override
    public final void run() {
        Log.d("PTT", "Running StopThr: " + name);
        BlockingQueue<Integer> q;
        while (true) {
            synchronized (this) {
                q = stopper;
            }
            if (q != null) {
                break;
            }
            step();
        }

        Log.d("PTT", "Shutting Down StopThr: " + name);
        // Adding to the stopper queue signals that we are stopped.
        shutdown();
        q.add(0);
    }

    public final void stopAndWait() {
        synchronized (this) {
            // Request thread to stop, by setting stopper.
            stopper = new ArrayBlockingQueue<Integer>(1);
        }
        while (true) {
            try {
                // Wait until thread stops.
                stopper.take();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}