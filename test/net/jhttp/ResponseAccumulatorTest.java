package net.jhttp;

import org.testng.annotations.Test;

@Test(groups = { "func" })
public class ResponseAccumulatorTest {
    @Test
    public void testResponseAccumulator() {
        ResponseAccumulator ra = new ResponseAccumulator();
        assert !ra.complete();
        ra.messageComplete();
        assert ra.complete();
        ra.messageStart();
        assert !ra.complete();
        
        ra.startLineFirstField(Util.asciiBuffer("HTTP/1.1"));
        ra.startLineSecondField(Util.asciiBuffer("200"));
        ra.startLineThirdField(Util.asciiBuffer("OK"));
        
        ra.header(Util.asciiBuffer("Foo"), Util.asciiBuffer("bar"));
        
        ra.bodyPart(Util.asciiBuffer("cheese"));
        
        ra.trailer(Util.asciiBuffer("X-Y"), Util.asciiBuffer("Z"));
        
        ra.messageComplete();
        
        HttpResponse res = ra.getResponse();
        assert res != null;
        
        assert res.getReasonPhrase().equals("OK");
        assert res.getStatusCode() == 200;
        assert res.getBodyAsString().equals("cheese");
    }
}
