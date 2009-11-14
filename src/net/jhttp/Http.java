package net.jhttp;

/**
 * Central entrypoint into this package. Use {@link #client()} for a client
 * with default options. Use {@link #clientBuilder()} to build a customized
 * {@link HttpClient} instance.
 */
public class Http {
    Http() {}

    /**
     * Get a new {@link HttpClientBuilder}. Useful if you want to customize
     * {@link HttpClient} default behavior.
     * 
     * @return a new {@link HttpClientBuilder} instance
     */
    public static HttpClientBuilder clientBuilder() {
        return new HttpClientBuilderImpl();
    }

    /**
     * Get a new {@link HttpClient} instance with default options.
     *
     * @return a new {@link HttpClient} instance
     */
    public static HttpClient client() {
        return clientBuilder().build();
    }
    
    /**
     * Get a new {@link HttpRequestBuilder} instance that is not yet bound
     * to a {@link HttpClient}.
     *
     * @return a new {@link HttpRequestBuilder}
     */
    public static HttpRequestBuilder requestBuilder() {
        return new HttpRequestBuilderImpl();
    }
}
