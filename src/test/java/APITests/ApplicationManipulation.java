package APITests;

import java.io.IOException;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

public class ApplicationManipulation {

    private static Process process;

    public static Process startApplication() throws InterruptedException {
        Runtime rt = Runtime.getRuntime();
        try {
            process = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return process;
    }

    public static void stopApplication() {
        process.destroy();
    }
}
