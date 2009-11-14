package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

@Test(groups={"func"})
public class SimpleRequestExecutorTest {
    @Test(dataProvider = "sampleEncodings")
    public void testCharacterEncodingSniffer(String input, String expected) {
        assert expected.equals(
                SimpleRequestExecutor.sniffCharacterEncoding(input));
    }
    
    @DataProvider(name = "sampleEncodings")
    public String[][] getSampleEncodings() {
        return new String[][] {
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
