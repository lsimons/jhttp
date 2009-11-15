package net.jhttp;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

class Protocol {
    static final int SP = 32;
    static final int CR = 13;
    static final int LF = 10;

    static final int HT = 9;
    static final int COLON = 58;

    static final byte[] HTTP_VERSION = ascii("HTTP/1.0");
    
    static boolean isLWS(byte b) {
        return (b == SP) || (b == HT) || (b == CR) || (b == LF);
    }

    static byte[] ascii(String s) {
        try {
            return s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    static String ascii(byte[] b) {
        try {
            return new String(b, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    static String ascii(ByteBuffer bb) {
        final int size = bb.remaining();
        final byte[] b = new byte[size];
        bb.get(b);
        return ascii(b);
    }
}
