package net.yak.rekolink;

public class Config {
    static final int DEFAULT_SERVER_PORT = 18512;
    static final String DEFAULT_SERVER_NAME = "forth.yak.net";

    public String callsignStr = "W6REK/X";
    public long callsign = RekoPacket.encodeCallsign(callsignStr);
    public String serverName = DEFAULT_SERVER_NAME;
    public int serverPort = DEFAULT_SERVER_PORT;
}
