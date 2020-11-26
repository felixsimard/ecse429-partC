package APITests;

import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.security.auth.login.CredentialException;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

public class CategoriesTests extends BaseTestSetup{

    private static final String BASEURI = "http://localhost:4567";
    private static final int SUCCESS_STATUS_CODE = 200;
    private static final int CREATED_STATUS_CODE = 201;
    private static final int FAILED_STATUS_CODE = 400;

    @Test
    public void testCreateValidCategoryWithTitleAndDescription() {

        String title = "Jet Category";
        String description = "Working from private jet";

        JSONObject requestBody = new JSONObject();
        requestBody.put("title", title);
        requestBody.put("description", description);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE)
                .assertThat().body(containsString("id"))
                .assertThat().body(containsString(title))
                .assertThat().body(containsString(description));
    }

    @Test
    public void testCreateValidCategoryOnlyTitle() {

        String title = "Jet Category";

        JSONObject requestBody = new JSONObject();
        requestBody.put("title", title);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)
                .body(requestBody.toJSONString())

                .post("/categories")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE)
                .assertThat().body(containsString("id"))
                .assertThat().body(containsString(title));
    }

    @Test
    public void testCreateCategoryWithXml() {

        String title = "Jet Category";
        String description = "Working from private jet";


        given()
                .header("Content-Type", "application/xml")
                .header("Accept", "application/xml")
                .param("description", description)
                .param("title", title)
                .baseUri(BASEURI)

                .post("/categories")

                .then()
                .assertThat().statusCode(FAILED_STATUS_CODE);
    }

    @Test
    public void testCreateCategoryNoTitle() {

        String description = "Working from private jet";

        JSONObject requestBody = new JSONObject();
        requestBody.put("description", description);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories")

                .then()
                .assertThat().statusCode(FAILED_STATUS_CODE);
    }

    @Test
    public void testGetAllCategories() {
        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .get("/categories")

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testGetAllCategoryHeads() {
        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .head("/categories")

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testGetCategoryFromValidId() {
        String id = CategoriesHelper.createCategory("Test title", "Test description");

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .get("/categories/" + id)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE)
                .assertThat().body(containsString(id));
    }

    @Test
    public void testGetHeadOfCategoryFromValidId() {
        String id = CategoriesHelper.createCategory("Test title", "Test description");

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .head("/categories/" + id)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testPostModifyCategoryTitleAndDescriptionFromValidId() {
        String old_title = "Old title";
        String old_description = "Old description";

        String new_title = "New title";
        String new_description = "New description";

        String id = CategoriesHelper.createCategory(old_title, old_description);

        JSONObject requestBody = new JSONObject();
        requestBody.put("title", new_title);
        requestBody.put("description", new_description);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + id)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE)
                .assertThat().body(containsString(id))
                .assertThat().body(containsString(new_title))
                .assertThat().body(containsString(new_description));
    }

    @Test
    public void testPutModifyCategoryTitleAndDescriptionFromValidId() {
        String old_title = "Old title";
        String old_description = "Old description";

        String new_title = "New title";
        String new_description = "New description";

        String id = CategoriesHelper.createCategory(old_title, old_description);

        JSONObject requestBody = new JSONObject();
        requestBody.put("title", new_title);
        requestBody.put("description", new_description);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .put("/categories/" + id)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE)
                .assertThat().body(containsString(id))
                .assertThat().body(containsString(new_title))
                .assertThat().body(containsString(new_description));
    }

    @Test
    public void testDeleteCategoryFromValidId() {

        String id = CategoriesHelper.createCategory("Test title", "Test description");

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .delete("/categories/" + id)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testPostProjectToCategory() {
        String projectId = CategoriesHelper.createProject();
        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", projectId);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);
    }

    @Test
    public void testGetAllProjectsOfCategoryFromValidId() {
        String projectId1 = CategoriesHelper.createProject();
        String projectId2 = CategoriesHelper.createProject();

        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody1 = new JSONObject();
        requestBody1.put("id", projectId1);

        JSONObject requestBody2 = new JSONObject();
        requestBody2.put("id", projectId2);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody1.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody2.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .get("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE)
                .assertThat().body(containsString("id\":\"" + projectId1))
                .assertThat().body(containsString("id\":\"" + projectId2));
    }

    @Test
    public void testHeadAllProjectsOfCategoryFromValidId() {
        String projectId1 = CategoriesHelper.createProject();
        String projectId2 = CategoriesHelper.createProject();

        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody1 = new JSONObject();
        requestBody1.put("id", projectId1);

        JSONObject requestBody2 = new JSONObject();
        requestBody2.put("id", projectId2);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody1.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody2.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .head("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testDeleteProjectLinkOfCategoryFromValidId() {
        String projectId = CategoriesHelper.createProject();
        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", projectId);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/projects")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .delete("/categories/" + categoryId + "/projects/" + projectId)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testPostTodoToCategory() {
        String todoId = CategoriesHelper.createTodo();
        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", todoId);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);
    }

    @Test
    public void testGetAllTodosOfCategoryFromValidId() {
        String todoId1 = CategoriesHelper.createTodo();
        String todoId2 = CategoriesHelper.createTodo();

        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody1 = new JSONObject();
        requestBody1.put("id", todoId1);

        JSONObject requestBody2 = new JSONObject();
        requestBody2.put("id", todoId2);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody1.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody2.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .get("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE)
                .assertThat().body(containsString("id\":\"" + todoId1))
                .assertThat().body(containsString("id\":\"" + todoId2));
    }

    @Test
    public void testHeadAllTodosOfCategoryFromValidId() {
        String todoId1 = CategoriesHelper.createTodo();
        String todoId2 = CategoriesHelper.createTodo();

        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody1 = new JSONObject();
        requestBody1.put("id", todoId1);

        JSONObject requestBody2 = new JSONObject();
        requestBody2.put("id", todoId2);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody1.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody2.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .head("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }

    @Test
    public void testDeleteTodoLinkOfCategoryFromValidId() {
        String todoId = CategoriesHelper.createTodo();
        String categoryId = CategoriesHelper.createCategory("Test title", "Test description");

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", todoId);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toJSONString())
                .baseUri(BASEURI)

                .post("/categories/" + categoryId + "/todos")

                .then()
                .assertThat().statusCode(CREATED_STATUS_CODE);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .baseUri(BASEURI)

                .delete("/categories/" + categoryId + "/todos/" + todoId)

                .then()
                .assertThat().statusCode(SUCCESS_STATUS_CODE);
    }
}
