import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.testng.Assert;

public class App {
    public static void main(String[] args) {

        JSONObject requestParams = new JSONObject();
        requestParams.put("title", "Lorem Ipsum");
        requestParams.put("author","Dolor Sit Amet");

        //Test
        //STEP 1: Create a book via PUT, the book is going to create with id = 1 because API starts with an empty store.
        Response response = RestAssured.given()

                .contentType("application/json")
                //.body(requestParams.toString())
                .when()
                .get("https://5fdd444a48321c0017012850.mockapi.io/api/books/1");

        String statusCode = ((Integer) response.statusCode()).toString();
        Assert.assertFalse(statusCode.startsWith("3"));

        JSONObject responseParams = new JSONObject(response.asString());

        System.out.println(responseParams.get("id"));
        System.out.println(responseParams.get("title"));
        System.out.println(responseParams.get("author"));



    }
}
