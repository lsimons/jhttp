package net.jhttp;

/**
 * A helper supported by certain {@link RequestExecutor} and {@link HttpClient}
 * implementations to facilitate producing debug output tracing http requests
 * and responses. Use {@link HttpClientBuilder#enableTracing(HttpTracer)} to
 * pass in an implementation.
 */
public interface HttpTracer {
    /**
     * Log a request line.
     * 
     * @param line the line to log
     */
    void requestLine(String line);

    /**
     * Log a request header.
     * 
     * @param headerName the name of the header to log
     * @param headerValue the value of the header to log
     */
    void requestHeader(String headerName, String headerValue);

    /**
     * Log that a request completed.
     */
    void requestComplete();

    /**
     * Log a response line
     * 
     * @param line the line to log
     */
    void statusLine(String line);

    /**
     * Log a response header.
     * 
     * @param headerName the name of the header to log
     * @param headerValue the value of the header to log
     */
    void responseHeader(String headerName, String headerValue);

    /**
     * Log that a response completed.
     */
    void responseComplete();
}
