package APITests;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import java.util.ArrayList;
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


    public static void main(String[] args) throws InterruptedException {

        int TOTAL_INSTANCES = 3;
        int NUM_LOOPS = 10;

        TodosTests todos = new TodosTests();

        // Stat application
        System.out.print("Starting application...");
        AppTests.setUp();
        System.out.print("OK\n----------\n");


        //-------------------------

        // Setup todos
        setupTodos(TOTAL_INSTANCES);

        ArrayList<TestResult> todosResults = new ArrayList<TestResult>();

        for(int i = 0; i < NUM_LOOPS; i++) {

            TestResult tr;
            long start_time;
            long end_time;

            // Add another todo
            Random rn = new Random();
            AppTests.createTodo("Test Todo #"+rn.nextInt(), false, "This is a test description #"+rn.nextInt());
            TOTAL_INSTANCES++;

            // Create todo
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_create_todo = todos.testCreateTodo();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_create_todo = end_time - start_time;

            // Modify todo
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_modify_todo = todos.testModifyTodo();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_modify_todo = end_time - start_time;

            // Delete todo
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_delete_todo = todos.testDeleteTodo();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_delete_todo = end_time - start_time;

            tr = new TestResult(TOTAL_INSTANCES, t1_create_todo, t2_create_todo, t1_modify_todo, t2_modify_todo, t1_delete_todo, t2_delete_todo);
            todosResults.add(tr);

        }

        AppTests.displayResults(todosResults, "TODOS");


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

    public static void displayResults(ArrayList<TestResult> results, String resultsType) {

        System.out.println("Results for " + resultsType);
        System.out.println("Total "+resultsType+" \tT1 Create \tT2 Create \tT1 Modify \tT2 Modify \tT1 Delete \tT2 Delete");
        for(TestResult tr: results) {
            System.out.println(tr.getTotalInstances()+ "\t\t\t\t" +tr.getT1Create()+ "\t\t\t" +tr.getT2Create()+ "\t\t\t" +tr.getT1Modify()+ "\t\t\t" +tr.getT2Modify()+ "\t\t\t" +tr.getT1Delete()+ "\t\t\t" +tr.getT2Delete());
        }
        System.out.println("----------");

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
