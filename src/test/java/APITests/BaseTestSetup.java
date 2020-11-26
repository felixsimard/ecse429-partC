package APITests;

import org.junit.After;
import org.junit.Before;

import java.net.ConnectException;

import static APITests.ApplicationManipulation.startApplication;
import static APITests.ApplicationManipulation.stopApplication;
import static io.restassured.RestAssured.get;


public class BaseTestSetup {
    @Before
    public void setUp() {
        try {
            boolean appStarted = false;
            Process process = startApplication();
            Thread.sleep(200);
            while (!appStarted) {
                try {
                    get("http://localhost:4567/");
                    appStarted = true;
                } catch (Exception e){
                    Thread.sleep(200);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void teardown() throws InterruptedException {
        stopApplication();
        Thread.sleep(260);
    }
}
