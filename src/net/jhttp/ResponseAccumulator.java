package net.jhttp;

import static net.jhttp.Util.ascii;
import static net.jhttp.Util.copy;
import static net.jhttp.CaseInsensitiveComparator.CASE_INSENSITIVE;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

class ResponseAccumulator implements Parser.Listener {
    HttpTracer httpTracer;
    HttpResponseImpl res;
    String version;
    int statusCode;
    String reasonPhrase;
    List<ByteBuffer> bodyParts;
    Map<String, String> headers;

    ResponseAccumulator() {
    }

    ResponseAccumulator(HttpTracer httpTracer) {
        this.httpTracer = httpTracer;
    }

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
        version = ascii(field);
    }

    public void startLineSecondField(ByteBuffer field) {
        statusCode = Integer.parseInt(ascii(field));
    }

    public void startLineThirdField(ByteBuffer field) {
        reasonPhrase = ascii(field).trim();
        if (httpTracer != null) {
            httpTracer.statusLine(String.format(
                    "%s %d %s", version, statusCode, reasonPhrase));
        }
    }

    public void header(ByteBuffer name, ByteBuffer value) {
        if (this.headers == null) {
            this.headers = new TreeMap<String, String>(CASE_INSENSITIVE);
        }
        String nameString = ascii(name).trim(); 
        String valueString = (value == null)? null : ascii(value).trim(); 
        this.headers.put(nameString, valueString);
        if (httpTracer != null) {
            httpTracer.responseHeader(nameString, valueString);
        }
    }

    public void trailer(ByteBuffer name, ByteBuffer value) {
        // TODO implement
    }

    public void messageComplete() {
        res = new HttpResponseImpl(statusCode, reasonPhrase);
        res.setBody(bodyParts);
        res.setHeaders(headers);
        
        if (httpTracer != null) {
            httpTracer.responseComplete();
        }
    }

    public void bodyPart(ByteBuffer bodyPart) {
        if(bodyParts == null) {
            bodyParts = new LinkedList<ByteBuffer>();
        }
        bodyParts.add(copy(bodyPart));
    }
}
