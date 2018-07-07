package net.yak.rekolink;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import static net.yak.rekolink.AudioParams.FRAME_SIZE;

/**
 * Created by strick on 1/14/18.
 */

public final class Microphone extends StoppableThread {
    private Config config;
    private Etc etc;

    public Microphone(Etc etc) {
        super("<Microphone>");
        this.etc = etc;
        this.config = etc.config;
        createMic();
    }

    @Override
    public void start() {
        startMic();
        super.start();
    }

    @Override
    public void step() {
        stepMic();
    }

    @Override
    public void shutdown() {
        stopMic();
    }

    // private

    private byte[] pcm8Frame;
    private short[] pcm16Frame;
    private AudioRecord mic;
    private byte[] mulawFrame;

    private void createMic() {
        Log.e("PTT", "Recorder init...");
        mic = conjureMic();
        mulawFrame = new byte[FRAME_SIZE];
        pcm8Frame = new byte[FRAME_SIZE * (aStereo ? 2 : 1)];
        pcm16Frame = new short[FRAME_SIZE * (aStereo ? 2 : 1)];
    }

    private void startMic() {
        mic.startRecording();
        etc.feedback.Ready();

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    }

    private void stopMic() {
        mic.stop();
        mic.release();
        mic = null;
        mulawFrame = null;
        pcm8Frame = null;
        pcm16Frame = null;
        etc.feedback.Disabled();
    }

    // =========================== STEP ==========================

    private void stepMic() {
        if (aRate != AudioParams.SAMPLE_RATE) {
            etc.fatal("Cannot handle Microphone audio Rate = " + aRate);
        }

        int sz;
        if (aPcmBits == 8) {
            sz = mic.read(pcm8Frame, 0, FRAME_SIZE, AudioRecord.READ_BLOCKING);
        } else {
            sz = mic.read(pcm16Frame, 0, FRAME_SIZE, AudioRecord.READ_BLOCKING);
        }
        if (sz < 1) {
            return;
        }

        if (aStereo) {
            // In stereo, pcm*Frame has a stride of 2, and we average Left & Right channels.
            if (aPcmBits == 8) {
                for (int i = 0; i < sz; i++) {
                    mulawFrame[i] = Mulaw.encode((pcm8Frame[i + i] + pcm8Frame[i + i + 1]) / 2);
                }
            } else {
                for (int i = 0; i < sz; i++) {
                    mulawFrame[i] = Mulaw.encode((pcm16Frame[i + i] + pcm16Frame[i + i + 1]) / 2);
                }
            }
        } else {
            if (aPcmBits == 8) {
                for (int i = 0; i < sz; i++) {
                    mulawFrame[i] = Mulaw.encode(pcm8Frame[i]);
                }
            } else {
                for (int i = 0; i < sz; i++) {
                    mulawFrame[i] = Mulaw.encode(pcm16Frame[i]);
                }
            }
        }

        sendMulawFrame(sz);
    }

    private void sendMulawFrame(int sz) {
        RekoPacket p = new RekoPacket();
        p.address = etc.serverAddress;
        p.port = config.serverPort;
        p.milliseconds = System.currentTimeMillis();
        p.callsign = config.callsign;
        p.payload = mulawFrame;
        p.len = sz;

        DatagramPacket dg = p.forgeDatagram();
        SocketAddress sa = new InetSocketAddress(p.address, p.port);
        dg.setSocketAddress(sa);

        try {
            etc.socket.send(dg);
            etc.feedback.PacketSendSuccessful();
        } catch (SocketException e) {
            etc.feedback.PacketSendFailure();
        } catch (IOException e) {
            etc.feedback.PacketSendFailure();
        }
    }

    // ======================= CONJURE ========================

    private int aRate;
    private int aPcmBits;
    private boolean aStereo;

    // https://stackoverflow.com/a/5440517
//    private static int[] mSampleRates = new int[]{8000, 16000, 11025, 22050, 44100};
    private static int[] mSampleRates = new int[]{AudioParams.SAMPLE_RATE};

    private AudioRecord conjureMic() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d("PTT", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                aRate = rate;
                                aPcmBits = (audioFormat == AudioFormat.ENCODING_PCM_16BIT) ? 16 : 8;
                                aStereo = (channelConfig == AudioFormat.CHANNEL_IN_STEREO);
                                return recorder;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("PTT", rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        Log.e("PTT", "CANNOT Conjure Mic -- Out of Rates");
        etc.fatal("CANNOT Conjure Mic -- Out of Rates");
        return null;
    }
}
