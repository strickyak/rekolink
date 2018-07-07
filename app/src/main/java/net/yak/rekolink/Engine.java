package net.yak.rekolink;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by strick on 1/15/18.
 */

public class Engine extends LevelActor {
    public static final int PERMISSION_LEVEL = 10;
    public static final int DNS_LEVEL = 20;
    public static final int SPEAKER_LEVEL = 30;
    public static final int MICRPOPHONE_LEVEL = 40;
    public static final int TALK_LEVEL = 50;

    public Engine(Activity activity, Etc etc, Handler handler) {
        super("<Engine>");
        this.activity = activity;
        this.etc = etc;
        this.config = etc.config;
        this.handler = handler;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void stepUp() {
        switch (level) {
            case PERMISSION_LEVEL:
                upToPermission();
                break;
            case DNS_LEVEL:
                upToDNS();
                break;
            case SPEAKER_LEVEL:
                upToSpeaker();
                break;
            case MICRPOPHONE_LEVEL:
                break;
            case TALK_LEVEL:
                upToMicrophone();
                break;
            default:
                ;
        }
    }

    @Override
    public void stepDown() {
        switch (level) {
            case 0:
                downToStop();
                break;
            case PERMISSION_LEVEL:
                downToPermission();
                break;
            case DNS_LEVEL:
                downToDNS();
                break;
            case SPEAKER_LEVEL:
                downToSpeaker();
                break;
            case MICRPOPHONE_LEVEL:
                downToMicrophone();
                break;
            default:
                ;
        }
    }

    // private

    private Activity activity;
    private Etc etc;
    private Config config;
    private Handler handler;
    private Microphone microphone;
    private Speaker speaker;
    private Receiver receiver;
    private Heartbeat heartbeat;

    void upToSpeaker() {
        receiver = new Receiver(etc);
        receiver.start();
        speaker = new Speaker(etc, receiver.getJitterBuffer());
        speaker.start();
        heartbeat = new Heartbeat(etc);
        heartbeat.start();
    }

    void upToMicrophone() {
        microphone = new Microphone(etc);
        microphone.start();
    }

    void downToStop() {
    }

    void downToPermission() {
    }

    void downToDNS() {
        speaker.stopAndWait();
    }

    void downToSpeaker() {
    }

    void downToMicrophone() {
        microphone.stopAndWait();
    }

    private static final int REQUEST_RECORD_AUDIO = 888;
    private ArrayBlockingQueue<Boolean> requestRecordAudioResult =
            new ArrayBlockingQueue<Boolean>(5);

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO: {
                boolean granted = false;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestRecordAudioResult.add(true);
                } else {
                    requestRecordAudioResult.add(false);
                }
            }
            break;
            default:
                etc.fatal("Bad requestCode: " + requestCode);
                break;
        }
    }

    void upToPermission() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("PTT", "Asking Permission");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO);

            try {
                Log.e("PTT", "Taking Permission");
                boolean ok = requestRecordAudioResult.take();
                Log.e("PTT", "Took Permission: " + ok);
                if (!ok) {
                    etc.fatal("Microphone Record Permission is Required");
                }
            } catch (InterruptedException e) {
                Log.e("PTT", "upToPermission take interrupted: " + e);
            }
        }
    }

    ////////////////////////////////////   DNS

    String upToDNS() {
        Log.e("PTT", "doDnsLookup.");

        try {
            etc.socket = new DatagramSocket();
            etc.socket.setSoTimeout(5);
        } catch (SocketException e) {
            e.printStackTrace();
            return "Cannot create DatagramSocket: " + e;
        }

        if (config.serverName.equals("-")) {
            try {
                etc.serverAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new Error(e);
            }
        } else {
            try {
                Log.i("PTT", "serverName = " + config.serverName);
                etc.serverAddress = InetAddress.getByName(config.serverName);
            } catch (UnknownHostException e) {
                Log.e("PTT", "getByName error: " + e);
                e.printStackTrace();
                return "DNS Lookup fails: " + e;
            }
        }
        Log.i("PTT", "serverAddress = " + etc.serverAddress);
        etc.socketAddress = new InetSocketAddress(etc.serverAddress, config.serverPort);

        return "";
    }

}
