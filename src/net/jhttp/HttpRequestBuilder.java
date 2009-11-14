package net.jhttp;

import java.io.IOException;

/**
 * Builder for http requests. Can be used by itself to produce http requests that are
 * not bound to a {@link HttpClient} to create unbound requests, but is more commonly
 * used when bound. A bound request builder can be used to actually execute http
 * requests and retrieve responses.
 */
public interface HttpRequestBuilder {
    /**
     * Finish building the request, execute it, and return the response body.
     *
     * @return the body of the response as a string, or null if there was no body.
     * @throws IOException if there was an error executing the request.
     */
    String responseBody() throws IOException;
}
