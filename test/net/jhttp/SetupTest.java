package net.jhttp;

import org.testng.annotations.Test;

@Test(groups={"func"})
public class SetupTest {
    @Test(groups={"checkin"})
    public void exampleTestInPreCheckinTestSuite() {
    }

    public void exampleTestInFunctionalTestSuite() {
    }

    @Test(groups={"int"})
    public void exampleTestInIntegrationTestSuite() {
    }

    @Test(groups={"broken"})
    public void exampleDisabledFunctionalTest() {
        //assert false;
    }

    @Test(groups={"broken", "int"})
    public void exampleDisabledIntegrationTest() {
        //assert false;
    }

    @Test(groups={"broken", "checkin"})
    public void exampleDisabledPreCheckinTest() {
        //assert false;
    }
}
