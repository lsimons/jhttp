package net.jhttp;

import org.testng.annotations.Test;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

@Test(groups = { "func" })
public class UtilTest {
    class MyUtil extends Util {
    }

    @Test
    public void testPackagePrivateConstructor() {
        new MyUtil();
    }

    @Test
    public void closeNull() {
        Util.tryClose((Socket) null);
        Util.tryClose((InputStream) null);
        Util.tryClose((OutputStream) null);
    }
}
