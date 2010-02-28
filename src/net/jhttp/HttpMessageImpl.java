package net.jhttp;

import static net.jhttp.Util.string;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

class HttpMessageImpl implements HttpMessage {

    static final Pattern characterEncodingRE = Pattern.compile(
            "^.*?;\\s*charset\\s*=\\s*([^\\s;]+)\\s*(?:;.*)?$",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private Map<String, String> headers =
            new TreeMap<String, String>(CaseInsensitiveComparator.CASE_INSENSITIVE);
    private String bodyString;
    private List<ByteBuffer> bodyParts;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        if (headers != null) {
            for (String name : headers.keySet()) {
                this.headers.put(name, headers.get(name));
            }
        }
    }

    public void setBody(String responseBodyString) {
        assert bodyParts == null;
        this.bodyString = responseBodyString;
    }

    public void setBody(List<ByteBuffer> bodyParts) {
        assert bodyString == null;
        this.bodyParts = bodyParts;
    }

    public String getBodyAsString() {
        return (bodyString == null)? getBodyStringFromParts() : bodyString;
    }

    String getBodyStringFromParts() {
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
