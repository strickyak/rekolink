package net.yak.rekolink;

import android.util.Log;

public class Mulaw {
    static void encode(int offset, int size, short[] pcmFrame, byte[] encodedFrame) {
        for (int i = 0; i < size; i++) {
            encodedFrame[offset + i] = encode(pcmFrame[i]);
        }
    }

    static void decode(int offset, int size, byte[] encodedFrame, short[] pcmFrame) {
        for (int i = 0; i < size; i++) {
            pcmFrame[i] = decode(encodedFrame[offset + i]);
        }
    }

    static final int[] boundary = {4063, 2015, 991, 479, 223, 95, 31, 1};

    // Decode one mulaw sample byte.
    static short decode(byte b) {
        if (b == 0xFF) {
            return 0;
        }
        if (b == 0x7F) {
            return -1;
        }
        int sign = b & 0x80;
        int exp = (b & 0x70) >> 4;
        int man = b & 0x0F;

        int sz = 0x100 >> exp;

        int x = sz * (15 - man) + boundary[exp];

        if (exp == 7) {
            x -= 2;
        }

        // Scale Mulaw range to PCM16 range.
        x = (int) (x * 32768.0 / 8158.0);
        if (sign == 0) { // if negative
            return (short) (~x);
        }
        return (short) x;
    }

    // Encode one mulaw sample byte.
    static byte encode(int x) {
        boolean neg = false;
        if (x < 0) {
            neg = true;
            x = ~x;
        }
        // Scale PCM16 int range to Mulaw Range.
        x = (int) (x * 8158.0 / 32768.0);
        // Clip.
        if (x > 8158) {
            x = 8158;
        }
        int sz = 256;
        int m = 0;
        int i = 0;
        for (; i < 8; i++) {
            int b = boundary[i];
            if (x >= b) {
                m = 15 - ((x - b) / sz);
                if (i == 7) {
                    m--;
                }
                break;
            }
            sz >>= 1;
        }

        if (m < 0) {
            wtf(m);
        }
        if (m > 15) {
            wtf(m);
        }
        byte z = (byte) ((i << 4) | m);
        if (x == 0) {
            z = 0x7F; // Special case.
        }
        if (!neg) {
            z |= 0x80; // Positive case has sign bit.
        }
        return z;
    }

    private static void wtf(int x) {
        Log.wtf("PTT", "WTF: " + x);
    }
}


