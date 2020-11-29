package APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class ProjectsTests extends BaseTestSetup {

    private static final int STATUS_CODE_SUCCESS = 200;
    private static final int STATUS_CODE_CREATED = 201;
    private static final int STATUS_CODE_NOT_FOUND = 404;

    /**
     * The expect array error for missing field. Used in invalid creation todo test.
     */
    ArrayList<String> fieldErrorArray = new ArrayList<String>(Arrays.asList("title : field is mandatory"));

    /**
     * The expected array error for adding non existing category to todo.
     */
    ArrayList<String> categoryError = new ArrayList<String>(Arrays.asList("Could not find thing matching value for id"));

    /**
     * Constructor which sets the base uri before runnning the tests.
     */
    public ProjectsTests() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    /**
     * Test: create a valid project.
     * Endpoint: POST /project
     */
    @Test
    public long testCreateProject() throws JSONException {

        String title = "Term ECSE429 Project";
        boolean completed = false;
        boolean active = true;
        String description = "Description for the term project";

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", title);
        requestParams.put("completed", completed);
        requestParams.put("active", active);
        requestParams.put("description", description);

        request.body(requestParams.toJSONString());

        long startTime = Calendar.getInstance().getTimeInMillis();
        Response response = request.post("/projects");
        response.then();
        long endTime = Calendar.getInstance().getTimeInMillis();
        String body = response.getBody().asString();
        org.json.JSONObject jsonResponse = new org.json.JSONObject(body);
        Context.getContext().set("projectId", jsonResponse.getInt("id"), ContextElement.ElementType.TODO);

        response.then()
                .assertThat()
                .statusCode(equalTo(STATUS_CODE_CREATED))
                .body("title", equalTo(title),
                        "completed", equalTo(String.valueOf(completed)),
                        "active", equalTo(String.valueOf(active)),
                        "description", equalTo(description));

        return endTime - startTime;
    }

    /**
     * Test: modify a valid project.
     * Endpoint: POST /projects
     */
    @Test
    public long testModifyProject() {

        int projectId = Context.getContext().get("projectId");
        String newTitle = "New Term 429 Project";
        boolean newCompleted = false;
        boolean newActive = false;
        String newDescription = "New Description for 429 Term Project";

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();

        requestParams.put("title", newTitle);
        requestParams.put("completed", newCompleted);
        requestParams.put("active", newActive);
        requestParams.put("description", newDescription);

        request.body(requestParams.toJSONString());

        long startTime = Calendar.getInstance().getTimeInMillis();
        Response response = request.post("/projects/" + projectId);
        long endTime = Calendar.getInstance().getTimeInMillis();

        response.then()
                .assertThat()
                .statusCode(equalTo(STATUS_CODE_SUCCESS))
                .body("title", equalTo(newTitle),
                        "completed", equalTo(String.valueOf(newCompleted)),
                        "completed", equalTo(String.valueOf(newActive)),
                        "description", equalTo(newDescription));

        return endTime - startTime;
    }

    /**
     * Test: delete a valid project.
     * Endpoint: POST /projects
     */
    @Test
    public long testDeleteProject() {
        int projectId = Context.getContext().get("projectId");
        RequestSpecification request = RestAssured.given();

        request.get("/projects/" + projectId).then().assertThat().statusCode(STATUS_CODE_SUCCESS);

        long startTime = Calendar.getInstance().getTimeInMillis();
        request.delete("/projects/" + projectId).then().assertThat().statusCode(STATUS_CODE_SUCCESS);
        long endTime = Calendar.getInstance().getTimeInMillis();

        request.get("/projects/" + projectId).then().assertThat().statusCode(STATUS_CODE_NOT_FOUND);

        return endTime - startTime;
    }





}
