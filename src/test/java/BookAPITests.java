import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class BookAPITests extends APITestCase {


    private final static String BOOKS_API_ENDPOINT = "/books";

    @Test(priority = 1, description = "Verify that the API starts with an empty store.")
    public void verifyEmptyStoreAtStarts(){
        //Request & Assertion
        request
                .when()
                .get(BOOKS_API_ENDPOINT)
                .then()
                .body("$", Matchers.hasSize(0));
    }

    @Test(description = "Verify that title is required field.")
    public void verifyTitleRequired(){
        //JSON Request Body
        JSONObject requestParams = new JSONObject();
        requestParams.put("author", "Lorem Ipsum");

        //Request & Assertion
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(BOOKS_API_ENDPOINT)
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

        //Request & Assertion
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(BOOKS_API_ENDPOINT)
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

        //Request & Assertion
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(BOOKS_API_ENDPOINT)
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

        //Request & Assertion
        request
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                .post(BOOKS_API_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .assertThat()
                .body(equalTo("{\"error\":\"Field 'author' cannot be empty.\"}"));

    }


    @Test(description = "Verify that the id field is read−only.")
    public void verifyIDReadOnly(){

        //STEP 1: Try create a book via 'ID' field
        //JSON Request Body for create a record
        String putRequestID = "10"; //Read-Only
        String putRequestTitle = "Lorem Ipsum";
        String putRequestAuthor = "Dolor Sit Amet";

        JSONObject putRequestParams = new JSONObject();
        putRequestParams.put("id", putRequestID);
        putRequestParams.put("title", putRequestTitle);
        putRequestParams.put("author",putRequestAuthor);

        Response putResponse =
                request
                .contentType("application/json")
                .body(putRequestParams.toString())
                .when()
                .put(BOOKS_API_ENDPOINT);
                /* Usually, creating objects is preferred with POST. However, if the Rest API is properly developed,
                it may not be an obstacle to use PUT. I could not check it because the mock API I used was not support
                PUT usage this way. Hope it works :) */


                /*If read-only exception was not handled on server side, API returns "500 Internal Server Error"
                However, since it is unknown, all codes except 2xx Success codes was accepted.
                 */
                String statusCode = ((Integer) putResponse.statusCode()).toString();
                Assert.assertFalse(statusCode.startsWith("2"));


    }

    @Test(description = "Verify that can create a new book via PUT.")
    public void verifyCreateNewBook(){

        //STEP 1: Create a book via PUT
        //JSON Request Body for create a record
        String putRequestTitle = "Lorem Ipsum";
        String putRequestAuthor = "Dolor Sit Amet";

        JSONObject putRequestParams = new JSONObject();
        putRequestParams.put("title", putRequestTitle);
        putRequestParams.put("author",putRequestAuthor);

        String putResponseID;
        String putResponseTitle;
        String putResponseAuthor;

        Response putResponse =
                request
                .contentType("application/json")
                .body(putRequestParams.toString())
                .when()
                .put(BOOKS_API_ENDPOINT);

        //Make sure it was created
        Assert.assertEquals(201,putResponse.getStatusCode());

        //Read PUT response
        JSONObject putResponseParams = new JSONObject(putResponse.asString());
        putResponseID = putResponseParams.get("id").toString();
        putResponseTitle = putResponseParams.get("title").toString();
        putResponseAuthor = putResponseParams.get("author").toString();

        //Assert response values
        Assert.assertEquals(putResponseTitle, putRequestTitle);
        Assert.assertEquals(putResponseAuthor, putRequestAuthor);

        //STEP 2: GET the created book

        String getResponseID;
        String getResponseTitle;
        String getResponseAuthor;

        Response getResponse =
                request
                .contentType("application/json")
                .when()
                .get(BOOKS_API_ENDPOINT + "/" + putResponseID);

        //Make sure it was received
        Assert.assertEquals(200,getResponse.getStatusCode());

        //Read GET response
        JSONObject getResponseParams = new JSONObject(getResponse.asString());
        getResponseID = getResponseParams.get("id").toString();
        getResponseTitle = getResponseParams.get("title").toString();
        getResponseAuthor = getResponseParams.get("author").toString();

        //Assertions
        Assert.assertEquals(putResponseID, getResponseID);
        Assert.assertEquals(putResponseAuthor, getResponseAuthor);
        Assert.assertEquals(putResponseTitle, getResponseTitle);
    }

    @Test(description = "Verify that cannot create a duplicate book.")
    public void verifyDuplicateBook(){
        //STEP 1: Create a book via PUT
        //JSON Request Body for create a record
        String putRequestTitle = "Lorem Ipsum";
        String putRequestAuthor = "Dolor Sit Amet";

        JSONObject putRequestParams = new JSONObject();
        putRequestParams.put("title", putRequestTitle);
        putRequestParams.put("author",putRequestAuthor);

        request
                .contentType("application/json")
                .body(putRequestParams.toString())
                .when()
                .put(BOOKS_API_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        //STEP 2: Try to send the same payload again
        request
                .contentType("application/json")
                .body(putRequestParams.toString())
                .when()
                .put(BOOKS_API_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .assertThat()
                .body(equalTo("{\"error\":\"Another book with similar\n" +
                        "title and author already exists.\"}"));
    }
}
