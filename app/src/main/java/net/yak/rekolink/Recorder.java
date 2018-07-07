//package net.yak.rekolink;
//
///**
// * Created by strick on Friday the Thirteenth 2017-10-13 at Sewanee Tennessee.
// */
//
//import android.content.Context;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder.AudioSource;
//import android.util.Log;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.net.SocketException;
//
//import static net.yak.rekolink.RekoPacket.bytesToHex;
//
//public class Recorder implements Runnable {
//    private Thread machineThread;
//    private RecorderStepMachine machine;
//
//    class RecorderStepMachine extends StepMachine {
//
//        public RecorderStepMachine(Context context, Step[] steps) {
//            super(context, steps, "[Recorder]");
//        }
//
//        void waitOrWhatever() {
//            if (machine.getLevel() == MainActivity.TALK_LEVEL) {
//                Recorder.this.whateverPTT();
//            } else {
//                super.waitOrWhatever();
//            }
//        }
//    }
//
//    protected Config config;
//    protected Context context;
//
//    // boolean ptt;
//
//    protected final int SO_TIMEOUT_MILLISEC = 0;
//
//    protected AudioRecord mic;
//
//    protected byte[] mulawFrame;
//
//    public Recorder(Config cf, Context cx) {
//        config = cf;
//        context = cx;
//        machine = new RecorderStepMachine(context, steps);
//
//    }
//
//    public void run() {
//        machineThread = new Thread(machine);
//        machineThread.start();
//    }
//
//    private Step[] steps = new Step[]{
//            new Step(MainActivity.PERMISSION_LEVEL, "Permission",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return doPermission();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return undoPermission();
//                        }
//                    }),
//            new Step(MainActivity.DNS_LEVEL, "DnsLookup",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return nop();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return nop();
//                        }
//                    }),
//            new Step(MainActivity.BACKGROUND_LEVEL, "Background",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return nop();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return nop();
//                        }
//                    }),
//            new Step(MainActivity.FOREGROUND_LEVEL, "Foreground",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return startForeground();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return stopForeground();
//                        }
//                    }),
//            new Step(MainActivity.TALK_LEVEL, "Talk",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return startTalk();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return stopTalk();
//                        }
//                    }),
//    };
//
//    String nop() {
//        return "";
//    }
//
//    private byte[] pcm8Frame;
//    private short[] pcm16Frame;
//
//    private String doPermission() {
//        // Set audio specific thread priority
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
//        Log.e("PTT", "Recorder Running");
//        return "";
//    }
//
//    private String undoPermission() {
//        return "";
//    }
//
//    private String startForeground() {
//        Log.e("PTT", "Recorder init...");
//        mic = conjureMic();
//        mulawFrame = new byte[AudioParams.FRAME_SIZE];
//        pcm8Frame = new byte[AudioParams.FRAME_SIZE * (aStereo ? 2 : 1)];
//        pcm16Frame = new short[AudioParams.FRAME_SIZE * (aStereo ? 2 : 1)];
//        config.feedback.Ready();
//        return "";
//    }
//
//    private String stopForeground() {
//        Log.e("PTT", "Recorder Release...");
//        mic.release();
//        mic = null;
//        Log.e("PTT", "Recorder null.");
//        config.feedback.Disabled();
//        return "";
//    }
//
//    private String startTalk() {
//        mic.startRecording();
//        config.feedback.Ready();
//        return "";
//    }
//
//    private String stopTalk() {
//        mic.stop();
//        config.feedback.Ready();
//        return "";
//    }
//
//    private void whateverPTT() {
//        Log.e("PTT", "Recorder read pcmFrame...");
//        if (aPcmBits == 8) {
//            mic.read(pcm8Frame, 0, AudioParams.FRAME_SIZE);
//        } else {
//            mic.read(pcm16Frame, 0, AudioParams.FRAME_SIZE);
//        }
//        if (aStereo) {
//            // In stereo, pcm*Frame has a stride of 2, and we average Left & Right channels.
//            if (aPcmBits == 8) {
//                for (int i = 0; i < AudioParams.FRAME_SIZE; i++) {
//                    mulawFrame[i] = Mulaw.encode((pcm8Frame[i + i] + pcm8Frame[i + i + 1]) / 2);
//                }
//            } else {
//                for (int i = 0; i < AudioParams.FRAME_SIZE; i++) {
//                    mulawFrame[i] = Mulaw.encode((pcm16Frame[i + i] + pcm16Frame[i + i + 1]) / 2);
//                }
//            }
//        } else {
//            if (aPcmBits == 8) {
//                for (int i = 0; i < AudioParams.FRAME_SIZE; i++) {
//                    mulawFrame[i] = Mulaw.encode(pcm8Frame[i]);
//                }
//            } else {
//                for (int i = 0; i < AudioParams.FRAME_SIZE; i++) {
//                    mulawFrame[i] = Mulaw.encode(pcm16Frame[i]);
//                }
//            }
//        }
//
//        sendMulawFrame();
//    }
//
//    protected void sendMulawFrame() {
//        RekoPacket packet = new RekoPacket();
//        packet.address = config.serverAddress;
//        packet.port = config.serverPort;
//        packet.callsign = config.callsign;
//        packet.payload = mulawFrame;
//        packet.len = mulawFrame.length;
//
//        Log.e("PTT", "Recorder forging datagram...");
//        DatagramPacket dg = packet.forgeDatagram();
//        SocketAddress sa = new InetSocketAddress(packet.address, packet.port);
//        dg.setSocketAddress(sa);
//        Log.d("PTT", "Sending to " + sa + " : " + bytesToHex(dg.getData()));
//        try {
//            DatagramSocket so = config.socket;
//            if (so != null) {
//                so.send(dg);
//                Log.i("PTT", "Sent.");
//            } else
//                Log.i("PTT", "Cannot sendMulawFrame on null socket");
//            Config.Feedback fe = config.feedback;
//            if (fe != null)
//                fe.PacketSendSuccessful();
//            else
//                Log.i("PTT", "Cannot PacketSendSuccessful on null feedback");
//        } catch (SocketException e) {
//            Config.Feedback fe = config.feedback;
//            if (fe != null)
//                fe.PacketSendFailure();
//            else
//                Log.i("PTT", "Cannot PacketSendFailure on null feedback");
//        } catch (IOException e) {
//            Log.e("PTT", "Recorder sendMulawFrame: IOEx " + e);
//            e.printStackTrace();
//        }
//    }
//
//    int aRate;
//    int aPcmBits;
//    boolean aStereo;
//    int bytesPerSample;
//
//    // https://stackoverflow.com/a/5440517
//    private static int[] mSampleRates = new int[]{8000, 16000, 11025, 22050, 44100};
//
//    public AudioRecord conjureMic() {
//        for (int rate : mSampleRates) {
//            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT}) {
//                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
//                    try {
//                        Log.d("PTT", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
//                                + channelConfig);
//                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
//
//                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
//                            // check if we can instantiate and have a success
//                            AudioRecord mic = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
//
//                            if (mic.getState() == AudioRecord.STATE_INITIALIZED) {
//                                aRate = rate;
//                                aPcmBits = (audioFormat == AudioFormat.ENCODING_PCM_16BIT) ? 16 : 8;
//                                aStereo = (channelConfig == AudioFormat.CHANNEL_IN_STEREO);
//                                bytesPerSample = ((aPcmBits == 16) ? 2 : 1) * (aStereo ? 2 : 1);
//                                return mic;
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.e("PTT", rate + "Exception, keep trying.", e);
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    void setGoal(int level) {
//        Log.i("PTT", "SET GOAL " + level);
//        machine.setGoal(level);
//    }
//
//    int getLevel() {
//        return machine.getLevel();
//    }
//}
