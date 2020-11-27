package APITests;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.*;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.core.IsEqual.equalTo;


public class TodosTests extends BaseTestSetup {

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
    public TodosTests() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    /**
     * Test: create a valid todo with only title in body.
     * Endpoint: POST /todos
     */
    @Test
    public long testCreateTodo() throws JSONException {

        String title = "Must complete ECSE429 project";
        boolean doneStatus = false;
        String description = "Description";

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", title);
        requestParams.put("doneStatus", doneStatus);
        requestParams.put("description", description);

        request.body(requestParams.toJSONString());

        long startTime = Calendar.getInstance().getTimeInMillis();
        Response response = request.post("/todos");
        response.then();
        long endTime = Calendar.getInstance().getTimeInMillis();
        String body = response.getBody().asString();
        org.json.JSONObject jsonResponse = new org.json.JSONObject(body);
        Context.getContext().set("todoId", jsonResponse.getInt("id"), ContextElement.ElementType.TODO);

        response.then()
                .assertThat()
                .statusCode(equalTo(STATUS_CODE_CREATED))
                .body("title", equalTo(title),
                        "doneStatus", equalTo(String.valueOf(doneStatus)),
                        "description", equalTo(description));

        return endTime - startTime;
    }

    @Test
    public long testModifyTodo() {

        int todoId = Context.getContext().get("todoId");
        String newTitle = "New Title";
        boolean newDoneStatus = true;
        String newDescription = "New Description";

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();

        requestParams.put("title", newTitle);
        requestParams.put("doneStatus", newDoneStatus);
        requestParams.put("description", newDescription);

        request.body(requestParams.toJSONString());

        long startTime = Calendar.getInstance().getTimeInMillis();
        Response response = request.post("/todos/" + todoId);
        long endTime = Calendar.getInstance().getTimeInMillis();


        response.then()
                .assertThat()
                .statusCode(equalTo(STATUS_CODE_SUCCESS))
                .body("title", equalTo(newTitle),
                        "doneStatus", equalTo(String.valueOf(newDoneStatus)),
                        "description", equalTo(newDescription));

        return endTime - startTime;
    }

    @Test
    public long testDeleteTodo() {
        int todoId = Context.getContext().get("todoId");
        RequestSpecification request = RestAssured.given();

        request.get("/todos/" + todoId).then().assertThat().statusCode(STATUS_CODE_SUCCESS);

        long startTime = Calendar.getInstance().getTimeInMillis();
        request.delete("/todos/" + todoId).then().assertThat().statusCode(STATUS_CODE_SUCCESS);
        long endTime = Calendar.getInstance().getTimeInMillis();

        request.get("/todos/" + todoId).then().assertThat().statusCode(STATUS_CODE_NOT_FOUND);

        return endTime - startTime;
    }

}
