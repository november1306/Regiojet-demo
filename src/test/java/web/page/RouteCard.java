package web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$;

public class RouteCard {
    private final SelenideElement container;
    private final SelenideElement departureArrivalTime;
    private final SelenideElement travelTime;
    private final SelenideElement transfers;
    private final SelenideElement price;
    private final SelenideElement fromCity;
    private final SelenideElement toCity;
    private final SelenideElement expandButton;
    private static final Pattern PRICE_PATTERN = Pattern.compile("(?:from\\s+)?([A-Z]{3}|&nbsp;)?\\s*(\\d+(?:[.,]\\d{1,2})?)");

    private boolean expanded;

    public RouteCard(SelenideElement container) {
        this.container = container;
        this.departureArrivalTime = container.$("h2.h3");
        this.travelTime = container.$("h2.h3 + span.text-13");
        this.transfers = container.$(".pl-0\\.5");
        this.price = container.$(".items-start button");
        this.fromCity = container.$("div.cardOpenTransfer-depart-icon + div");
        this.toCity = container.$("div.cardOpenTransfer-arrival-icon + div");
        this.expandButton = $(container.$("svg.transition-transform"));
        this.expanded = expandButton.has(Condition.cssClass("transformed"));
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void expand() {
        if (!isExpanded()) {
            expandButton.click();
            expanded = true;
        }
    }

    public LocalTime getDepartureTime() {
        return LocalTime.parse(departureArrivalTime.getText().split("-")[0].trim(), DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalTime getArrivalTime() {
        return LocalTime.parse(departureArrivalTime.getText().split("-")[1].trim(), DateTimeFormatter.ofPattern("HH:mm"));
    }

    public SelenideElement getTransfers() {
        return transfers;
    }

    public void chooseThisRoute() {
        price.click();
    }

    public BigDecimal parsePrice() {
        String priceText = price.getText();
        Matcher matcher = PRICE_PATTERN.matcher(priceText);
        if (matcher.find()) {
            String priceValue = matcher.group(2).replace(",", ".");
            return new BigDecimal(priceValue).setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new IllegalArgumentException("Invalid price format: " + priceText);
        }
    }

    public SelenideElement getFromCity() {
        return fromCity;
    }

    public SelenideElement getToCity() {
        return toCity;
    }

    @Override
    public String toString() {
        return "Departure time: " + getDepartureTime() +
                " Arrival time: " + getArrivalTime() +
                " Price of journey: " + price.getText();
    }

    public Duration getTravelTime() {
        String travelTimeStr = travelTime.getText();
        int hours = Integer.parseInt(travelTimeStr.substring(0, 2));
        int minutes = Integer.parseInt(travelTimeStr.substring(3, 5));
        return Duration.ofHours(hours).plusMinutes(minutes);
    }
}