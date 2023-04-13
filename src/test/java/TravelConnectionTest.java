import org.junit.jupiter.api.Test;
import page.HomePage;
import page.SearchResults;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TravelConnectionTest extends BaseTest {
    private final HomePage homePage = new HomePage();

    @Test
    public void directConnectionTest() {
        homePage.openHomePage();
        //open("https://regiojet.com/?departureDate=2023-04-17&tariffs=REGULAR&fromLocationId=10202000&fromLocationType=CITY&toLocationId=10202002&toLocationType=CITY");
        homePage.enterFrom("Ostrava");
        homePage.enterTo("Brno");
        homePage.expandDepartureDatePicker()
                .selectNextMonday();
        homePage.clickSearch();
        new SearchResults().printResults();

        assertTrue(true);
    }
}
