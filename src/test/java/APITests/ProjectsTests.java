package APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class ProjectsTests extends BaseTestSetup {

    private static final int CREATED_STATUS_CODE = 201;
    private static final int OK_STATUS_CODE = 200;
    private static final int BAD_REQUEST_STATUS_CODE = 400;

    public ProjectsTests() {
        RestAssured.baseURI = "http://localhost:4567/projects";
    }

    @Test
    public void testCreateProjectValidInfo() {

        String title = "proj1";
        boolean completed = false;
        boolean active = true;
        String description = "this is a proj";

        String requestBody = withValidInfo(title, completed, active, description);

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(requestBody);

        request.when().post().then().
                assertThat().
                statusCode(CREATED_STATUS_CODE).
                body("title", equalTo(title),
                        "completed", equalTo(String.valueOf(completed)),
                        "active", equalTo(String.valueOf(active)),
                        "description", equalTo(String.valueOf(description)));
    }

    @Test
    public void testCreateProjectNoInfo() {

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.when().post().then().
                assertThat().
                statusCode(CREATED_STATUS_CODE).
                body("title", equalTo(""),
                        "completed", equalTo(String.valueOf(false)),
                        "active", equalTo(String.valueOf(false)),
                        "description", equalTo(String.valueOf("")));

    }

    @Test
    public void testCreateProjectNumberAsTitleSuccess() {

        int title = 4;

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", title);

        request.body(requestParams.toJSONString());

        request.post()
                .then()
                .assertThat()
                .statusCode(equalTo(CREATED_STATUS_CODE))
                .body("title", equalTo(String.valueOf((float) title)));
    }

    @Test
    public void testCreateProjectNumberAsTitleFailure() {

        int title = 4;

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", title);

        request.body(requestParams.toJSONString());

        request.post()
                .then()
                .assertThat()
                .statusCode(equalTo(CREATED_STATUS_CODE))
                .body("title", equalTo(title));
    }

    @Test
    public void testCreateProjectWithInvalidCompletedType() {
        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        String requestBody = withInvalidCompleted("false");

        request.body(requestBody);

        request.when().post().then().assertThat().statusCode(BAD_REQUEST_STATUS_CODE).
                body("errorMessages[0]", equalTo("Failed Validation: completed should be BOOLEAN"));
    }

    @Test
    public void testGetAllProjectsShowsNewProjects() {
        String title1 = "new proj 1";
        String title2 = "new proj 2";

        createValidProject(title1, false, false, "");
        createValidProject(title2, false, false, "");

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        Response response = request.when().get().then().assertThat().statusCode(OK_STATUS_CODE).contentType(ContentType.JSON).extract().response();

        String titles = response.jsonPath().getString("projects.title");

        assert (titles.contains(title1));
        assert (titles.contains(title2));
    }

    @Test
    public void testGetAllProjectsCommandLineQuery() throws IOException {
        String title1 = "new proj 1";
        String title2 = "new proj 2";

        createValidProject(title1, false, false, "");
        createValidProject(title2, false, false, "");

        String curlRequest = "curl -v http://localhost:4567/projects";
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(curlRequest).getInputStream()).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";

        assert (response.contains(title1));
        assert (response.contains(title2));
    }

    @Test
    public void testCreateNewProjectXML() throws ParserConfigurationException {
        String title = "new proj";
        StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlBuilder.append("<project>").append("<title>" + title + "</title>");
        xmlBuilder.append("</project>");


        RequestSpecification request = given();

        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        request.body(xmlBuilder.toString());

        request.when().post().then().
                assertThat().
                statusCode(CREATED_STATUS_CODE).
                body("response.title", equalTo(title),
                        "response.completed", equalTo(String.valueOf(false)),
                        "response.active", equalTo(String.valueOf(false)),
                        "response.description", equalTo(String.valueOf("")));
    }

    @Test
    public void testGetProjectById() {
        String title = "proj";

        int id = createValidProject(title, false, false, "");
        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.when().get("/" + id).then().assertThat().
                statusCode(OK_STATUS_CODE).
                body("projects[0].title", equalTo(title));
    }

    @Test
    public void testAmmendProjectById() {
        String oldTitle = "old title";
        String newTitle = "new title";

        int id = createValidProject(oldTitle, false, false, "");
        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        String requestBody = withValidInfo(newTitle, false, false, "");

        request.body(requestBody);

        request.when().post("/" + id).then().assertThat().
                statusCode(OK_STATUS_CODE).
                body("title", equalTo(newTitle));
    }

    @Test
    public void testDeleteExistingProject() {
        String title = "proj to delete";

        int id = createValidProject(title, false, false, "");

        RequestSpecification request = given();

        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        request.when().delete("/" + id).then().
                assertThat().
                statusCode(OK_STATUS_CODE);

        RequestSpecification getAllProjectsRequest = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        Response response = request.when().get().then().assertThat().statusCode(OK_STATUS_CODE).contentType(ContentType.JSON).extract().response();

        String ids = response.jsonPath().getString("projects.id");

        assert (!ids.contains("" + id));
    }

    @Test
    public void testAddCategoryRelationshipToProject() {
        int id = createValidProject("proj", false, false, "");

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.body("{ \"id\": \"1\"}");

        request.when().post("/" + id + "/categories").then().assertThat().
                statusCode(CREATED_STATUS_CODE);
    }

    @Test
    public void testDeleteCategoryRelationshipWithProject() {
        int id = createValidProject("proj", false, false, "");

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.body("{ \"id\": \"1\"}");

        request.when().post("/" + id + "/categories").then().assertThat().
                statusCode(CREATED_STATUS_CODE);

        request.when().delete("/" + id + "/categories/" + 1).then().assertThat().
                statusCode(OK_STATUS_CODE);
    }

    @Test
    public void testAddTaskRelationshipWithProject() {
        int id = createValidProject("proj", false, false, "");

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.body("{ \"id\": \"1\"}");

        request.when().post("/" + id + "/tasks").then().assertThat().
                statusCode(CREATED_STATUS_CODE);
    }

    @Test
    public void testDeleteTaskRelationshipWithProject() {
        int id = createValidProject("proj", false, false, "");

        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.body("{ \"id\": \"1\"}");

        request.when().post("/" + id + "/tasks").then().assertThat().
                statusCode(CREATED_STATUS_CODE);

        request.when().delete("/" + id + "/tasks/" + 1).then().assertThat().
                statusCode(OK_STATUS_CODE);
    }

    @Test
    public void testCreateProjectBadXML() throws ParserConfigurationException {
        String title = "project DUMMY";
        StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlBuilder.append("<proj>").append("<title>" + title + "</title>");
        xmlBuilder.append("</project>");


        RequestSpecification request = given();

        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        request.body(xmlBuilder.toString());

        request.when().post().then().
                assertThat().
                statusCode(equalTo(BAD_REQUEST_STATUS_CODE));
    }

    @Test
    public void testCreateProjectBadJSON() {
        RequestSpecification request = given();

        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.body("{ \"badJson\" : bad");


        request.when().post().then().assertThat().statusCode(BAD_REQUEST_STATUS_CODE);
    }

    private String withValidInfo(String title, boolean completed, boolean active, String description) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("title", title);
        requestParams.put("completed", completed);
        requestParams.put("active", active);
        requestParams.put("description", description);
        return requestParams.toJSONString();
    }

    private String withInvalidCompleted(String completed) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("completed", completed);
        return requestParams.toJSONString();
    }

    private int createValidProject(String title, boolean completed, boolean active, String description) {
        RequestSpecification request = given();

        String requestBody = withValidInfo(title, completed, active, description);
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(requestBody);
        int id;

        try {
            id = Integer.parseInt(request.post().then().extract().path("id").toString());
        } catch (Exception e) {
            return -1;
        }

        return id;
    }

}
