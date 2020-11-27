package APITests;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import java.util.Calendar;

import static APITests.ApplicationManipulation.startApplication;
import static APITests.ApplicationManipulation.stopApplication;
import static io.restassured.RestAssured.get;

import java.util.Random;

public class AppTests {

    private static final int STATUS_CODE_SUCCESS = 200;
    private static final int STATUS_CODE_CREATED = 201;
    private static final int STATUS_CODE_FAILURE = 400;
    private static final int STATUS_CODE_NOT_FOUND = 404;

    public AppTests() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    private class Test {

        int total_instances = 0;
        long t1_create;
        long t2_create;

        long t1_modify;
        long t2_modify;

        long t1_delete;
        long t2_delete;

        // getters and setters

    }

    public static void main(String[] args) throws InterruptedException {

        int NUM_TODOS_INITIAL = 3;
        int NUM_LOOPS = 3;

        // Stat application
        System.out.print("Starting application...");
        AppTests.setUp();
        System.out.print("OK\n----------\n");


        //-------------------------

        // Setup todos
        setupTodos(NUM_TODOS_INITIAL);

        for(int i = 0; i < NUM_LOOPS; i++) {

            // Add another todo
            Random rn = new Random();
            AppTests.createTodo("Test Todo #"+rn.nextInt(), false, "This is a test description #"+rn.nextInt());

            // Create todo
            long start_time = Calendar.getInstance().getTimeInMillis();
            // TodosTest.testCreateTodo()
            long end_time = Calendar.getInstance().getTimeInMillis();
            // end_time - start_time

            // Modify todo

            // Delete todo


        }


        //-------------------------

        // Setup categories




        //-------------------------

        // Setup projects




        //-------------------------


        // Teardown application
        System.out.print("Stopping application...");
        AppTests.teardown();
        System.out.print("OK\n----------\n");

    }

    /**
     * Initialize some todos
     * @param num_todos
     */
    public static void setupTodos(int num_todos) {

        RestAssured.baseURI = "http://localhost:4567";

        System.out.print("Initializing todos...");
        String title = "Test Todo #";
        String description = "This is a test description #";
        for(int i = 0; i < num_todos; i++) {
            AppTests.createTodo(title + (i+1), false, description + (i+1));
        }
        System.out.print("OK\n----------\n");

    }


    /**
     * This function creates a Todo.
     *
     * @param titleOfTodo - the tile to be used to create the todo
     * @param doneStatus - the doneStatus to be used to create the todo
     * @param descriptionOfTodo - the description to be used to create the todo
     *
     * @return int corresponding to id of todo
     *
     * @throws Exception - if todo could not be created throws an exception -
     */
    private static int createTodo(String titleOfTodo, Boolean doneStatus, String descriptionOfTodo) {
        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", titleOfTodo);
        requestParams.put("doneStatus", doneStatus);
        requestParams.put("description", descriptionOfTodo);

        request.body(requestParams.toJSONString());

        Response response = request.post("/todos");

        if (response.statusCode() != STATUS_CODE_CREATED) {
            return -1;
        }

        // want to return the 'Object' value of JSON field "id" as int
        int todoID = Integer.parseInt((String) response.jsonPath().get("id"));
        return todoID;
    }

    /**
     * This function will create a category
     *
     * @param categoryName
     *
     * @return int corresponding to id of the category
     *
     * @throws Exception - if category could not be created throws an exception -
     */
    private int createCategory(String categoryName) throws Exception {
        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", categoryName);

        request.body(requestParams.toJSONString());

        Response response = request.post("/categories");

        if (response.statusCode() != STATUS_CODE_CREATED) {
            throw new Exception("Could not create dummy category");
        }

        int categoryID = Integer.parseInt((String) response.jsonPath().get("id"));
        return categoryID;
    }

    /**
     *
     * Starting application (setup)
     *
     */
    public static void setUp() {
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

    /**
     *
     * Teardown of application
     * @throws InterruptedException
     */
    public static void teardown() throws InterruptedException {
        stopApplication();
        Thread.sleep(260);
    }


}
