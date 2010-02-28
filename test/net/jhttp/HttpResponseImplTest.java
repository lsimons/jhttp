package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Test(groups={"func"})
public class HttpResponseImplTest {
    @Test
    public void testHttpResponseImpl() throws Exception {
        int statusCode = 200;
        String reasonPhrase = "OK";
        String body = "data";
        
        HttpResponseImpl res = new HttpResponseImpl(statusCode, reasonPhrase);
        res.setBody(body);
        
        assert statusCode == res.getStatusCode();
        assert reasonPhrase.equals(res.getReasonPhrase());
        assert body.equals(res.getBodyAsString());
        
        try {
            res.setBody(new ArrayList<ByteBuffer>());
            assert false;
        } catch(AssertionError e) {
            // pass
        }

        res = new HttpResponseImpl(statusCode, reasonPhrase);
        ByteBuffer bb = ByteBuffer.wrap(body.getBytes("US-ASCII"));
        List<ByteBuffer> bbs = new ArrayList<ByteBuffer>();
        bbs.add(bb);
        res.setBody(bbs);
        assert body.equals(res.getBodyAsString());

        try {
            res.setBody("");
            assert false;
        } catch (AssertionError e) {
            // pass
        }

        res.setBody(new ArrayList<ByteBuffer>());
        assert res.getBodyAsString().equals("");

        res = new HttpResponseImpl(statusCode, reasonPhrase);
        assert res.getBodyAsString() == null;
    }

    @Test(dataProvider = "headerSpellings")
    public void testCaseSensitivity(String name, String value,
            String expected) {
        HttpResponseImpl res = new HttpResponseImpl(200, "OK");
        res.getHeaders().put(name, value);
        assert res.sniffCharacterEncoding().equals(expected);
    }

    @DataProvider(name = "headerSpellings")
    public String[][] getHeaderSpellings() {
        return new String[][]{
                { "content-type", ";charset=ISO-8859-1", "ISO-8859-1" },
                { "Content-Type", ";charset=ISO-8859-1", "ISO-8859-1" },
                { "CONTENT-TYPE", ";charset=ISO-8859-1", "ISO-8859-1" },
        };
    }

    @Test(dataProvider = "sampleEncodings")
    public void testCharacterEncodingSniffer(String input, String expected) {
        assert expected.equals(
                HttpResponseImpl.sniffCharacterEncoding(input));
    }

    @DataProvider(name = "sampleEncodings")
    public String[][] getSampleEncodings() {
        return new String[][]{
                { null, "UTF-8" },
                { "", "UTF-8" },
                { ";charset=ISO-8859-1", "ISO-8859-1" },
                { "\n\n\n  foo\n\n;\n  charset=  ISO-8859-1  ", "ISO-8859-1" },
                { "text/html; charset=ISO-8859-1", "ISO-8859-1" },
                { "text/html; charset=ISO- 8859-1", "UTF-8" },
                { "text/html; charset=ISO-\n8859-1", "UTF-8" },
                { "text/html; charset=SOMETHING_UNKNOWN", "UTF-8" },
        };
    }

}
