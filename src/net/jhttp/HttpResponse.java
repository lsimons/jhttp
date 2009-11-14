package net.jhttp;

/**
 * Represents a http response.
 */
public interface HttpResponse extends HttpMessage {
    /**
     * Get the http response body as a string. Since this loads the entire
     * response body in memory, this is not advisable for large response bodies.
     * 
     * @return the http response body, or null if there is no body
     */
    String getBodyAsString();

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
