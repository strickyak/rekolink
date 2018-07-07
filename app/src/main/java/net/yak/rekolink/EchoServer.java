//package net.yak.rekolink;
//
//import android.util.Log;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.net.SocketException;
//import java.net.SocketTimeoutException;
//import java.net.UnknownHostException;
//
///**
// * Created by strick on 10/28/17.
// */
//
//public class EchoServer implements Runnable {
//    private DatagramSocket socket;
//    final private Etc etc;
//
//    public EchoServer(Etc etc) {
//        this.etc = etc;
//    }
//
//    @Override
//    public void run() {
//
//        while (etc.socketAddress == null) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.i("PTT", "EchoServer serverAddress = " + etc.socketAddress);
//
//
//        try {
//            socket = new DatagramSocket(null);
//            socket.setSoTimeout(5000);
//            socket.bind(etc.socketAddress);
//        } catch (SocketException e) {
//            e.printStackTrace();
//            return;
//
//            //throw new Error(e);
//        }
//
//        while (true) {
//            final byte[] buf = new byte[512];
//            final DatagramPacket p = new DatagramPacket(buf, 512);
//            try {
//                socket.receive(p);
//                Log.i("PTT", "EchoServer received " + p);
//            } catch (SocketTimeoutException e) {
//                Log.i("PTT", "EchoServer Timeout");
//                continue;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            p.getData()[8] = (byte)'@';  // First byte of callsign.
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Log.i("PTT", "EchoServer thread Sleeping....");
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("PTT", "ECHO RekoPacket: " + p.getSocketAddress());
//                    try {
//                        socket.send(p);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
//    }
//}
