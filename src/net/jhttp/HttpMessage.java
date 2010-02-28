package net.jhttp;

import java.util.Map;

/**
 * Represents a http message, either a {@link HttpRequest} or a
 * {@link HttpResponse}.
 */
public interface HttpMessage {

    /**
     * Get the message headers. The returned map is modifiable. It is possible
     * to effectively override setting of a default/calculated value by putting
     * in a null entry, i.e. <code>req.getHeaders().put("Host",null)</code>
     * disables auto-setting of the <code>Host</code> header. 
     * 
     * @return the message headers. May be null.
     */
    Map<String, String> getHeaders();

    /**
     * Set the message headers. The provided map should normally be modifiable.
     * This wipes out any previously existing headers.
     * 
     * @param headers the message headers. May be null.
     */
    void setHeaders(Map<String, String> headers);

    /**
     * Get the http response body as a string. Since this loads the entire
     * response body in memory, this is not advisable for large response bodies.
     *
     * @return the http response body, or null if there is no body
     */
    String getBodyAsString();
}
