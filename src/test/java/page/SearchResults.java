package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

public class SearchResults {
    private final SelenideElement container = $("ul[aria-label='Search results']");

    public SearchResults() {
        container.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public RouteCard card(int index) {
        return new RouteCard(container.$(".card.pt-2", index));
    }

    public int cardCount() {
        return container.$$(".card.pt-2").size();
    }

    public List<RouteCard> allRecords() {
        return container.$$(".card.pt-2")
                .asDynamicIterable().stream()
                .map(RouteCard::new)
                .collect(Collectors.toList());
    }
}
