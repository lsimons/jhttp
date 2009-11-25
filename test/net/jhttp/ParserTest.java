package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import static net.jhttp.Util.ascii;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Test( groups = { "func" } )
public class ParserTest {
    static String[][] validTestFiles = new String[][] {
            { "ValidHttpVersions.txt" },
            { "ValidResponseCodes.txt" },
    };
    @DataProvider(name =  "valid")
    private String[][] getValidTests() {
        return validTestFiles;
    }

    static String[][] invalidTestFiles = new String[][] {
            { "InvalidHttpVersions.txt" },
            { "InvalidResponseCodes.txt" },
            { "InvalidNewlines.txt" },
            { "InvalidSpaces.txt" },
            { "InvalidControlChars.txt" },
            { "InvalidFieldSizes.txt" },
    };
    @DataProvider(name = "invalid")
    private String[][] getInvalidTests() {
        return invalidTestFiles;
    }

    static String chars1000 = "";
    static String chars4000 = "";
    static String chars10000 = "";
    static {
        for(int i = 0; i < 100; i++) {
                        //1234567890
            chars1000 += "abcdefghij";
        }
        for (int i = 0; i < 4; i++) {
            chars4000 += chars1000;
        }
        for (int i = 0; i < 10; i++) {
            chars10000 += chars1000;
        }
    }

    private String[] loadTests(String testFile) throws Exception {
        String path = "parsertests/" + testFile; 
        ClassLoader cl = getClass().getClassLoader();
        InputStream is = ParserTest.class.getResourceAsStream(path);
        if(is == null) {
            throw new NullPointerException(path);
        }
        InputStreamReader isr = new InputStreamReader(is, "US-ASCII");
        
        StringBuffer sb = new StringBuffer();
        char[] buf = new char[1024];
        int read;
        while( (read = isr.read(buf)) != -1 ) {
            sb.append(buf, 0, read);
        }
        String s = sb.toString();
        s = s.replaceAll("(?<!\r)\n", "\r\n");
        s = s.replaceAll("\r(?!\n)", "\r\n");
        s = s.replaceAll("\\\\u0000", "\u0000");
        s = s.replaceAll("<<1000\\*>>", chars1000);
        s = s.replaceAll("<<4000\\*>>", chars4000);
        s = s.replaceAll("<<10000\\*>>", chars10000);

        return s.split("----\r\n");
    }
    
    @Test(dataProvider = "valid")
    public void testParse(String testFile) throws Exception {
        String[] tests = loadTests(testFile);
        
        for(String test : tests) {
            Listener l = new Listener();
            Parser p = new Parser(new ResponseValidator(l));
            p.parse(Util.asciiBuffer(test));
            p.forceFinish();
            p.forceFinish();
            String result = l.getRequest();
            System.out.println("test = '" + test + "'");
            System.out.println("result = '" + result + "'");
            System.out.println();
            assert test.equals(result);
        }
    }

    @SuppressWarnings({ "ConstantConditions" })
    @Test(dataProvider = "invalid", groups = {"broken"})
    public void testParseError(String testFile) throws Exception {
        String[] tests = loadTests(testFile);

        for (String test : tests) {
            Listener l = new Listener();
            Parser p = new Parser(new ResponseValidator(l));
            try {
                p.parse(Util.asciiBuffer(test));
                p.forceFinish();
                p.forceFinish();
                throw new ProblemIgnored();
            } catch (ProblemIgnored e) {
                String result = l.getRequest();
                //System.out.println("test = '" + test + "'");
                //System.out.println("result = '" + result + "'");
                //System.out.println();
                assert false;
            } catch(IOException e) {
                //String result = l.getRequest();
                //System.out.println("test = '" + test + "'");
                //System.out.println("result = '" + result + "'");
                //System.out.println();
            } catch (AssertionError e) {
                //String result = l.getRequest();
                //System.out.println("test = '" + test + "'");
                //System.out.println("result = '" + result + "'");
                //System.out.println();
            }
        }
    }

    @Test(groups = { "broken" })
    public void testExpectHead() throws Exception {
        String test = "HTTP/1.1 200 OK\r\nContent-";

        Listener l = new Listener();
        Parser p = new Parser(new ResponseValidator(l));
        p.expectHeadRequest(true);
        p.expectHeadRequest(false);
        p.parse(Util.asciiBuffer(test));
        
        try {
            p.expectHeadRequest(true);
            assert false;
        } catch(IllegalStateException e) {
            // ignore
        }
    }

    @Test(groups = { "broken" })
    public void testKeepAliveSupport() throws Exception {
        String test1      = "HTTP/1.1 200 OK\r\nContent-Length: 0\r\n\r\n";
        String test2Part1 = "HTTP/1.1 404 Not Found\r\nCo";
        String test2Part2 = "ntent-Length: 0\r\n\r\n";

        Listener l = new Listener();
        Parser p = new Parser(new ResponseValidator(l));
        p.parse(Util.asciiBuffer(test1 + test2Part1));
        
        assert l.completed == 1;

        p.parse(Util.asciiBuffer(test2Part2));
        String test2Result = l.getRequest();
        //System.out.println("test2Result = " + test2Result);
        //System.out.println("test2Part1 = " + test2Part1);
        //System.out.println("test2Part2 = " + test2Part2);
        assert l.completed == 2;
        assert test2Result.equals(test2Part1 + test2Part2);

        try {
            p.expectHeadRequest(true);
            assert false;
        } catch (IllegalStateException e) {
            // ignore
        }
    }

    static class ProblemIgnored extends Exception {
        
    }
    
    static class Listener implements Parser.Listener {
        private StringBuffer request;
        private boolean bodyStarted = false;
        private int completed = 0;

        public void messageStart() {
            request = new StringBuffer();
        }

        public void startLineFirstField(ByteBuffer field) {
            request.append(ascii(field));
            request.append(" ");
        }

        public void startLineSecondField(ByteBuffer field) {
            request.append(ascii(field));
            request.append(" ");
        }

        public void startLineThirdField(ByteBuffer field) {
            request.append(ascii(field));
            request.append("\r\n");
        }

        public void header(ByteBuffer name, ByteBuffer value) {
            request.append(ascii(name));
            request.append(": ");
            request.append(ascii(value));
            request.append("\r\n");
        }

        public void trailer(ByteBuffer name, ByteBuffer value) {
            // TODO
        }

        public void messageComplete() {
            completed++;
        }

        public void bodyPart(ByteBuffer bodyPart) {
            if(!bodyStarted) {
                request.append("\r\n");
                bodyStarted = true;
            }
            request.append(ascii(bodyPart));
        }

        public String getRequest() {
            return request.toString();
        }

        public int getCompleted() {
            return completed;
        }
    }
}
