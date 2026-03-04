package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class JobListingsPage extends BasePage {

    // Filter dropdowns (Select2-based custom dropdowns)
    private static final By LOCATION_FILTER_TRIGGER = By.cssSelector("#select2-filter-by-location-container");
    private static final By DEPARTMENT_FILTER_TRIGGER = By.cssSelector("#select2-filter-by-department-container");
    private static final By SELECT2_DROPDOWN_SEARCH = By.cssSelector(".select2-search__field");
    private static final By SELECT2_RESULTS = By.cssSelector(".select2-results__option");

    // Job listings
    private static final By JOB_LIST_CONTAINER = By.cssSelector("#jobs-list, .position-list");
    private static final By JOB_LISTING_ITEMS = By.cssSelector(".position-list-item");
    private static final By JOB_TITLE = By.cssSelector("p.position-title");
    private static final By JOB_DEPARTMENT = By.cssSelector("span.position-department");
    private static final By JOB_LOCATION = By.cssSelector("div.position-location");
    private static final By VIEW_ROLE_BUTTON = By.cssSelector("a.btn[href*='lever.co']");

    public JobListingsPage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public void selectLocationFilter(String location) {
        // Click on the Select2 location dropdown trigger
        click(LOCATION_FILTER_TRIGGER);
        // Wait for dropdown to open and type in search box
        WebElement searchBox = waitForClickable(SELECT2_DROPDOWN_SEARCH);
        searchBox.sendKeys(location);
        // Wait for results and click the matching option
        By locationOption = By.xpath(
                "//li[contains(@class,'select2-results__option') and contains(normalize-space(text()),'" + location + "')]"
        );
        waitForClickable(locationOption).click();
    }

    public void selectDepartmentFilter(String department) {
        // Click on the Select2 department dropdown trigger
        click(DEPARTMENT_FILTER_TRIGGER);
        // Wait for dropdown to open and type in search box
        WebElement searchBox = waitForClickable(SELECT2_DROPDOWN_SEARCH);
        searchBox.sendKeys(department);
        // Wait for results and click the matching option
        By departmentOption = By.xpath(
                "//li[contains(@class,'select2-results__option') and contains(normalize-space(text()),'" + department + "')]"
        );
        waitForClickable(departmentOption).click();
    }

    public void waitForListingsToLoad() {
        // Wait for at least one listing item to be visible
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(JOB_LISTING_ITEMS, 0));
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

    public String getJobTitleAt(int index) {
        List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
        return items.get(index).findElement(JOB_TITLE).getText();
    }

    public String getDepartmentAt(int index) {
        List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
        return items.get(index).findElement(JOB_DEPARTMENT).getText();
    }

    public String getLocationAt(int index) {
        List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
        return items.get(index).findElement(JOB_LOCATION).getText();
    }

    public LeverApplicationPage clickViewRoleAt(int index) {
        List<WebElement> items = driver.findElements(JOB_LISTING_ITEMS);
        String originalWindow = driver.getWindowHandle();

        WebElement viewRoleBtn = items.get(index).findElement(VIEW_ROLE_BUTTON);
        scrollIntoView(By.cssSelector(
                ".position-list-item:nth-child(" + (index + 1) + ") a.btn[href*='lever.co']"
        ));
        viewRoleBtn.click();

        // Handle new tab if opened
        wait.until(d -> d.getWindowHandles().size() > 1);
        String newWindow = driver.getWindowHandles().stream()
                .filter(h -> !h.equals(originalWindow))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No new window opened after clicking View Role"));

        driver.switchTo().window(newWindow);
        return new LeverApplicationPage(driver);
    }

    public List<WebElement> getAllJobListings() {
        return driver.findElements(JOB_LISTING_ITEMS);
    }
}
