package net.jhttp;

import static net.jhttp.Util.ascii;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;

class HttpResponseImpl implements HttpResponse {
    private int statusCode;
    private String reasonPhrase;
    private String bodyString;
    private List<ByteBuffer> bodyParts;
    
    public HttpResponseImpl(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void setBody(String responseBodyString) {
        assert bodyParts == null;
        this.bodyString = responseBodyString;
    }
    
    public void addBodyPart(ByteBuffer bodyPart) {
        assert bodyString == null;
        if (bodyParts == null) {
            bodyParts = new LinkedList<ByteBuffer>();
        }
        bodyParts.add(bodyPart);
    }
    
    public void setBody(List<ByteBuffer> bodyParts) {
        assert bodyString == null;
        this.bodyParts = bodyParts;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getBodyAsString() {
        return (bodyString == null)? getBodyStringFromParts() : bodyString;
    }

    private String getBodyStringFromParts() {
        if (bodyParts == null) {
            return null;
        }
        if (bodyParts.size() == 0) {
            return "";
        }
        
        StringBuffer buf = new StringBuffer();
        for (ByteBuffer bb : bodyParts) {
            buf.append(ascii(bb)); // TODO sniff character encoding
        }
        return buf.toString();
    }
}
