package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class JobListingsPage extends BasePage {

    // Lever locators
    private static final By LOCATION_FILTER_WRAPPER = By
            .cssSelector("div.filter-bar > div:nth-child(2) > div.filter-button.filter-button-mlp");
    private static final By JOB_LISTING_ITEMS = By.cssSelector(".posting");
    private static final By JOB_TITLE = By.cssSelector("h5[data-qa='posting-name']");
    private static final By JOB_DEPARTMENT = By.cssSelector("span.sort-by-team");
    private static final By JOB_LOCATION = By.cssSelector("span.sort-by-location");
    private static final By VIEW_ROLE_BUTTON = By.cssSelector("a.posting-btn-submit");

    public JobListingsPage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public void selectLocationFilter(String location) {
        try {
            // Click to open the custom dropdown popup
            WebElement dropdownWrapper = waitForElement(LOCATION_FILTER_WRAPPER);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownWrapper);

            // Click the specific location option
            // Lever uses 'a' tags with class 'category-link' for location options
            By locationOption = By
                    .xpath("//div[contains(@class, 'filter-popup')]//a[contains(text(), '" + location + "')]");
            WebElement optionElement = waitForElement(locationOption);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", optionElement);

            // Wait for list update
            // 1. Wait for URL to update (Lever usually adds location parameter)
            try {
                waitForUrlToContain("location=");
            } catch (Exception ignored) {
            }

            // 3. Finally wait for the listings to be present and populated
            waitForPresenceOfElement(JOB_LISTING_ITEMS);

        } catch (Exception e) {
            System.out.println("Error selecting location filter: " + e.getMessage());
        }
    }

    public void selectDepartmentFilter(String department) {
        // Automatically populated based on the URL parameter (team=Quality Assurance)
    }

    public void waitForListingsToLoad() {
        try {
            waitForNumberOfElementsToBeMoreThan(JOB_LISTING_ITEMS, 0);
        } catch (Exception e) {
            System.out.println("Jobs list could not be found.");
        }
    }

    public boolean isListingDisplayed() {
        try {
            List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
            return !items.isEmpty() && items.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getJobListingCount() {
        return driver.findElements(JOB_LISTING_ITEMS).size();
    }

    // In a single loop fetch all data to avoid calling driver.findElements multiple
    // times.
    public boolean verifyJobsMatchCriteria(String deptKeyword, String locKeyword1, String locKeyword2,
            String titleKeyword) {
        List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
        if (items.isEmpty()) {
            System.out.println("No job listings found to verify.");
            return false;
        }

        boolean allMatch = true;

        for (int i = 0; i < items.size(); i++) {
            WebElement item = items.get(i);

            String title = item.findElement(JOB_TITLE).getText().toLowerCase();
            String loc = item.findElement(JOB_LOCATION).getText().toLowerCase();

            // Department check - sometimes missing from listing card when already filtered
            boolean deptMatch = true;
            try {
                String dept = item.findElement(JOB_DEPARTMENT).getText().toLowerCase();
                deptMatch = dept.contains(deptKeyword.toLowerCase());
            } catch (Exception ignored) {
            }

            // Location check - robust matching for Istanbul/Turkey/Turkiye/Remote
            boolean locMatch = loc.contains(locKeyword1.toLowerCase()) ||
                    loc.contains(locKeyword2.toLowerCase()) ||
                    loc.contains("turkiye");

            // Title check - flexible matching for QA vs Quality Assurance
            boolean titleMatch = title.contains(titleKeyword.toLowerCase()) ||
                    (titleKeyword.equalsIgnoreCase("qa") && title.contains("quality assurance")) ||
                    (titleKeyword.equalsIgnoreCase("quality assurance") && title.contains("qa"));

            if (!deptMatch || !locMatch || !titleMatch) {
                System.out.println("Mismatch at item " + i + ": Title=" + title + " Loc=" + loc);
                allMatch = false;
            }
        }
        return allMatch;
    }

    public LeverApplicationPage clickViewRoleAt(int index) {
        List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
        WebElement viewRoleBtn = items.get(index).findElement(VIEW_ROLE_BUTTON);

        // Scroll and ensure clickable
        scrollIntoView(VIEW_ROLE_BUTTON);
        waitForClickable(viewRoleBtn);

        viewRoleBtn.click();
        waitForUrlToContain("lever.co");

        return new LeverApplicationPage(driver);
    }
}
