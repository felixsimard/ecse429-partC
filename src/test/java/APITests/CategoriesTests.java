package APITests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.security.auth.login.CredentialException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsEqual.equalTo;

public class CategoriesTests extends BaseTestSetup{

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
     * Constructor which sets the base uri before running the tests.
     */
    public CategoriesTests() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    /**
     * Test: create a valid category.
     * Endpoint: POST /categories
     */
    @Test
    public long testCreateCategory() throws JSONException {

        String categoryName = "School Tasks";

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", categoryName);

        request.body(requestParams.toJSONString());

        long startTime = Calendar.getInstance().getTimeInMillis();
        Response response = request.post("/categories");
        response.then();
        long endTime = Calendar.getInstance().getTimeInMillis();
        String body = response.getBody().asString();
        org.json.JSONObject jsonResponse = new org.json.JSONObject(body);
        Context.getContext().set("categoryId", jsonResponse.getInt("id"), ContextElement.ElementType.CATEGORY);

        response.then()
                .assertThat()
                .statusCode(equalTo((STATUS_CODE_CREATED)))
                .body("title", equalTo(categoryName));

        return endTime - startTime;

    }

    /**
     * Test: modify a valid category.
     * Endpoint: POST /categories
     */
    @Test
    public long testModifyCategory() {

        int categoryId = Context.getContext().get("categoryId");
        String newCategoryName = "House stuff";

        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();

        requestParams.put("title", newCategoryName);

        request.body(requestParams.toJSONString());

        long startTime = Calendar.getInstance().getTimeInMillis();
        Response response = request.post("/categories/" + categoryId);
        long endTime = Calendar.getInstance().getTimeInMillis();

        response.then()
                .assertThat()
                .statusCode(equalTo(STATUS_CODE_SUCCESS))
                .body("title", equalTo(newCategoryName));

        return endTime - startTime;

    }

    /**
     * Test: delete a valid category.
     * Endpoint: POST /categories
     */
    @Test
    public long testDeleteCategory() {

        int categoryId = Context.getContext().get("categoryId");
        RequestSpecification request = RestAssured.given();

        request.get("/categories/" + categoryId).then().assertThat().statusCode(STATUS_CODE_SUCCESS);

        long startTime = Calendar.getInstance().getTimeInMillis();
        request.delete("/categories/" + categoryId).then().assertThat().statusCode(STATUS_CODE_SUCCESS);
        long endTime = Calendar.getInstance().getTimeInMillis();

        request.get("/categories/" + categoryId).then().assertThat().statusCode(STATUS_CODE_NOT_FOUND);

        return endTime - startTime;

    }

}
