package net.yak.rekolink;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Created by strick on 7/6/18.
 */

public class Heartbeat extends StoppableThread {

    private Config config;
    private Etc etc;

    public Heartbeat(Etc etc) {
        super("<Heartbeat>");
        this.etc = etc;
        this.config = etc.config;
    }

    @Override
    public void start() {
    }

    @Override
    public void step() {
        sendEmptyFrame();
    }

    @Override
    public void shutdown() {
    }

    private void sendEmptyFrame() {
        RekoPacket p = new RekoPacket();
        p.address = etc.serverAddress;
        p.port = config.serverPort;
        p.milliseconds = System.currentTimeMillis();
        p.callsign = config.callsign;
        p.payload = null;
        p.len = 0;

        DatagramPacket dg = p.forgeDatagram();
        SocketAddress sa = new InetSocketAddress(p.address, p.port);
        dg.setSocketAddress(sa);

        try {
            etc.socket.send(dg);
            etc.feedback.HeartbeatSuccessful();
        } catch (SocketException e) {
            etc.feedback.HeartbeatFailure();
        } catch (IOException e) {
            etc.feedback.HeartbeatFailure();
        }

        etc.sleep(15);
    }
}

