package net.jhttp;

import java.io.IOException;

/**
 * A helper for {@link HttpClient} that handles all actual network
 * communication. There is a default implementation provided and in use by
 * default so situations where you would want to implement a custom request
 * executor are rare.
 * 
 * @see HttpClientBuilder#requestExecutor(RequestExecutor)
 */
public interface RequestExecutor {
    /**
     * Execute a http request using this executor.
     *
     * @param request the request to execute
     * @return the http response
     * @throws IOException if any problem occurs
     */
    HttpResponse execute(HttpRequest request) throws IOException;
}
