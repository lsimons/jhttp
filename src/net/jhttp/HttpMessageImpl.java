package net.jhttp;

import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

class HttpMessageImpl implements HttpMessage {

    static final Comparator<String> CASE_INSENSITIVE =
            new CaseInsensitiveComparator();

    private Map<String, String> headers =
            new TreeMap<String, String>(CASE_INSENSITIVE);

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
}
