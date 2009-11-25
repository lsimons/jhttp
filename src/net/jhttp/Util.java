package net.jhttp;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

class Util {
    
    Util() {}
    
    static void tryClose(Socket s) {
        if (s == null) { return; }
        try { s.close(); }
        catch (IOException e) { /* ignore */ }
    }

    static void tryClose(InputStream is) {
        if (is == null) { return; }
        try { is.close(); }
        catch (IOException e) { /* ignore */ }
    }

    static void tryClose(OutputStream os) {
        if (os == null) { return; }
        try { os.close(); }
        catch (IOException e) { /* ignore */ }
    }

    static ByteBuffer copy(ByteBuffer view) {
        byte[] buf = new byte[view.remaining()];
        view.get(buf);
        return ByteBuffer.wrap(buf);
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

    static String asciiCopy(ByteBuffer bb) {
        final int pos = bb.position();
        try {
            final int size = bb.remaining();
            final byte[] b = new byte[size];
            bb.get(b);
            return ascii(b);
        } finally {
            bb.position(pos);
        }
    }

    static ByteBuffer asciiBuffer(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
