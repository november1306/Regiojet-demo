package web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DatePicker
{
    SelenideElement dayPickerContainer = $(".DayPicker");
    ElementsCollection days = $$("td.CalendarDay");

    public void selectNextMonday() {
        SelenideElement nextMonday = days
                .filterBy(Condition.attributeMatching("aria-label", ".*Monday.*"))
                .filterBy(Condition.attribute("aria-disabled", "false")).first()
                .cached();
        System.out.println(nextMonday.getText());
        nextMonday.click();
    }
}
