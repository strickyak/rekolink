package net.yak.rekolink;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class RekoPacket {

    public static int myCallSign;  // To identify packets from myself.

    long milliseconds;  // 6 bytes used.
    long callsign;      // 6-bit-ASCII encoded (chars 32 to 95 become hextets 0 to 63) lowbits first.
    long check;

    byte[] payload;
    int offset;  // Offset of used part of payload
    int len;     // len of used part of payload
    int flags;

    // Confusing: Destination for forging, or Source if constructed.
    InetAddress address;
    int port;

    static final int OVERHEAD = 24;
    static final byte MAGIC0 = (byte) 22;
    static final byte MAGIC1 = (byte) 202;

    public RekoPacket() {
    }

    public void Clear() {
        milliseconds = 0;
        callsign = 0;
        check = 0;
        payload = null;
        offset = 0;
        len = 0;
        flags = 0;
    }

    public RekoPacket(DatagramPacket datagram) {
        Clear();

        byte[] b = datagram.getData();
        int o = datagram.getOffset();
        int n = datagram.getLength();
        if (n < OVERHEAD) return;

        if (b[o + 0] != MAGIC0 || b[o + 1] != MAGIC1) throw new IllegalArgumentException();

        address = datagram.getAddress();
        port = datagram.getPort();

        milliseconds = ((long) (b[o + 2] & 255) << 0) |
                ((long) (b[o + 3] & 255) << 8) |
                ((long) (b[o + 4] & 255) << 16) |
                ((long) (b[o + 5] & 255) << 24) |
                ((long) (b[o + 6] & 255) << 32) |
                ((long) (b[o + 7] & 255) << 40);

        callsign = ((long) (b[o + 8] & 255) << 0) |
                ((long) (b[o + 9] & 255) << 8) |
                ((long) (b[o + 10] & 255) << 16) |
                ((long) (b[o + 11] & 255) << 24) |
                ((long) (b[o + 12] & 255) << 32) |
                ((long) (b[o + 13] & 255) << 40) |
                ((long) (b[o + 14] & 255) << 48) |
                ((long) (b[o + 15] & 255) << 56);

        len = ((b[o + 16] & 255) << 0) |
                ((b[o + 17] & 255) << 8);
        flags = ((b[o + 18] & 255) << 0) |
                ((b[o + 19] & 255) << 8);

        check = ((b[o + 20] & 255) << 0) |
                ((b[o + 21] & 255) << 8) |
                ((b[o + 22] & 255) << 16) |
                ((b[o + 23] & 255) << 24);

        payload = new byte[len];
        offset = 0;
        for (int i = 0; i < len; i++) {
            payload[i] = b[o + OVERHEAD + i];
        }
    }

    public DatagramPacket forgeDatagram() {
        byte[] b = new byte[OVERHEAD + len];
        b[0] = MAGIC0;
        b[1] = MAGIC1;

        b[2] = (byte) (milliseconds >> 0);
        b[3] = (byte) (milliseconds >> 8);
        b[4] = (byte) (milliseconds >> 16);
        b[5] = (byte) (milliseconds >> 24);
        b[6] = (byte) (milliseconds >> 32);
        b[7] = (byte) (milliseconds >> 40);

        b[8] = (byte) (callsign >> 0);
        b[9] = (byte) (callsign >> 8);
        b[10] = (byte) (callsign >> 16);
        b[11] = (byte) (callsign >> 24);
        b[12] = (byte) (callsign >> 32);
        b[13] = (byte) (callsign >> 40);
        b[14] = (byte) (callsign >> 48);
        b[15] = (byte) (callsign >> 56);

        b[16] = (byte) (len >> 0);
        b[17] = (byte) (len >> 8);
        b[18] = (byte) (flags >> 0);
        b[19] = (byte) (flags >> 8);

        b[20] = (byte) (check >> 0);
        b[21] = (byte) (check >> 8);
        b[22] = (byte) (check >> 16);
        b[23] = (byte) (check >> 24);

        for (int i = 0; i < len; i++) {
            b[OVERHEAD + i] = payload[offset + i];
        }

        return new DatagramPacket(b, OVERHEAD + len, address, port);
    }

    public static String decodeCallsign(long x) {
        String s = "";
        for (int i = 0; i < 10; i++) {
            char ch = (char) (((x >> i * 6) & 63) + 32);
            if (ch == 32) break;
            s = s + ch;
        }
        return s;
    }

    public static long encodeCallsign(String s) {
        long x = 0;
        for (int i = 0; i < s.length(); i++) {
            x = x | ((long) ((Character.toUpperCase(s.charAt(i)) - 32) & 63) << (i * 6));
            if (i == 9) break;
        }
        return x;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
