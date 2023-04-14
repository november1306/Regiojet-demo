package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API interface
 * https://app.swaggerhub.com/apis/regiojet/affiliate/1.1.0#/Consts/getLocations
 */

public class ApiTest {
    private static final String BASE_URI = "https://brn-ybus-pubapi.sa.cz/restapi";
    private static final String SEARCH_ROUTS_PATH = "/routes/search/simple";
    private static final String ROUT_DETAIL_PATH = "/routes/{routeId}/simple";


    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    public void direct_connection_search_test() {
        String nextMonday = getNextMonday();
        List<Map<String, String>> routesList =
                given()
                        .basePath(SEARCH_ROUTS_PATH)
                        .queryParam("departureDate", nextMonday)
                        .queryParam("tariffs", "REGULAR")
                        .queryParam("fromLocationType", "CITY")
                        .queryParam("fromLocationId", "10202000")
                        .queryParam("toLocationType", "CITY")
                        .queryParam("toLocationId", "10202002")
                        .header("Content-Type", "application/json")
                        .when()
                        .get()
                        .then()
                        .log().body()
                        .extract()
                        .jsonPath().getList("routes");

        routesList
                .parallelStream()
                .forEach(route ->
                        given()
                                .pathParam("routeId", route.get("id"))
                                .queryParam("fromStationId", route.get("departureStationId"))
                                .queryParam("toStationId", route.get("arrivalStationId"))
                                .queryParam("tariffs", "REGULAR")
                                .when()
                                .get(ROUT_DETAIL_PATH)
                                .then()
                                .assertThat()
                                .body("departureCityName", equalTo("Ostrava"))
                                .body("arrivalCityName", equalTo("Brno"))
                                .body("departureTime", containsString(nextMonday))
                                .body("transferInfo", nullValue()));
    }

    public String getNextMonday() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return nextMonday.format(formatter);
    }

}
