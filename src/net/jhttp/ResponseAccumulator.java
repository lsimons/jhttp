package net.jhttp;

import static net.jhttp.Protocol.ascii;

import static java.lang.Math.min;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;

class ResponseAccumulator implements Parser.Listener {
    HttpResponse res;
    int statusCode;
    String reasonPhrase;
    boolean readBody;
    
    void init() {
        res = null;
        statusCode = -1;
        reasonPhrase = null;
        readBody = false;
    }

    boolean complete() {
        return res != null;
    }

    HttpResponse getResponse() {
        return res;
    }
    
    ///
    /// Parser.Listener
    ///
    
    public void messageStart() {}

    public void startLineFirstField(ByteBuffer field) {
        // HTTP version. Ignore
    }

    public void startLineSecondField(ByteBuffer field) {
        // status code
        Integer.parseInt(ascii(field));
    }

    public void startLineThirdField(ByteBuffer field) {
        // reason phrase
        reasonPhrase = ascii(field);
    }

    public void header(ByteBuffer name, ByteBuffer value) {
        // TODO implement
    }

    public void trailer(ByteBuffer name, ByteBuffer value) {
        // TODO implement
    }

    public void messageComplete() {
        if(res == null) {
            res = new HttpResponseImpl(statusCode, reasonPhrase);
        }
    }

    public void bodyPart(ByteBuffer bodyPart) {
        // TODO implement
    }
}
