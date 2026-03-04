package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    // Locators
    private static final By NAVBAR = By.cssSelector("#navigation");
    private static final By HERO_SECTION = By.cssSelector("#main-head");
    private static final By FOOTER = By.cssSelector("section#footer, footer#footer, #footer");
    private static final By CONTENT_SECTION = By.cssSelector(".container .row, .tab-container, .swiper-slide");

    public HomePage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public void open() {
        driver.get(ConfigReader.getBaseUrl());
    }

    public boolean isPageLoaded() {
        String title = driver.getTitle();
        return title != null
                && !title.isBlank()
                && (title.toLowerCase().contains("insider") || title.toLowerCase().contains("insiderone"));
    }

    public boolean isNavbarDisplayed() {
        return isDisplayed(NAVBAR);
    }

    public boolean isHeroSectionDisplayed() {
        return isDisplayed(HERO_SECTION);
    }

    public boolean isFooterDisplayed() {
        scrollIntoView(FOOTER);
        return isDisplayed(FOOTER);
    }

    public boolean isContentSectionDisplayed() {
        return isDisplayed(CONTENT_SECTION);
    }
}
