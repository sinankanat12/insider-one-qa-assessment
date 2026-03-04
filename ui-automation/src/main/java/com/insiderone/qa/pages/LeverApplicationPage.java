package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeverApplicationPage extends BasePage {

    private static final By APPLICATION_FORM = By.cssSelector(
            "form.application-form, .application-form, #application-form"
    );
    private static final By APPLY_BUTTON = By.cssSelector(
            "a[data-qa='btn-apply-bottom'], a[href*='apply'], button[type='submit']"
    );

    public LeverApplicationPage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public boolean isOnLeverPage() {
        String currentUrl = driver.getCurrentUrl();
        String title = driver.getTitle();
        return currentUrl.contains("lever.co")
                || (title != null && title.toLowerCase().contains("lever"));
    }

    public boolean isApplicationFormDisplayed() {
        try {
            return isDisplayed(APPLICATION_FORM) || isDisplayed(APPLY_BUTTON);
        } catch (Exception e) {
            return false;
        }
    }
}
