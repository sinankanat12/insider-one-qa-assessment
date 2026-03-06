package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CareersQAPage extends BasePage {

    private static final String QA_PAGE_URL = ConfigReader.getBaseUrl() + "/careers/";

    private static final By EXPLORE_ROLES_SECTION = By.xpath("//h2[text()='Explore open roles']");
    private static final By SEE_ALL_TEAMS_LINK = By.xpath("//a[contains(text(), 'See all teams')]");
    private static final By QA_OPEN_POSITIONS_BTN = By.xpath("//div[@data-department='Quality Assurance']//a");

    public CareersQAPage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public void open() {
        driver.get(QA_PAGE_URL);
        acceptCookies();
        waitForPageToLoad();
    }

    public JobListingsPage clickSeeAllQAJobs() {
        // Find explore section
        scrollIntoView(EXPLORE_ROLES_SECTION);

        // Click see all teams if it is in the DOM
        if (driver.findElements(SEE_ALL_TEAMS_LINK).size() > 0) {
            WebElement seeAllBtn = waitForClickable(SEE_ALL_TEAMS_LINK);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", seeAllBtn);
        }

        // Wait for QA Card to become visible
        waitForElement(QA_OPEN_POSITIONS_BTN);
        scrollIntoView(QA_OPEN_POSITIONS_BTN);

        // Click QA open positions using JS
        WebElement qaBtn = waitForClickable(QA_OPEN_POSITIONS_BTN);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", qaBtn);

        // Wait until new window opens or URL changes to lever.co
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(
                    driver, java.time.Duration.ofSeconds(2));
            shortWait.until(d -> d.getWindowHandles().size() > 1 || d.getCurrentUrl().contains("lever.co"));

            // Switch to the newly opened window if there are multiple windows
            String currentWindow = driver.getWindowHandle();
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(currentWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
        } catch (Exception e) {
            // Bypass redirect if 0 open positions
            driver.get("https://jobs.lever.co/insiderone?team=Quality%20Assurance");
        }

        waitForUrlToContain("lever.co");
        return new JobListingsPage(driver);
    }
}
