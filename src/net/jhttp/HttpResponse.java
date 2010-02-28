package net.jhttp;

/**
 * Represents a http response.
 */
public interface HttpResponse extends HttpMessage {
    /**
     * Get the http response status code.
     * 
     * @return the status code
     */
    int getStatusCode();

    /**
     * Get the http response reason phrase.
     * 
     * @return the reason phrase
     */
    String getReasonPhrase();
}
