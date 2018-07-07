//package net.yak.rekolink;
//
//import android.content.Context;
//import android.util.Log;
//import android.widget.Toast;
//
///**
// * Created by strick on 10/22/17.
// */
//class StepMachine implements Runnable {
//    private int level;
//    private int goal;
//    private Step[] steps;
//    private Context context;
//    private String name;
//
//    public StepMachine(Context context, Step[] steps, String name) {
//        this.context = context;
//        this.steps = steps;
//        this.name = name;
//    }
//
//    final void toast(String s) {
//        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
//    }
//
//    void sleep(int milliseconds) {
//        try {
//            Thread.sleep(milliseconds);
//        } catch (InterruptedException ignore) {
//            ;
//        }
//    }
//
//    final synchronized void setGoal(int g) {
//        Log.i("PTT", name + ": Level=" + level + " Goal=" + goal + "; SETTING Goal " + g);
//        synchronized (this) {
//            this.goal = g;
//            this.notifyAll();
//        }
//    }
//    final synchronized int getLevel() {
//        return level;
//    }
//
//
//    final Step nextStepAfter(int x) {
//        for (Step s : steps) {
//            // Return the first one you find, so they happen in order.
//            if (s.step > x) return s;
//        }
//        return null;
//    }
//
//    final Step stepBeforeOrAt(int x) {
//        Step z = null;
//        for (Step s : steps) {
//            // Return the last one you find, so they happen in backwards order.
//            if (s.step <= x) z = s;
//        }
//        return z;
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            int g;
//            synchronized (this) {
//                Log.i("PTT", name + ": Level " + level + " Getting Goal " + goal);
//                g = goal;
//            }
//
//            while (g > level) {
//                Step t = nextStepAfter(level);
//                if (t != null && t.step <= g) {
//                    Log.i("PTT", name + ": Level " + level + " Goal " + g + " Do Step " + t.step + ":" + t.description);
//                    String complaint = t.doer.run();
//                    if (complaint == "") {
//                        Log.i("PTT", name + ": Did Step " + t.step);
//                    } else {
//                        Log.e("PTT", name + ": Did Step " + t.step + " CRASHES :: " + complaint);
//                        toast(name + ": Did Step " + t.step + " CRASHES :: " + complaint);
//                    }
//                    sleep(100);
//                    level = t.step;
//                } else {
//                    break;
//                }
//            }
//            while (g < level) {
//                Step t = stepBeforeOrAt(level);
//                if (t != null && t.step > g) {
//                    Log.i("PTT", name + ": Level " + level + " Goal " + g + " UNDO Step " + t.step + ":" + t.description);
//                    String complaint = t.undoer.run();
//                    if (complaint == "") {
//                        Log.i("PTT", name + ": Undid Step " + t.step);
//                    } else {
//                        Log.e("PTT", name + ": Undid Step " + t.step + " CRASHES :: " + complaint);
//                        toast(name + ": Undid Step " + t.step + " CRASHES :: " + complaint);
//                    }
//                    sleep(100);
//                    level = t.step - 1;
//                } else {
//                    break;
//                }
//            }
//            level = g;
//            sleep(1);
////            Log.i("PTT", "StepMachine Waiting...");
//            waitOrWhatever();
//        }
//    }
//
//    void waitOrWhatever() {
//        synchronized (this) {
//            try {
//                this.wait();
//            } catch (InterruptedException e) {
//                // It's OK if we continue through the loop.
//            }
//        }
//    }
//}
