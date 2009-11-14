package net.jhttp;

import org.testng.annotations.Test;
import static org.easymock.EasyMock.*;

@Test(groups={"checkin"})
public class BasicUsageTest {
    @Test
    public void testSimpleGET() {
        HttpRequestBuilder hc = Http.client().GET("http://example.org/");
        assert hc != null;
        HttpRequest hr = hc.build();
        assert "example.org".equals(hr.getHost());
        assert -1 == hr.getPort();
        assert "/".equals(hr.getRequestURI());
    }

    @Test
    public void testFakeGET() throws Exception {
        RequestExecutor re = createMock(RequestExecutor.class);
        HttpResponse hr = createMock(HttpResponse.class);
        expect(re.execute(isA(HttpRequest.class))).andReturn(hr);
        expect(hr.getBodyAsString()).andReturn("body");
        replay(re, hr);
        
        assert "body".equals(

                Http.clientBuilder()
                        .requestExecutor(re)
                        .build()
                        .GET("http://example.org/").responseBody()

                );
        
        verify(re, hr);
    }
}
