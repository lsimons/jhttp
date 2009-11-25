package net.jhttp;

import static net.jhttp.Util.*;
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
        int port = getPort(request);
        
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
            return readResponse(is, request.getMethod());
        } finally {
            tryClose(s);
            tryClose(os);
            tryClose(is);
        }
    }

    private int getPort(HttpRequest request) {
        int port = request.getPort();
        if (port != -1) {
            return port;
        }
        if ("https".equalsIgnoreCase(request.getProtocol())) {
            return 443;
        }
        return 80;
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

    static HttpResponse readResponse(InputStream is, String method)
            throws IOException {
        ResponseAccumulator ra = new ResponseAccumulator();
        ResponseValidator rv = new ResponseValidator(ra);
        Parser p = new Parser(rv);
        if("HEAD".equals(method)) {
            p.expectHeadRequest(true);
        }
        
        byte[] buf = new byte[1500];
        int read;
        while ( (read = is.read(buf)) != -1) {
            ByteBuffer bb = ByteBuffer.wrap(buf, 0, read);
            p.parse(bb);
        }
        
        if(!ra.complete()) {
            p.forceFinish();
        }
        
        
        return ra.getResponse();
    }
}
