package net.jhttp;

import static net.jhttp.Util.ascii;
import static net.jhttp.Util.copy;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;

class ResponseAccumulator implements Parser.Listener {
    HttpResponseImpl res;
    int statusCode;
    String reasonPhrase;
    List<ByteBuffer> bodyParts;
    
    void init() {
        res = null;
        statusCode = -1;
        reasonPhrase = null;
        bodyParts = null;
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
    
    public void messageStart() {
        init();
    }

    public void startLineFirstField(ByteBuffer field) {
        // HTTP version. Ignore
    }

    public void startLineSecondField(ByteBuffer field) {
        // status code
        statusCode = Integer.parseInt(ascii(field));
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
        res = new HttpResponseImpl(statusCode, reasonPhrase);
        res.setBody(bodyParts);
    }

    public void bodyPart(ByteBuffer bodyPart) {
        if(this.bodyParts == null) {
            this.bodyParts = new LinkedList<ByteBuffer>();
        }
        this.bodyParts.add(copy(bodyPart));
    }
}
