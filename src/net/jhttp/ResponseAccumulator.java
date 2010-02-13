package net.jhttp;

import static net.jhttp.Util.ascii;
import static net.jhttp.Util.copy;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

class ResponseAccumulator implements Parser.Listener {
    HttpResponseImpl res;
    int statusCode;
    String reasonPhrase;
    List<ByteBuffer> bodyParts;
    Map<String, String> headers;
    
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
        reasonPhrase = ascii(field).trim();
    }

    public void header(ByteBuffer name, ByteBuffer value) {
        if (this.headers == null) {
            this.headers = new HashMap<String, String>(2);
        }
        String valueString = (value == null)? null : ascii(value).trim(); 
        this.headers.put(ascii(name).trim(), valueString);
    }

    public void trailer(ByteBuffer name, ByteBuffer value) {
        // TODO implement
    }

    public void messageComplete() {
        res = new HttpResponseImpl(statusCode, reasonPhrase);
        res.setBody(bodyParts);
        res.setHeaders(headers);
    }

    public void bodyPart(ByteBuffer bodyPart) {
        if(bodyParts == null) {
            bodyParts = new LinkedList<ByteBuffer>();
        }
        bodyParts.add(copy(bodyPart));
    }
}
