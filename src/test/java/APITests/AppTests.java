package APITests;


import APITests.Threads.CPULoadInstanceThread;
import APITests.Threads.CPULoadSampleTime;
import APITests.Threads.MemoryInstancesThread;
import APITests.Threads.MemorySampleTimeThread;
import com.sun.management.OperatingSystemMXBean;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;

import static APITests.ApplicationManipulation.startApplication;
import static APITests.ApplicationManipulation.stopApplication;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Random;

public class AppTests {

    private static final int STATUS_CODE_SUCCESS = 200;
    private static final int STATUS_CODE_CREATED = 201;
    private static final int STATUS_CODE_FAILURE = 400;
    private static final int STATUS_CODE_NOT_FOUND = 404;
    private static boolean todo_cpu_flag = true;
    public static int TOTAL_INSTANCES = 3;

    static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);

    public AppTests() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        CPULoadInstanceThread cpuInstance = new CPULoadInstanceThread();
        Thread t = new Thread(cpuInstance);
        t.start();

        CPULoadSampleTime cpuSample = new CPULoadSampleTime();
        Thread t2 = new Thread(cpuSample);
        t2.start();

        MemoryInstancesThread memoryInstance = new MemoryInstancesThread();
        Thread t3 = new Thread(memoryInstance);
        t3.start();

        MemorySampleTimeThread memorySample = new MemorySampleTimeThread();
        Thread t4 = new Thread(memorySample);
        t4.start();

        // ----- setup CSV for transaction time graphs ------
        ArrayList<FileWriter> todoCSVs = makeCSVs("todo", "transaction_time_sample_time", 0 );
        ArrayList<FileWriter> projectCSVs = makeCSVs("project", "transaction_time_sample_time", 0);
        ArrayList<FileWriter> categoryCSVs = makeCSVs("category", "transaction_time_sample_time", 0);

        ArrayList<FileWriter> todoCSVsInstances = makeCSVs("todo", "transaction_time_instances",1 );
        ArrayList<FileWriter> projectCSVsInstances = makeCSVs("project", "transaction_time_instances", 1);
        ArrayList<FileWriter> categoryCSVsInstances = makeCSVs("category", "transaction_time_instances",1 );

        int NUM_LOOPS = 10;

        TodosTests todos = new TodosTests();
        CategoriesTests categories = new CategoriesTests();
        ProjectsTests projects = new ProjectsTests();

        // Stat application
        System.out.print("Starting application...");
        AppTests.setUp();
        System.out.print("OK\n----------\n");


        //-------------------------

        // Setup todos, categories, projects
        setupTodos(TOTAL_INSTANCES);
        setupCategories(TOTAL_INSTANCES);
        setupProjects(TOTAL_INSTANCES);

        // Initialize test results containers
        ArrayList<TestResult> todosResults = new ArrayList<TestResult>();
        ArrayList<TestResult> categoriesResults = new ArrayList<TestResult>();
        ArrayList<TestResult> projectsResults = new ArrayList<TestResult>();

        //-------------------------
        // TODOS

//        todo_cpu_flag = true;
//
//        Thread cpu_loag_thread = new Thread(() -> start_cpu_load_thread_instances("todo", "DELETE"));
//        cpu_loag_thread.start();

        for (int i = 0; i < NUM_LOOPS; i++) {

            TestResult tr;
            long start_time;
            long end_time;
            String sample_time;

            // Add another todo
            Random rn = new Random();
            AppTests.createTodo("Test Todo #"+rn.nextInt(), false, "This is a test description #"+rn.nextInt());
            TOTAL_INSTANCES++;

            // Create todo
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_create_todo = todos.testCreateTodo();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_create_todo = end_time - start_time;

            addToCSV(todoCSVs.get(0), new String[]{sample_time,String.valueOf(t1_create_todo),String.valueOf(t2_create_todo)});
            addToCSV(todoCSVsInstances.get(0), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_create_todo),String.valueOf(t2_create_todo)});

            // Modify todo
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_modify_todo = todos.testModifyTodo();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_modify_todo = end_time - start_time;

            addToCSV(todoCSVs.get(1), new String[]{sample_time,String.valueOf(t1_modify_todo),String.valueOf(t2_modify_todo)});
            addToCSV(todoCSVsInstances.get(1), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_modify_todo),String.valueOf(t2_modify_todo)});

            // Delete todo
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_delete_todo = todos.testDeleteTodo();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_delete_todo = end_time - start_time;

            addToCSV(todoCSVs.get(2), new String[]{sample_time,String.valueOf(t1_delete_todo),String.valueOf(t2_delete_todo)});
            addToCSV(todoCSVsInstances.get(2), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_delete_todo),String.valueOf(t2_delete_todo)});

            tr = new TestResult(TOTAL_INSTANCES, t1_create_todo, t2_create_todo, t1_modify_todo, t2_modify_todo, t1_delete_todo, t2_delete_todo);
            todosResults.add(tr);

        }

        AppTests.displayResults(todosResults, "TODOS");


        //-------------------------
        // CATEGORIES

        TOTAL_INSTANCES = 3; // reset total number of instances
        for(int i = 0; i < NUM_LOOPS; i++) {
            TestResult tr;

            String sample_time;
            long start_time;
            long end_time;

            // Add another category
            Random rn = new Random();
            AppTests.createCategory("Test Category #"+rn.nextInt());
            TOTAL_INSTANCES++;

            // Create category
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_create_category = categories.testCreateCategory();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_create_category = end_time - start_time;

            addToCSV(categoryCSVs.get(0), new String[]{sample_time,String.valueOf(t1_create_category),String.valueOf(t2_create_category)});
            addToCSV(categoryCSVsInstances.get(0), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_create_category),String.valueOf(t2_create_category)});

            // Modify category
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_modify_category = categories.testModifyCategory();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_modify_category = end_time - start_time;

            addToCSV(categoryCSVs.get(1), new String[]{sample_time,String.valueOf(t1_modify_category),String.valueOf(t2_modify_category)});
            addToCSV(categoryCSVsInstances.get(1), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_modify_category),String.valueOf(t2_modify_category)});

            // Delete category
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_delete_category = categories.testDeleteCategory();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_delete_category = end_time - start_time;

            addToCSV(categoryCSVs.get(2), new String[]{sample_time,String.valueOf(t1_delete_category),String.valueOf(t2_delete_category)});
            addToCSV(categoryCSVsInstances.get(2), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_delete_category),String.valueOf(t2_delete_category)});

            tr = new TestResult(TOTAL_INSTANCES, t1_create_category, t2_create_category, t1_modify_category, t2_modify_category, t1_delete_category, t2_delete_category);
            categoriesResults.add(tr);
        }

        AppTests.displayResults(categoriesResults, "CATEGORIES");

        //-------------------------
        // PROJECTS

        TOTAL_INSTANCES = 3; // reset total number of instances
        for(int i = 0; i < NUM_LOOPS; i++) {

            TestResult tr;
            long start_time;
            long end_time;
            String sample_time;

            // Add another project
            Random rn = new Random();
            AppTests.createProject("Test Category #"+rn.nextInt(), false, true, "This is a test description #"+rn.nextInt());
            TOTAL_INSTANCES++;

            // Create project
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_create_project = projects.testCreateProject();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_create_project = end_time - start_time;

            addToCSV(projectCSVs.get(0), new String[]{sample_time,String.valueOf(t1_create_project),String.valueOf(t2_create_project)});
            addToCSV(projectCSVsInstances.get(0), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_create_project),String.valueOf(t2_create_project)});

            // Modify project
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_modify_project = projects.testModifyProject();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_modify_project = end_time - start_time;

            addToCSV(projectCSVs.get(1), new String[]{sample_time,String.valueOf(t1_modify_project),String.valueOf(t2_modify_project)});
            addToCSV(projectCSVsInstances.get(1), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_modify_project),String.valueOf(t2_modify_project)});

            // Delete project
            sample_time = getSampleTime();
            start_time = Calendar.getInstance().getTimeInMillis();
            long t2_delete_project = projects.testDeleteProject();
            end_time = Calendar.getInstance().getTimeInMillis();
            long t1_delete_project = end_time - start_time;

            addToCSV(projectCSVs.get(2), new String[]{sample_time,String.valueOf(t1_delete_project),String.valueOf(t2_delete_project)});
            addToCSV(projectCSVsInstances.get(2), new String[]{String.valueOf(TOTAL_INSTANCES),String.valueOf(t1_delete_project),String.valueOf(t2_delete_project)});

            tr = new TestResult(TOTAL_INSTANCES, t1_create_project, t2_create_project, t1_modify_project, t2_modify_project, t1_delete_project, t2_delete_project);
            projectsResults.add(tr);
        }

        AppTests.displayResults(projectsResults, "PROJECTS");

        //-------------------------

        // Teardown application
        System.out.print("Stopping application...");
        AppTests.teardown();
        System.out.print("OK\n----------\n");

        for (FileWriter f : categoryCSVs) {
            f.flush();
            f.close();
        }

        for (FileWriter f : projectCSVs) {
            f.flush();
            f.close();
        }

        for (FileWriter f : todoCSVs) {
            f.flush();
            f.close();
        }

        for (FileWriter f : categoryCSVsInstances) {
            f.flush();
            f.close();
        }

        for (FileWriter f : projectCSVsInstances) {
            f.flush();
            f.close();
        }

        for (FileWriter f : todoCSVsInstances) {
            f.flush();
            f.close();
        }

        // todo_cpu_flag = false;

        return;

    }

    public static void displayResults(ArrayList<TestResult> results, String resultsType) {

        System.out.println("Results for " + resultsType);
        System.out.println("Total instances \tT1 Create \tT2 Create \tT1 Modify \tT2 Modify \tT1 Delete \tT2 Delete");
        for (TestResult tr : results) {
            System.out.println(tr.getTotalInstances() + "\t\t\t\t\t" + tr.getT1Create() + "\t\t\t" + tr.getT2Create() + "\t\t\t" + tr.getT1Modify() + "\t\t\t" + tr.getT2Modify() + "\t\t\t" + tr.getT1Delete() + "\t\t\t" + tr.getT2Delete());
        }
        System.out.println("----------");

    }

    public static ArrayList<FileWriter> makeCSVs(String name, String folder, int sample_or_instance) throws IOException {

        FileWriter create = new FileWriter("csv/" + folder + "/" + name + "-create.csv");
        FileWriter modify = new FileWriter("csv/" + folder + "/" + name + "-modify.csv");
        FileWriter delete = new FileWriter("csv/" + folder + "/" + name + "-delete.csv");

        if (sample_or_instance == 0) {
            csvSetup(create, "Sample Time");
            csvSetup(modify, "Sample Time");
            csvSetup(delete, "Sample Time");
        }else{
            csvSetup(create, "Instances");
            csvSetup(modify, "Instances");
            csvSetup(delete, "Instances");
        }
        ArrayList<FileWriter> CSVs = new ArrayList<FileWriter>();
        CSVs.add(create);
        CSVs.add(modify);
        CSVs.add(delete);

        return CSVs;
    }

    public static void csvSetup(FileWriter writer, String xAxis) throws IOException {
        writer.append(xAxis);
        writer.append(',');
        writer.append("T1");
        writer.append(',');
        writer.append("T2");
        writer.append(',');
        writer.append('\n');
    }

    public static void addToCSV(FileWriter writer, String[] additions) throws IOException {
        for (String word : additions) {
            writer.append(word);
            writer.append(",");
        }
        writer.append("\n");
    }

    /**
     * Initialize some todos
     *
     * @param num_todos
     */
    public static void setupTodos(int num_todos) {
        RestAssured.baseURI = "http://localhost:4567";

        System.out.print("Initializing todos...");
        String title = "Test Todo #";
        String description = "This is a test description #";
        for (int i = 0; i < num_todos; i++) {
            AppTests.createTodo(title + (i + 1), false, description + (i + 1));
        }
        System.out.print("OK\n----------\n");

    }

    /**
     * Initialize some categories
     *
     * @param num_categories
     */
    public static void setupCategories(int num_categories) {
        RestAssured.baseURI = "http://localhost:4567";

        System.out.print("Initializing categories...");
        String title = "Test Category #";
        for (int i = 0; i < num_categories; i++) {
            AppTests.createCategory(title + (i + 1));
        }
        System.out.print("OK\n----------\n");
    }

    /**
     * Initialize some projects
     *
     * @param num_projects
     */
    public static void setupProjects(int num_projects) {
        RestAssured.baseURI = "http://localhost:4567";

        System.out.print("Initializing projects...");
        String title = "Test Project #";
        String description = "This is a test description #";
        for (int i = 0; i < num_projects; i++) {
            AppTests.createProject(title + (i + 1), false, true, description + (i + 1));
        }
        System.out.print("OK\n----------\n");
    }


    /**
     * This function creates a Todo.
     *
     * @param titleOfTodo       - the tile to be used to create the todo
     * @param doneStatus        - the doneStatus to be used to create the todo
     * @param descriptionOfTodo - the description to be used to create the todo
     * @return int corresponding to id of todo
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
     * @return int corresponding to id of the category
     */
    private static int createCategory(String categoryName) {
        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", categoryName);

        request.body(requestParams.toJSONString());

        Response response = request.post("/categories");

        if (response.statusCode() != STATUS_CODE_CREATED) {
            return -1;
        }

        int categoryID = Integer.parseInt((String) response.jsonPath().get("id"));
        return categoryID;
    }

    /**
     * This function will create a project
     *
     * @param title
     * @param completed
     * @param active
     * @param description
     * @return int corresponding to id of the project
     */
    private static int createProject(String title, boolean completed, boolean active, String description) {
        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", title);
        requestParams.put("completed", completed);
        requestParams.put("active", active);
        requestParams.put("description", description);

        request.body(requestParams.toJSONString());

        Response response = request.post("/projects");

        if (response.statusCode() != STATUS_CODE_CREATED) {
            return -1;
        }

        int projectID = Integer.parseInt((String) response.jsonPath().get("id"));
        return projectID;
    }

    /**
     * Starting application (setup)
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
                } catch (Exception e) {
                    Thread.sleep(200);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Teardown of application
     *
     * @throws InterruptedException
     */
    public static void teardown() throws InterruptedException {
        stopApplication();
        Thread.sleep(260);
    }

    private static String getSampleTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" +
                cal.get(Calendar.SECOND) + ":" + cal.get(Calendar.MILLISECOND);
    }

}
