package net.yak.rekolink;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by strick on 10/15/17.
 */
public class MulawTest {
    @Test
    public void decodeMulaw16() throws Exception {
        for (int i = -128; i < 128; i++) {
            byte b = (byte) i;
            short s = Mulaw.decode(b);
            byte z = Mulaw.encode(s);

            int diff = (z - b);
            diff = (diff > 0) ? diff : -diff;
            System.out.format("b=%d z=%d diff=%d\n", b, z, diff);
            Assert.assertTrue(diff <= 1 || diff == 128);
        }
    }
}