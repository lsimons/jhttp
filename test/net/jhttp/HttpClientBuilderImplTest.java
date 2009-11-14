package net.jhttp;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

@Test(groups={"func"})
public class HttpClientBuilderImplTest {
    HttpClientBuilder hcb;

    HttpClientBuilder newInstance() {
        return new HttpClientBuilderImpl();
    }
    
    @BeforeMethod
    public void setUp() {
        hcb = newInstance();
    }
    
    @Test
    public void testBuild() {
        HttpClient c = hcb.build();
        assert c != null;
    }
}
