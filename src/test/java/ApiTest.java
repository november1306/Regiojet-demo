import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class ApiTest
{
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://brn-ybus-pubapi.sa.cz/";
    }
}
