package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    // Locators
    private static final By NAVBAR = By.cssSelector("nav, [id='navigation']");
    private static final By HERO_SECTION = By.cssSelector("main h1");
    private static final By FOOTER = By.cssSelector(".footer-main");
    private static final By CONTENT_SECTION = By
            .cssSelector(".homepage-core-differentiators-wrapper, .homepage-capabilities-main");

    public HomePage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public void open() {
        driver.get(ConfigReader.getBaseUrl());
        acceptCookies();
    }

    public boolean isPageLoaded() {
        waitForPageToLoad();
        try {
            wait.until(d -> d.getTitle() != null && !d.getTitle().isBlank());
        } catch (Exception e) {
            // ignore
        }
        String title = driver.getTitle();
        return title != null && (title.toLowerCase().contains("insider") || title.toLowerCase().contains("insiderone"));
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
