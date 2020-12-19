import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

abstract class APITestCase {

    private final static String API_ROOT = "https://5fdd444a48321c0017012850.mockapi.io/api"; //temp api
    //private final static String API_ROOT = "[Enter API Root Path]";
    RequestSpecification request;

    @BeforeSuite
    public void beforeSuite(){
        RestAssured.baseURI = API_ROOT;
        request = RestAssured.given();

    }
}
