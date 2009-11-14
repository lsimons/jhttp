package net.jhttp;

/**
 * Represents a http request.
 */
public interface HttpRequest extends HttpMessage {
    /**
     * Get the request URI.
     * 
     * @return the request URI
     */
    String getRequestURI();

    /**
     * Get the request method.
     * 
     * @return the request method
     */
    String getMethod();

    /**
     * Get the request host.
     * 
     * @return the request host
     */
    String getHost();

    /**
     * Get the request port.
     * 
     * @return the request port
     */
    int getPort();

    /**
     * Get the request protocol (i.e. "http" or "https").
     * 
     * @return the request protocol
     */
    String getProtocol();
}
