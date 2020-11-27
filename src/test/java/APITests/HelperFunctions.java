//package APITests;
//
//import ecse429.storytesting.model.*;
//import gherkin.deps.com.google.gson.*;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import io.restassured.specification.RequestSpecification;
//import org.json.simple.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static io.restassured.RestAssured.get;
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.CoreMatchers.containsString;
//
//
//public class HelperFunctions {
//
//    private static final int STATUS_CODE_CREATED = 201;
//    private static final int STATUS_CODE_OK = 200;
//    private static Gson gson = new Gson();
//    private static Process process;
//
//    public HelperFunctions() {
//        RestAssured.baseURI = "http://localhost:4567/";
//    }
//
//    //--------APPLICATION----------//
//
//    public static Process startApplication() {
//        System.out.println("Starting application...");
//        Runtime rt = Runtime.getRuntime();
//        try {
//            process = rt.exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        boolean appStarted = false;
//        while (!appStarted) {
//            try {
//                get("http://localhost:4567/");
//                appStarted = true;
//            } catch (Exception e1) {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e2) {
//                    e2.printStackTrace();
//                }
//            }
//        }
//
//        return process;
//    }
//
//    public static void stopApplication() {
//        if (process != null) process.destroy();
//    }
//
//    public static void restoreInitialState() {
//        List<ContextElement> list = Context.getContext().getAllElementsToDelete();
//
//        for (ContextElement e : list) {
//            if (e.type == ContextElement.ElementType.PROJECT) {
//                deleteProject(e.id);
//                System.out.println("deleted project with id: " + e.id);
//            } else if (e.type == ContextElement.ElementType.TODO) {
//                System.out.println("deleted todo with id: " + e.id);
//                deleteTodo(e.id);
//            } else if (e.type == ContextElement.ElementType.CATEGORY) {
//                System.out.println("deleted category with id: " + e.id);
//                deleteCategory(e.id);
//            }
//        }
//
//        Context.resetContext();
//
////        List<Project> projects = getAllProjects();
////        for (Project p: projects) {
////            deleteProject(p.getId());
////            System.out.println("deleted project with id: " + p.getId());
////        }
////
////        List<Category> categories = getAllCategories();
////        for (Category c: categories) {
////            deleteCategory(c.getId());
////            System.out.println("deleted categories with id: " + c.getId());
////        }
////
////        List<Todo> todos = getAllTodos();
////        for (Todo t: todos) {
////            deleteTodo(t.getId());
////            System.out.println("deleted todos with id: " + t.getId());
////        }
//
//    }
//
//    //---------PROJECTS------------//
//
//    public static List<Project> getAllProjects() {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        Response response = request.get("/projects");
//
//        ProjectsResponse results = gson.fromJson(response.asString(), ProjectsResponse.class);
//        return results.getProjects();
//    }
//
//    public static Project createProject(String title, String completed, String active, String description) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        JSONObject requestParams = new JSONObject();
//        if (!title.equals("")) requestParams.put("title", title);
//        if (!completed.equals(""))
//            requestParams.put("completed", getBoolean(completed) != null ? getBoolean(completed) : completed);
//        if (!active.equals("")) requestParams.put("active", getBoolean(active) != null ? getBoolean(active) : active);
//        if (!description.equals("")) requestParams.put("description", description);
//
//        request.body(requestParams.toJSONString());
//
//        Response response = request.post("/projects");
//
//        int statusCode = response.getStatusCode();
//
//        Context.getContext().set("status_code", statusCode, ContextElement.ElementType.OTHER);
//
//        if (statusCode / 100 == 4) {
//            ErrorResponse e = gson.fromJson(response.asString(), ErrorResponse.class);
//            StepDefinitions.errorMessage = e.getErrorMessages().get(0);
//            return null;
//        }
//
//        Project result = gson.fromJson(response.asString(), Project.class);
//        return result;
//    }
//
//    public static void deleteProject(int projectId) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        Response response = request.delete("/projects/" + projectId);
//    }
//
//    public static int addTodoToProject(int todoId, int projectId) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//        ;
//
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("id", "" + todoId);
//
//        request.body(requestParams.toJSONString());
//
//        Response response = request.post("/projects/" + projectId + "/tasks");
//
//        return response.statusCode();
//    }
//
//    public static int deleteTodoFromProject(int todoId, int projectId) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        Response response = request.delete("/projects/" + projectId + "/tasks/" + todoId);
//
//        int statusCode = response.getStatusCode();
//
//        if (statusCode / 100 == 4) {
//            ErrorResponse e = gson.fromJson(response.asString(), ErrorResponse.class);
//            StepDefinitions.errorMessage = e.getErrorMessages().get(0);
//        }
//
//        Context.getContext().set("status_code", statusCode, ContextElement.ElementType.OTHER);
//
//        return statusCode;
//    }
//
//    public static int deleteProjectFromTodo(int todoId, int projectId) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        Response response = request.delete("/todos/" + todoId + "/tasksof/" + projectId);
//
//        int statusCode = response.getStatusCode();
//
//        if (statusCode / 100 == 4) {
//            ErrorResponse e = gson.fromJson(response.asString(), ErrorResponse.class);
//            StepDefinitions.errorMessage = e.getErrorMessages().get(0);
//        }
//
//        Context.getContext().set("status_code", statusCode, ContextElement.ElementType.OTHER);
//
//        return statusCode;
//    }
//
//    //---------CATEGORIES------------//
//
//
//    public static Category createCategory(String title, String description) {
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("title", title);
//        requestBody.put("description", description);
//
//        RequestSpecification request = given()
//                .body(requestBody.toJSONString())
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .baseUri("http://localhost:4567");
//
//        Response response = request.post("/categories");
//
//        response.then()
//                .assertThat().statusCode(STATUS_CODE_CREATED)
//                .assertThat().body(containsString("id"))
//                .assertThat().body(containsString(title))
//                .assertThat().body(containsString(description));
//
//        //return response.jsonPath().getString("id");
//        Category c = gson.fromJson(response.asString(), Category.class);
//        return c;
//    }
//
//    public static void deleteCategory(int categoryId) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        request.delete("/categories/" + categoryId);
//    }
//
//    public static List<Category> getAllCategories() {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        Response response = request.get("/categories");
//
//        JsonObject json = new JsonParser().parse(response.asString()).getAsJsonObject();
//        JsonArray arr = json.getAsJsonArray("categories");
//
//        List<Category> result = new ArrayList<>();
//        for (JsonElement obj : arr) {
//            Category cat = gson.fromJson(obj.getAsJsonObject(), Category.class);
//            result.add(cat);
//        }
//        return result;
//    }
//
//    public static int linkTodoAndCategory(int todoId, int categoryId) {
//
//        // add the category to the todo
//        RequestSpecification requestPost = RestAssured.given();
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("id", String.valueOf(categoryId));
//
//        requestPost.body(requestParams.toJSONString())
//                .baseUri("http://localhost:4567");
//
//
//        Response r = requestPost.post("/todos/" + todoId + "/categories");
//        return r.getStatusCode();
//    }
//
//    public static int linkCategoryAndTodo(int todoId, int categoryId) {
//
//        // add the todo to category
//        RequestSpecification requestPost = RestAssured.given();
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("id", String.valueOf(todoId));
//
//        requestPost.body(requestParams.toJSONString())
//                .baseUri("http://localhost:4567");
//
//
//        Response r = requestPost.post("/categories/" + categoryId + "/todos");
//        return r.getStatusCode();
//    }
//
//    public static int removeTodoAndCategoryLink(int todoId, int categoryId) {
//        RequestSpecification requestPost = RestAssured.given();
//        Response r = requestPost.delete("http://localhost:4567/todos/" + todoId + "/categories/" + categoryId);
//        return r.getStatusCode();
//    }
//
//    public static Category getCategoryFromTodoId(int todoId, String category_title) {
//
//        RequestSpecification request = given()
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .baseUri("http://localhost:4567");
//
//        Response response = request.get("/todos/" + todoId + "/categories");
//
//        JsonObject json = new JsonParser().parse(response.asString()).getAsJsonObject();
//        JsonArray arr = json.getAsJsonArray("categories");
//        System.out.println("array size: " + arr.size());
//        Category c = null;
//        for (JsonElement e : arr) {
//            JsonObject obj = e.getAsJsonObject();
//            System.out.println("title2 " + category_title);
//            System.out.println("title1 " + obj.get("title").getAsString());
//            if (category_title.equals(obj.get("title").getAsString())) {
//                c = gson.fromJson(obj, Category.class);
//            }
//        }
//        return c;
//    }
//
//    //---------TODOS------------//
//
//    public static Todo createTodo(String title, boolean doneStatus, String description) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("title", title);
//        requestParams.put("doneStatus", doneStatus);
//        requestParams.put("description", description);
//
//        request.body(requestParams.toJSONString());
//
//        Response response = request.post("/todos");
//
//        Todo result = gson.fromJson(response.asString(), Todo.class);
//        return result;
//    }
//
//    public static List<Todo> getAllTodos() {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        Response response = request.get("/todos");
//
//        JsonObject json = new JsonParser().parse(response.asString()).getAsJsonObject();
//        JsonArray arr = json.getAsJsonArray("todos");
//
//        List<Todo> result = new ArrayList<>();
//        for (JsonElement obj : arr) {
//            Todo todo = gson.fromJson(obj.getAsJsonObject(), Todo.class);
//            result.add(todo);
//        }
//        return result;
//    }
//
//    public static void deleteTodo(int todoId) {
//        RequestSpecification request = RestAssured.given().baseUri("http://localhost:4567");
//
//        request.delete("/todos/" + todoId);
//    }
//
//    public static Todo getTodoFromTodoId(int todoId) {
//
//        RequestSpecification request = given()
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .baseUri("http://localhost:4567");
//
//        Response response = request.get("/todos/" + todoId);
//
//        TodosResponse todos = gson.fromJson(response.asString(), TodosResponse.class);
//        return todos.getTodos().get(0);
//    }
//
//    public static void addProjectToTodoTasksOf(int todoId, int projectId) {
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("id", todoId);
//
//        RequestSpecification request = given()
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .baseUri("http://localhost:4567")
//                .body(requestParams.toJSONString());
//
//        request.post("/todos/" + todoId + "/tasksof");
//    }
//
//    public static Todo updateTodoDescription(int todoId, String new_description, String oldTitle) throws Exception{
//
//        RequestSpecification request = RestAssured.given();
//
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("description", new_description);
//        requestParams.put("title", oldTitle);
//
//        request.body(requestParams.toJSONString())
//                .baseUri("http://localhost:4567");
//
//        Response response = request.put("/todos/" + todoId);
//
//        int statusCode = response.getStatusCode();
//
//        Context.getContext().set("status_code", statusCode, ContextElement.ElementType.OTHER);
//
//        Todo result = gson.fromJson(response.asString(), Todo.class);
//
//        return result;
//    }
//
//    public static int updateTodoDoneStatus(int todoId, boolean doneStatus) {
//        RequestSpecification request = RestAssured.given();
//
//        JSONObject requestParams = new JSONObject();
//        requestParams.put("doneStatus", doneStatus);
//
//        request.body(requestParams.toJSONString())
//                .baseUri("http://localhost:4567");
//
//        Response response = request.post("/todos/" + todoId);
//
//        return response.getStatusCode();
//    }
//
//    public static List<String> getAllIncompleteTasksOfProject(int projectId) {
//        RequestSpecification requestPost = RestAssured.given();
//        ArrayList list;
//        try {
//            list = requestPost.get(String.format("http://localhost:4567/projects/%d/tasks?doneStatus=false", projectId))
//                    .then()
//                    .extract()
//                    .body()
//                    .jsonPath()
//                    .get("todos.id");
//            System.out.println("Here: " + list.toString());
//        } catch (Exception e) {
//            list = new ArrayList<>();
//            System.out.println(e);
//        }
//
//        list.forEach((n) -> n = Integer.parseInt((String) n));
//        Collections.sort(list);
//        list.forEach((n) -> n = "" + n);
//        return list;
//    }
//
//    public static List<Integer> getAllIncompleteTasksOfProjectWithHighPriority(int categoryId) {
//        RequestSpecification requestPost = RestAssured.given();
//        ArrayList list;
//        try {
//            list = requestPost.get(String.format("http://localhost:4567/categories/%d/todos?doneStatus=false", categoryId))
//                    .then()
//                    .extract()
//                    .body()
//                    .jsonPath()
//                    .get("todos.id");
//            System.out.println("Here: " + list.toString());
//        } catch (Exception e) {
//            list = new ArrayList<>();
//            System.out.println(e);
//        }
//
//        list.forEach((n) -> n = Integer.parseInt((String) n));
//        Collections.sort(list);
//        return list;
//    }
//
//    private static Boolean getBoolean(String s) {
//        Boolean result;
//        switch (s) {
//            case "false":
//                result = false;
//                break;
//            case "true":
//                result = true;
//                break;
//            default:
//                result = null;
//        }
//        return result;
//    }
//
//}
