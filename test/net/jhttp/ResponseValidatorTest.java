package net.jhttp;

import org.testng.annotations.Test;
import static org.easymock.EasyMock.*;

import java.nio.ByteBuffer;

@Test(groups = { "func" })
public class ResponseValidatorTest {
    @Test
    public void testValidator() {
        try {
            new ResponseValidator(null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        
        Parser.Listener delegate = createMock(Parser.Listener.class);
        delegate.messageStart();
        
        delegate.startLineFirstField(isA(ByteBuffer.class));
        delegate.startLineFirstField(isA(ByteBuffer.class));
        delegate.startLineSecondField(isA(ByteBuffer.class));
        delegate.startLineSecondField(isA(ByteBuffer.class));
        delegate.startLineThirdField(isA(ByteBuffer.class));
        delegate.startLineThirdField(isA(ByteBuffer.class));

        delegate.header(isA(ByteBuffer.class), isA(ByteBuffer.class));
        expectLastCall().times(3);

        delegate.bodyPart(isA(ByteBuffer.class));

        delegate.trailer(isA(ByteBuffer.class), isA(ByteBuffer.class));
        expectLastCall().times(3);

        delegate.messageComplete();

        replay(delegate);
        
        ResponseValidator rv = new ResponseValidator(delegate);
        
        rv.messageStart();

        rv.startLineFirstField(Util.asciiBuffer("HTTP/1.1"));
        try {
            rv.startLineFirstField(null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            rv.startLineFirstField(Util.asciiBuffer("HTTP/18.19"));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            rv.startLineFirstField(Util.asciiBuffer(""));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        rv.startLineFirstField(Util.asciiBuffer("HTTP/1.1"));

        
        rv.startLineSecondField(Util.asciiBuffer("200"));
        try {
            rv.startLineSecondField(null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            rv.startLineSecondField(Util.asciiBuffer("9999"));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            rv.startLineSecondField(Util.asciiBuffer(""));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        rv.startLineSecondField(Util.asciiBuffer("999"));


        rv.startLineThirdField(Util.asciiBuffer("OK"));
        try {
            rv.startLineSecondField(null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            StringBuffer x = new StringBuffer();
            for(int i = 0; i < 400; i++) {
                x.append("abcdef 1234567890");
            }
            rv.startLineThirdField(Util.asciiBuffer(x.toString()));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        rv.startLineThirdField(Util.asciiBuffer(""));


        rv.header(Util.asciiBuffer("XY"), Util.asciiBuffer("Z"));
        try {
            rv.header(null, Util.asciiBuffer(""));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            rv.header(Util.asciiBuffer("XY"), null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        rv.header(Util.asciiBuffer("XY"), Util.asciiBuffer("Z"));
        rv.header(Util.asciiBuffer("XY"), Util.asciiBuffer(""));
        
        
        rv.bodyPart(Util.asciiBuffer("cheese"));
        try {
            rv.bodyPart(null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }

        rv.messageComplete();


        rv.trailer(Util.asciiBuffer("XY"), Util.asciiBuffer("Z"));
        try {
            rv.trailer(null, Util.asciiBuffer(""));
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        try {
            rv.trailer(Util.asciiBuffer("XY"), null);
            assert false;
        } catch (AssertionError e) {
            // ignore
        }
        rv.trailer(Util.asciiBuffer("XY"), Util.asciiBuffer("Z"));
        rv.trailer(Util.asciiBuffer("XY"), Util.asciiBuffer(""));
        
        
        verify(delegate);
    }
}
