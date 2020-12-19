import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class BookAPITests extends APITestCase {


    private final static String ENDPOINT_PATH = "/books";

    @Test(description = "Verify that the API starts with an empty store.")
    public void verifyEmptyStoreAtStarts(){
        RestAssured.given().when().get(ENDPOINT_PATH).then().body("$", Matchers.hasSize(0));
    }

    @Test(description = "Verify that title is required field.")
    public void verifyTitleRequired(){
        //JSON Request Body
        JSONObject requestParams = new JSONObject();
        requestParams.put("author", "Lorem Ipsum");

        //Test
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .assertThat()
                .body(equalTo("{\"error\":\"Field 'title' is required\"}"));

    }

    @Test(description = "Verify that author is required field.")
    public void verifyAuthorRequired(){
        //JSON Request Body
        JSONObject requestParams = new JSONObject();
        requestParams.put("title", "Lorem Ipsum");

        //Test
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .assertThat()
                .body(equalTo("{\"error\":\"Field 'author' is required\"}"));

    }

    @Test(description = "Verify that title cannot be empty.")
    public void verifyEmptyTitle(){
        //JSON Request Body
        JSONObject requestParams = new JSONObject();
        requestParams.put("title", "");
        requestParams.put("author","Lorem Ipsum");

        //Test
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .assertThat()
                .body(equalTo("{\"error\":\"Field 'title' cannot be empty.\"}"));

    }


    @Test(description = "Verify that author cannot be empty.")
    public void verifyEmptyAuthor(){
        //JSON Request Body
        JSONObject requestParams = new JSONObject();
        requestParams.put("title", "Lorem Ipsum");
        requestParams.put("author","");

        //Test
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .assertThat()
                .body(equalTo("{\"error\":\"Field 'author' cannot be empty.\"}"));

    }


    @Test(description = "Verify that the id field is read−only.")
    public void verifyIDReadOnly(){
        //JSON Request Body
        JSONObject requestParams = new JSONObject();
        requestParams.put("id","1");
        requestParams.put("title", "Lorem Ipsum");
        requestParams.put("author","Dolor Sit Amet");

        //Test
        request
                //"Http Status Code: 405 Method Not Allowed" returns when a PUT request on a read-only resource.
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);

    }

    @Test(description = "Verify that can create a new book via PUT.")
    public void verifyCreateNewBook(){

    }

    @Test(description = "Verify that cannot create a duplicate book.")
    public void verifyDuplicateBook(){

    }
}
