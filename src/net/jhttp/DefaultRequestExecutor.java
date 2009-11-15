package net.jhttp;

import static net.jhttp.Util.tryClose;
import static net.jhttp.Protocol.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

class DefaultRequestExecutor implements RequestExecutor {
    public HttpResponse execute(HttpRequest request) throws IOException {
        String host = request.getHost();
        int port = request.getPort();
        
        InetAddress serverAddress = InetAddress.getByName(host);
        if(serverAddress == null) {
            throw new IOException(String.format(
                    "Error resolving %s to IP address", host));
        }
        
        Socket s = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            if("https".equalsIgnoreCase(request.getProtocol())) {
                SocketFactory f = SSLSocketFactory.getDefault();
                s = f.createSocket(serverAddress, port);
            } else {
                s = new Socket(serverAddress, port);
            }
            // TODO timeout, linger, no delay, keep alive, etc
            os = new BufferedOutputStream(s.getOutputStream());
            writeRequest(os, request);
            is = new BufferedInputStream(s.getInputStream());
            return readResponse(is);
        } finally {
            tryClose(s);
            tryClose(os);
            tryClose(is);
        }
    }

    static void writeRequest(OutputStream os, HttpRequest req)
            throws IOException {
        startLine(os, req.getMethod(), req.getRequestURI());
        header(os, "Host", req.getHost());
        header(os, "Content-Length", "0");
        header(os, "Connection", "close");
        header(os, "User-Agent", "jhttp/0.1.0");
        crlf(os);
        os.flush();
        // todo body
    }

    static void startLine(OutputStream os, String method, String uri)
            throws IOException {
        os.write(ascii(method));
        os.write(SP);
        os.write(ascii(uri));
        os.write(SP);
        os.write(HTTP_VERSION);
        crlf(os);
    }
    
    static void header(OutputStream os, String name, String value)
            throws IOException {
        os.write(ascii(name));
        os.write(COLON);
        os.write(ascii(value));
        crlf(os);
    }

    static void crlf(OutputStream os) throws IOException {
        os.write(CR);
        os.write(LF);
    }

    static HttpResponse readResponse(InputStream is) throws IOException {
        ResponseAccumulator ra = new ResponseAccumulator();
        ra.init();
        Parser p = new Parser(ra);
        
        byte[] buf = new byte[1500];
        int read;
        while ( (read = is.read(buf)) != -1 && !ra.complete()) {
            ByteBuffer bb = ByteBuffer.wrap(buf, 0, read);
            p.parse(bb);
        }
        
        p.forceFinish();
        
        return ra.getResponse();
    }
}
