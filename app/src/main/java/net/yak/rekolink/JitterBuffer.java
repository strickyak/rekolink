package net.yak.rekolink;

import android.util.Log;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by strick on 7/6/18.
 */

public class JitterBuffer {
    public JitterBuffer() {
        tree = new TreeMap();
    }

    synchronized public void Add(long millis, short[] audio) {
        tree.put(millis, audio);
        Log.d("JB", "Add k=" + millis + " len=" + audio.length + "->" + tree.size());
    }

    synchronized public short[] Take(long minMillis) {
        if (tree.isEmpty()) {
            return null;
        }
//        if (tree.size() < 3) {
//            return null;
//        }
        Long k = tree.firstKey();
//            if (k < minMillis) {
//                return null;
//            }
        short[] z = tree.remove(k);

        Log.d("JB", "Take k=" + k + " len=" + z.length + "->" + tree.size());
        return z;
    }

    synchronized public void setSkew(long a) {
        if (skew == 0) {
            skew = a;
        } else {
            skew = (3 * skew + a) / 4;
        }
    }

    synchronized public int size() {
        return tree.size();
    }

    private long skew;
    private SortedMap<Long, short[]> tree;
}
