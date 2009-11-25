package net.jhttp;

import static net.jhttp.Util.ascii;

class Protocol {
    static final int SP = 32;
    static final int CR = 13;
    static final int LF = 10;

    static final int HT = 9;
    static final int COLON = 58;

    static final byte[] HTTP_VERSION = ascii("HTTP/1.0");
    
    Protocol() {}
    
    static boolean isLWS(byte b) {
        return (b == SP) || (b == HT) || (b == CR) || (b == LF);
    }
}
