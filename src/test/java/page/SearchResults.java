package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

public class SearchResults {
    private SelenideElement container = $("ul[aria-label='Search results']");

    public SearchResults() {
        container.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public RouteCard record(int index) {
        return new RouteCard(container.$(".card.pt-2", index));
    }

//    public List<RouteCard> getAllResults()
//    {
//        return container.$$(".card.pt-2")
//                .stream()
//
//                .map(RouteCard::new)
//                .collect(Collectors.toList());
//
//    }

    public void printResults() {
        List<RouteCard> cards = container.$$(".card.pt-2")
                .asDynamicIterable().stream()
                .map(RouteCard::new)
                .collect(Collectors.toList());

        for (RouteCard card : cards) {
            System.out.println(card.toString());
        }

    }

}
