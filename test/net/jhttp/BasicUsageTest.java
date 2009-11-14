package net.jhttp;

import org.testng.annotations.Test;

@Test(groups={"checkin"})
public class BasicUsageTest {
    @Test(groups={"broken"})
    public void testSimpleGetBuilder() {
        HttpClient c = Http.client();
        assert c != null;
        HttpRequestBuilder b = c.GET("http://example.org/");
        assert b != null;
    }
}
