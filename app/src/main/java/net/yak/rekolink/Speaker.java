package net.yak.rekolink;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.net.DatagramPacket;

import static net.yak.rekolink.RekoPacket.OVERHEAD;

/**
 * Created by strick on 4/29/18.
 */

public class Speaker extends StoppableThread {

    private Config config;
    private Etc etc;
    private JitterBuffer jitterBuffer;

    public Speaker(Etc etc, JitterBuffer jb) {
        super("<Speaker>");
        this.etc = etc;
        this.config = etc.config;
        this.jitterBuffer = jb;
    }

    @Override
    public void start() {
        startPlayer();
        super.start();
    }

    @Override
    public void step() {
        stepPlayer();
    }

    @Override
    public void shutdown() {
        stopPlayer();
    }

    // private

    private AudioTrack track = null; // only track
    private DatagramPacket packet;
    private short[] pcmFrame = new short[AudioParams.FRAME_SIZE];
    private byte[] encodedFrame;

    private void startPlayer() {
        encodedFrame = new byte[AudioParams.FRAME_SIZE + OVERHEAD];
        packet = new DatagramPacket(encodedFrame, encodedFrame.length);

        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                AudioParams.SAMPLE_RATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioParams.ENCODING_PCM_NUM_BITS,
                AudioParams.TRACK_BUFFER_SIZE,
                AudioTrack.MODE_STREAM);

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        track.play();
    }

    private void stopPlayer() {
        if (track != null) {
            track.stop();
            track.release();
            track = null;
        }
        encodedFrame = null;
        packet = null;
    }

    private void stepPlayer() {
        short[] pcm = jitterBuffer.Take(0);
        if (pcm != null) {
//            Log.i("JB", "Write " + pcm.length + ":=" + pcm[0] + "," + pcm[1] + "," + pcm[2]);
            track.write(pcm, 0, AudioParams.FRAME_SIZE);
        }
    }
//    private void OLDstepPlayer() {
//        try {
////            Log.e("PTT", "Player receive..." + etc.socket.getLocalAddress() + "@" + etc.socket.getLocalPort());
//            try {
//                etc.socket.receive(packet);
//            } catch (java.net.SocketTimeoutException timeout) {
//                return; /// WHY WAS THIS continue;
//            }
//            RekoPacket rp = new RekoPacket(packet);
////            Log.e("PTT", "Player RekoPacket!");
//
//            if (false) if (rp.callsign == config.callsign) return;
//
////            Log.d("Player", "RekoPacket sender: " + packet.getAddress().getHostAddress() +
////                    " RekoPacket port: " + packet.getPort() + " RekoPacket length: " + packet.getLength());
//            // Decode audio
//            Mulaw.decode(OVERHEAD, AudioParams.FRAME_SIZE, encodedFrame, pcmFrame);
//            track.write(pcmFrame, 0, AudioParams.FRAME_SIZE);
//        } catch (SocketException e) {
//            // this may be expected
//            Log.e("PTT", "Player Socket Exception: " + e.getMessage());
//        } catch (IOException e) {
//            Log.e("PTT", "Player IOEx: " + e);
//            e.printStackTrace();
//        }
//    }
}
