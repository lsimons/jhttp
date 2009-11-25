package net.jhttp;

import org.testng.annotations.Test;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.net.UnknownHostException;

@Test(groups = { "int" })
public class DefaultRequestExecutorTest {
    @Test(expectedExceptions = { UnknownHostException.class })
    public void testUnknownHost() throws IOException {
        HttpRequest req = createMock(HttpRequest.class);
        expect(req.getHost()).andReturn("no.such.host.exists");
        replay(req);
        DefaultRequestExecutor e = new DefaultRequestExecutor();
        try {
            e.execute(req);
        } finally {
            verify(req);
        }
    }
    
    @Test
    public void testGetPort() {
        DefaultRequestExecutor e = new DefaultRequestExecutor();

        HttpRequest req = createMock(HttpRequest.class);
        expect(req.getPort()).andReturn(-1);
        expect(req.getProtocol()).andReturn("http");
        replay(req);
        int port = e.getPort(req);
        assert port == 80;
        verify(req);

        req = createMock(HttpRequest.class);
        expect(req.getPort()).andReturn(-1);
        expect(req.getProtocol()).andReturn("https");
        replay(req);
        port = e.getPort(req);
        assert port == 443;
        verify(req);

        req = createMock(HttpRequest.class);
        expect(req.getPort()).andReturn(1234);
        replay(req);
        port = e.getPort(req);
        assert port == 1234;
        verify(req);
    }
}
