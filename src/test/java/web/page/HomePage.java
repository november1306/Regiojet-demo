package web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class HomePage
{
    private final SelenideElement departureDateButton = $(".w-40[data-id='departure-date']");
    private final SelenideElement searchOverlay = $(".searchBox-overlay");
    private final SelenideElement fromCityExpand = $$(".city-select").first();
    private final SelenideElement toCityExpand = $$(".city-select").last();
    private SelenideElement fromCityInput = $$(".react-select__input").first();
    private SelenideElement toCityInput = $$(".react-select__input").last();
    private final SelenideElement firstDropdownOption = $(".react-select__option--is-focused"); // 3h to get this locator (sic!)
    private final SelenideElement searchButton = $("button[data-id='search-btn']");
    private final SelenideElement cookiesAllowAll = $(byText("ALLOW ALL"));

    public void openHomePage() {
        open("https://regiojet.com/");
        if (cookiesAllowAll.exists()) {
            cookiesAllowAll.click();
        }
    }

    public void enterFrom(String city) {
        fromCityExpand.click();
        fromCityInput.sendKeys(city);
        fromCityInput = fromCityInput.shouldHave(Condition.attribute("value", city));
        System.out.println(fromCityInput.getAttribute("value"));
        firstDropdownOption.click();
        System.out.println(fromCityExpand.getText());
    }

    public void enterTo(String city) {
        toCityExpand.click();
        toCityInput.sendKeys(city);
        toCityInput = toCityInput.shouldHave(Condition.attribute("value", city));
        firstDropdownOption.click();
        System.out.println(toCityExpand.getText());
    }

    public DatePicker expandDepartureDatePicker() {
        if (searchOverlay.exists()) {
            searchOverlay.click();
        }
        departureDateButton.click();
        DatePicker datePicker = new DatePicker();
        datePicker.dayPickerContainer.shouldBe(Condition.visible);
        return datePicker;
    }

    public void clickSearch() {
        searchButton.click();
    }

}
