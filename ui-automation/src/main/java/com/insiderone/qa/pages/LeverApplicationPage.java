package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeverApplicationPage extends BasePage {

    private static final By APPLICATION_FORM = By.cssSelector(
            "form.application-form, .application-form, #application-form");
    private static final By APPLY_BUTTON = By.cssSelector(
            "a[data-qa='btn-apply-bottom'], a[href*='apply'], button[type='submit']");

    public LeverApplicationPage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public boolean isOnLeverPage() {
        return driver.getCurrentUrl().contains("lever.co");
    }

    public boolean isApplicationFormDisplayed() {
        // Lever pages can be slow, but we don't want to wait 15+ seconds if we just
        // want a quick check
        // Check for either the form or the apply button with a short 3-second timeout
        return isDisplayed(APPLICATION_FORM, 3) || isDisplayed(APPLY_BUTTON, 3);
    }
}
