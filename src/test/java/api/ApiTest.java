package api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

    private final String nextMonday = getNextMonday();
    static RequestSpecification searchRoutesSpec;


    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = BASE_URI;
        searchRoutesSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath(SEARCH_ROUTS_PATH)
                .addHeader("Content-Type", "application/json")
                .addQueryParam("tariffs", "REGULAR")
                .addQueryParam("fromLocationType", "CITY")
                .addQueryParam("fromLocationId", "10202000")
                .addQueryParam("toLocationType", "CITY")
                .addQueryParam("toLocationId", "10202002")
                .build();

    }

    @Test
    public void direct_connection_search_test() {
        List<Map<String, String>> routesList =
                given()
                        .spec(searchRoutesSpec)
                        .queryParam("departureDate", nextMonday)
                        .when()
                        .get()
                        .then()
                        .log().body()
                        .statusCode(200)
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

    @Test
    public void earliest_arrival_time_test() {
        String earliestArrival =
                given()
                        .spec(searchRoutesSpec)
                        .queryParam("departureDate", nextMonday)
                        .when()
                        .get()
                        .then()
                        .extract()
                        .jsonPath()
                        .getString("routes.arrivalTime.min()");

        System.out.println("The earliest arrival time to Brno is " + earliestArrival);
    }

    @Test
    public void shortest_travel_time_test() {
        List<Map<String, String>> routesList =
                given()
                        .spec(searchRoutesSpec)
                        .queryParam("departureDate", nextMonday)
                        .when()
                        .get()
                        .then()
                        .extract()
                        .jsonPath()
                        .getList("routes.findAll { it.vehicleTypes.contains('TRAIN') }");

        Map<String, String> shortestRoute =
                routesList.stream()
                        .min(Comparator.comparing(route -> {
                            String travelTimeStr = route.get("travelTime");
                            int hours = Integer.parseInt(travelTimeStr.substring(0, 2));
                            int minutes = Integer.parseInt(travelTimeStr.substring(3, 5));
                            return Duration.ofHours(hours).plusMinutes(minutes);
                        }))
                        .orElseThrow(() -> new NoSuchElementException("No train routes found"));

        System.out.println("The shortest travel time to Brno is " + shortestRoute.get("travelTime") + " by route:");
        System.out.println(shortestRoute);
    }

    @Test
    public void lowest_price_test() {

        List<Map<String, Object>> routesList =
                given()
                        .spec(searchRoutesSpec)
                        .queryParam("departureDate", nextMonday)
                        .when()
                        .get()
                        .then()
                        .extract()
                        .jsonPath()
                        .getList("routes.findAll { it.vehicleTypes.contains('TRAIN') }");

        Map<String, Object> cheapestRoute =
                routesList.stream()
                        .min(Comparator.comparing(route -> new BigDecimal(route.get("priceFrom").toString())))
                        .orElseThrow(() -> new NoSuchElementException("No train routes found"));

        System.out.println("The lowest price to Brno is " + cheapestRoute.get("priceFrom") + " by route:");
        System.out.println(cheapestRoute);
    }


    public String getNextMonday() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return nextMonday.format(formatter);
    }

}
