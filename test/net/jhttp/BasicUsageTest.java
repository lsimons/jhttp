package net.jhttp;

import org.testng.annotations.Test;

@Test(groups={"checkin"})
public class BasicUsageTest {

    @Test
    public void testSimpleClientBuilder() {
        HttpClientBuilder hcb = Http.clientBuilder();
        assert hcb != null;
        HttpClient hc = hcb.build();
        assert hc != null;
    }

    @Test
    public void testSimpleClient() {
        HttpClient hc = Http.client();
        assert hc != null;
        HttpRequestBuilder hrb = hc.GET("http://example.org/");
        assert hrb != null;
    }

    @Test
    public void testSimpleRequest() {
        HttpRequestBuilder hrb = Http.request();
        assert hrb != null;
        HttpRequestBuilder hrb2 = hrb.GET("http://example.org/");
        assert hrb2 != null;
        assert hrb == hrb2;
    }
}
