package net.jhttp;

import static net.jhttp.Util.string;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpResponseImpl extends HttpMessageImpl implements HttpResponse {
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
        
        String characterEncoding = sniffCharacterEncoding();
        
        StringBuffer buf = new StringBuffer();
        for (ByteBuffer bb : bodyParts) {
            buf.append(string(bb, characterEncoding));
        }
        return buf.toString();
    }

    private static Pattern characterEncodingRE = Pattern.compile(
            "^.*?;\\s*charset\\s*=\\s*([^\\s;]+)\\s*(?:;.*)?$",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    String sniffCharacterEncoding() {
        Map<String, String> headers = getHeaders();
        if (headers == null) {
            return "UTF-8";
        } else {
            String contentType = headers.get("Content-Type");
            return sniffCharacterEncoding(contentType);
        }
    }

    static String sniffCharacterEncoding(String contentType) {
        if (contentType != null) {
            Matcher m = characterEncodingRE.matcher(contentType);
            if (m.matches()) {
                String characterEncoding = m.group(1);
                try {
                    Charset.forName(characterEncoding);
                    return characterEncoding.trim();
                } catch (IllegalArgumentException e) {
                    // fall back
                }
            }
        }
        return "UTF-8";
    }
}
