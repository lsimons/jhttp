package net.jhttp;

import org.testng.annotations.Test;

@Test(groups={"checkin"})
public class BasicUsageTest {
    @Test
    public void testSingleLineGET() {
        HttpRequestBuilder hc = Http.client().GET("http://example.org/");
        assert hc != null;
    }
}
