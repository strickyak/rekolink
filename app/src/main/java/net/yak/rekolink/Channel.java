package net.yak.rekolink;

import java.net.InetAddress;

public abstract class Channel {

    public static final int GOOD_STATUS = 0;
    public static final int BAD_STATUS = 1;
    public static final int PARTIAL_STATUS = 2;

    public static final int BROADCAST = 0;
    public static final int MULTICAST = 1;
    public static final int UNICAST = 2;

    public static final int VOICE_PORT = 2011;
    public static final int PROBE_PORT = 2012;

    public String name = null;

    public int port = VOICE_PORT;
    public InetAddress addr = null;

    public int status = BAD_STATUS;

    public Channel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract boolean isRecorderEnabled();

    public abstract boolean isPlayerEnabled();

    public void setStatus(int status) {
        this.status = status;
    }

    public class RekoChannel extends Channel {

        public RekoChannel(String name, InetAddress addr) {
            super(name);
            this.addr = addr;
        }

        @Override
        public boolean isRecorderEnabled() {
            return true;
        }

        @Override
        public boolean isPlayerEnabled() {
            return true;
        }
    }
}
