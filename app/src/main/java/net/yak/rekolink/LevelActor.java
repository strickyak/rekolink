package net.yak.rekolink;

/**
 * LevelActor is a Thread that reads Integer target levels from an input queue
 * and calls stepUp() or stepDown() until the current level
 * (set by the subclass) equals the target level.
 * <p>
 * Each target level is achieved before the next target level
 * is read from the queue.
 * <p>
 * A target of STOP_LEVEL causes the thread to exit (without calling
 * any stepUp() or stepDown()).
 */

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

abstract public class LevelActor extends Thread {
    // Subclass Responsibility.
    abstract void stepUp();

    abstract void stepDown();

    // Public Constants.
    public final static int STOP_LEVEL = -1;

    // fields needed by subclasses.
    protected int level;
    protected int goal;

    // Private objects.
    private String name;
    private BlockingQueue<Integer> queue;

    // ctor
    public LevelActor(String name) {
        super(name);
        this.name = name;
        this.queue = new ArrayBlockingQueue<Integer>(100);
    }

    // WHY DO WE NEED A QUEUE OF GOALS, rather than just one current goal?
    public void setGoal(int goal) {
        Log.d("PTT", name + ": setGoal " + goal);
        queue.add(goal);
    }

    @Override
    public void run() {
        while (true) {
            try {
                goal = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (goal == STOP_LEVEL) {
                return;
            }
            while (level != goal) {
                if (level < goal) {
                    level++;
                    Log.e("PTT", name + ": stepUp " + level);
                    stepUp();
                } else {
                    level--;
                    Log.e("PTT", name + ": stepDown " + level);
                    stepDown();
                }
            }
        }
    }
}
