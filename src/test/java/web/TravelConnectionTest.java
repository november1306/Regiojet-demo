package web;

import com.codeborne.selenide.WebDriverConditions;
import org.junit.jupiter.api.Test;
import web.page.HomePage;
import web.page.RouteCard;
import web.page.SearchResults;

import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.webdriver;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TravelConnectionTest extends BaseTest {
    private final HomePage homePage = new HomePage();

    /**
     * At the same time make sure that all the connections that have been found meet the input criteria
     * (departure time, place of the departure, place of the arrival and whether
     * it is a direct or indirect connection).
     * "departure time" - should be clarified, "departure hours:minutes"? or "departure date"?
     */
    @Test
    public void direct_connection_search_test() {
        homePage.openHomePage();
        //open("https://regiojet.com/?departureDate=2023-04-17&tariffs=REGULAR&fromLocationId=10202000&fromLocationType=CITY&toLocationId=10202002&toLocationType=CITY");
        homePage.enterFrom("Ostrava");
        homePage.enterTo("Brno");
        homePage.expandDepartureDatePicker()
                .selectNextMonday();
        homePage.clickSearch();


        SearchResults searchResults = new SearchResults();
        for (int i = 0; i < searchResults.cardCount(); i++) {
            RouteCard routeCard = searchResults.card(i);
            System.out.println(routeCard);
            routeCard.expand();
            assertAll("city of departure, arrival, is a Direct route",
                    () -> routeCard.getTransfers().shouldHave(text("Direct")),
                    () -> routeCard.getFromCity().shouldHave(text("Ostrava")),
                    () -> routeCard.getToCity().shouldHave(text("Brno"))
            );
        }
    }

    /**
     * Create 3 cases which will choose optimal connection, based on the following criteria:
     * a) the soonest arrival time to Brno starting from midnight
     */
    @Test
    public void select_optimal_connection_based_on_arrival_time() {
        homePage.openHomePage();
        homePage.enterFrom("Ostrava");
        homePage.enterTo("Brno");
        homePage.expandDepartureDatePicker()
                .selectNextMonday();
        homePage.clickSearch();

        List<RouteCard> connections = new SearchResults().allRecords();
        connections.sort(Comparator.comparing(RouteCard::getArrivalTime));
        RouteCard optimalConnection = connections.get(0);
        System.out.println(optimalConnection);
        optimalConnection.chooseThisRoute();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/reservation"));
    }

    /**
     * b) the shortest time spent with travelling – sitting in the train
     */
    @Test
    public void select_optimal_connection_based_on_travel_time() {
        homePage.openHomePage();
        homePage.enterFrom("Ostrava");
        homePage.enterTo("Brno");
        homePage.expandDepartureDatePicker()
                .selectNextMonday();
        homePage.clickSearch();

        List<RouteCard> connections = new SearchResults().allRecords();
        connections.sort(Comparator.comparing(RouteCard::getTravelTime));
        RouteCard optimalConnection = connections.get(0);
        System.out.println(optimalConnection);
        optimalConnection.chooseThisRoute();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/reservation"));
    }

    /**
     * c) the lowest price of the journey
     */
    @Test
    public void select_optimal_connection_based_on_price() {
        homePage.openHomePage();
        homePage.enterFrom("Ostrava");
        homePage.enterTo("Brno");
        homePage.expandDepartureDatePicker()
                .selectNextMonday();
        homePage.clickSearch();

        List<RouteCard> connections = new SearchResults().allRecords();
        connections.sort(Comparator.comparing(RouteCard::parsePrice));
        RouteCard optimalConnection = connections.get(0);
        System.out.println(optimalConnection);
        optimalConnection.chooseThisRoute();
        webdriver().shouldHave(WebDriverConditions.urlContaining("/reservation"));
    }


}
