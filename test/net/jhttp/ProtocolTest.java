package net.jhttp;

import org.testng.annotations.Test;

@Test( groups = { "func" } )
public class ProtocolTest {
    class MyProtocol extends Protocol {}
    
    @Test
    public void testPackagePrivateConstructor() {
        new MyProtocol();
    }
}
