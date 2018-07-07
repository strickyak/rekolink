package net.yak.rekolink;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * Created by strick on 1/15/18.
 */

public class Etc {

    Context context;
    Config config;

    public Etc(Context context, Config config) {
        this.context = context;
        this.config = config;
    }

    public void toast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignore) {
            ;
        }
    }

    public void fatal(String s) {
        try {
            //toast("FATAL: " + s);
        } catch (Exception ex) {
            //Log.e("PTT", ex + " DURING fatal: " + s);
        }

        Log.e("PTT", "FATAL: " + s);
        throw new Error(s);
    }

    public InetAddress serverAddress;
    public SocketAddress socketAddress;
    public DatagramSocket socket;

    interface Feedback {
        void Busy();

        void Ready();

        void Disabled();

        void PacketSendSuccessful();

        void PacketSendFailure();

        void HeartbeatFailure();

        void HeartbeatSuccessful();
    }

    public Feedback feedback;
}
