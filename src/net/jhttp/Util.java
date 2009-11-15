package net.jhttp;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Util {
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
}
