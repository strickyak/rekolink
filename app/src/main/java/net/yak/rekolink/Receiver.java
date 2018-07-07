package net.yak.rekolink;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import static net.yak.rekolink.RekoPacket.OVERHEAD;

/**
 * Created by strick on 7/6/18.
 */

public class Receiver extends StoppableThread {

    private Config config;
    private Etc etc;

    public Receiver(Etc etc) {
        super("<Receiver>");
        this.etc = etc;
        this.config = etc.config;
    }

    @Override
    public void start() {
        super.start();
        startReceiver();
        jb = new JitterBuffer();
    }

    @Override
    public void step() {
        stepReceiver();
    }

    @Override
    public void shutdown() {
        stopReceiver();
    }

    // private
    private DatagramPacket packet;
    ;
    private byte[] encodedFrame;
    private JitterBuffer jb;

    JitterBuffer getJitterBuffer() {
        return jb;
    }

    private void startReceiver() {
        encodedFrame = new byte[AudioParams.FRAME_SIZE + OVERHEAD];
        packet = new DatagramPacket(encodedFrame, encodedFrame.length);
    }

    private void stopReceiver() {
        encodedFrame = null;
        packet = null;
    }

    private void stepReceiver() {
        try {
            while (true) {
                try {
                    etc.socket.receive(packet);
                    break;
                } catch (java.net.SocketTimeoutException timeout) {
                    return;
                }
            }
            RekoPacket rp = new RekoPacket(packet);
            if (rp.milliseconds < 1) {
                return;
            }
            long t = System.currentTimeMillis();
            jb.setSkew(rp.milliseconds = t);

            if (rp.len < 1) return;

            if (false) if (rp.callsign == config.callsign) return;

            short[] pcmFrame = new short[rp.len];
            Mulaw.decode(OVERHEAD, rp.len, encodedFrame, pcmFrame);

//            Log.i("JB", "Add b " + encodedFrame.length + ":=" + encodedFrame[24] + "," + encodedFrame[25] + "," + encodedFrame[26]);
//            Log.i("JB", "Add s " + pcmFrame.length + ":=" + pcmFrame[0] + "," + pcmFrame[1] + "," + pcmFrame[2]);
            jb.Add(rp.milliseconds, pcmFrame);

        } catch (SocketException e) {
            // this may be expected
            Log.e("PTT", "Player Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            Log.e("PTT", "Player IOEx: " + e);
            e.printStackTrace();
        }
    }
}

