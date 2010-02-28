package net.jhttp;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;

class HttpRequestImpl extends HttpMessageImpl implements HttpRequest {
    private String protocol;
    private String method;
    private String requestURI;
    private String host;
    private int port;

    HttpRequestImpl(String method, String url)
            throws MalformedURLException {
        this(method, new URL(url));
    }
    
    HttpRequestImpl(String method, URL url) {
        this(getProtocol(url), method, getRequestURI(url), url.getHost(),
                url.getPort());
    }

    HttpRequestImpl(String protocol, String method, String requestURI,
            String host, int port) {
        this.protocol = protocol;
        this.method = method;
        this.requestURI = requestURI;
        this.host = host;
        this.port = port;

        ensureHostHeader();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setHeaders(Map<String, String> headers) {
        super.setHeaders(headers);
        ensureHostHeader();
    }

    void ensureHostHeader() {
        boolean hostSet = false;
        Map<String, String> headers = getHeaders();
        for (String header : headers.keySet()) {
            if (header == null) { continue; }
            if (!header.equalsIgnoreCase("Host")) { continue; }

            String value = headers.get(header);
            if (value != null) {
                hostSet = true;
                break;
            }
        }
        if (!hostSet) {
            if (port == -1) {
                headers.put("Host", host);
            } else {
                headers.put("Host", host + ":" + port);
            }
        }
    }

    static String getProtocol(URL url) {
        return url.getProtocol();
    }

    static String getRequestURI(URL url) {
        String path = url.getPath();
        String query = url.getQuery();
        String ref = url.getRef();

        String requestURI = "";
        if (path != null) {
            requestURI += path;
        }
        if (query != null) {
            requestURI += "?" + query;
        }
        if (ref != null) {
            requestURI += "#" + ref;
        }
        return requestURI;
    }

    static URL getURL(HttpRequest request)
            throws MalformedURLException {
        StringBuilder urlb = new StringBuilder();
        urlb.append(request.getProtocol());
        urlb.append("://");
        urlb.append(request.getHost());
        if (request.getPort() != -1) {
            urlb.append(":").append(request.getPort());
        }
        urlb.append(request.getRequestURI());

        String urls = urlb.toString();
        return new URL(urls);
    }
}
