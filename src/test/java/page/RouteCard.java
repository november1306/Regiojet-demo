package page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class RouteCard
{
    private SelenideElement container = $(".card.pt-2");

    public RouteCard(SelenideElement container) {
        this.container = container;
    }

    public SelenideElement departureArrivalTime() {
        return container.$("h2.h3");
    }

    public SelenideElement transfers() {
        return container.$(".pl-0\\.5");
    }

    public SelenideElement price() {
        return container.$("ul button");
    }

    @Override
    public String toString() {
        return "Departure time: " + departureArrivalTime().getText().split("- ")[0] +
                " Arrival time: " + departureArrivalTime().getText().split("- ")[1] +
                " Number of stops: " + transfers().getText() +
                " Price of journey: " + price().getText();
    }
}