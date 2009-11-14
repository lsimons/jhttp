package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

@Test(groups={"func"})
public class HttpClientImplTest {
    HttpClient hc;

    HttpClient newInstance() {
        return new HttpClientImpl();
    }
    
    @BeforeMethod
    public void setUp() {
        hc = newInstance();
    }
    
    @Test
    public void testGET() {
        HttpRequestBuilder hcb = hc.GET("http://example.org/");
        assert hcb != null;
    }
}
