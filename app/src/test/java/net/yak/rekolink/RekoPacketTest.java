package net.yak.rekolink;

import org.junit.Test;

/**
 * Created by strick on 7/5/18.
 */
public class RekoPacketTest {
    @Test
    public void forgeDatagram() throws Exception {
    }

    @Test
    public void decodeCallsign() throws Exception {
        long x = RekoPacket.encodeCallsign("W6REK/7890");
        String y = RekoPacket.decodeCallsign(x);
        System.out.printf("Encoded %d Decoded <%s>", x, y);
        assert y == "W6REK/7890";
    }

    @Test
    public void encodeCallsign() throws Exception {
        long x = RekoPacket.encodeCallsign("W6REK");
        System.out.printf("Encoded %d", x);
        assert x == 731325879L;

        String y = RekoPacket.decodeCallsign(x);
        System.out.printf("Encoded %d Decoded <%s>", x, y);
        assert y == "W6REK";
    }

    @Test
    public void bytesToHex() throws Exception {
        byte[] bb = new byte[]{1, 100, -100, 50, -1, 0};
        String s = RekoPacket.bytesToHex(bb);
        System.out.printf("HEX <%s>", s);
        assert s.equals("01649C32FF00");
    }

}