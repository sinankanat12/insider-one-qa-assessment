package com.insiderone.qa.tests;

import com.insiderone.qa.config.ConfigReader;
import com.insiderone.qa.driver.DriverFactory;
import com.insiderone.qa.driver.DriverManager;
import com.insiderone.qa.extensions.ScreenshotExtension;
import com.insiderone.qa.pages.CareersQAPage;
import com.insiderone.qa.pages.HomePage;
import com.insiderone.qa.pages.JobListingsPage;
import com.insiderone.qa.pages.LeverApplicationPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

@Epic("InsiderOne UI Tests")
@Feature("Careers & Job Listings")
@ExtendWith(ScreenshotExtension.class)
class InsiderOneUITest {

    private static final String LOCATION = "Istanbul, Turkey";
    private static final String DEPARTMENT = "Quality Assurance";

    @BeforeEach
    void setUp() {
        String browser = ConfigReader.getBrowser();
        String gridUrl = ConfigReader.getGridUrl();
        WebDriver driver = DriverFactory.createDriver(browser, gridUrl);
        DriverManager.setDriver(driver);
    }

    @AfterEach
    void tearDown() {
        DriverManager.quitDriver();
    }

    @Test
    @Story("Homepage")
    @DisplayName("Home page should load with all main blocks")
    @Description("Verifies that insiderone.com homepage loads correctly with navigation, hero, content, and footer blocks")
    void homePage_shouldLoadWithAllMainBlocks() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.open();

        assertTrue(homePage.isPageLoaded(),
                "Page title should contain 'Insider' or 'insiderone'");
        assertTrue(homePage.isNavbarDisplayed(),
                "Navigation bar should be visible");
        assertTrue(homePage.isHeroSectionDisplayed(),
                "Hero/banner section should be visible");
        assertTrue(homePage.isContentSectionDisplayed(),
                "At least one content section should be visible");
        assertTrue(homePage.isFooterDisplayed(),
                "Footer should be visible");
    }

    @Test
    @Story("Job Listings")
    @DisplayName("Careers QA page with filters applied should show job listings")
    @Description("Navigates to QA careers page, clicks 'See all QA jobs', applies Location and Department filters, verifies listings appear")
    void careersPage_filterApplied_shouldShowJobListings() {
        JobListingsPage listingsPage = navigateToJobListingsWithFilters();

        assertTrue(listingsPage.isListingDisplayed(),
                "At least one job listing should be displayed after applying filters");
        assertTrue(listingsPage.getJobListingCount() > 0,
                "Job listing count should be greater than zero");
    }

    @Test
    @Story("Job Listings")
    @DisplayName("All QA job listings should match Quality Assurance department and Istanbul location")
    @Description("Verifies every job listing matches the selected department and location filters")
    void allJobListings_shouldMatchQADepartmentAndIstanbulLocation() {
        JobListingsPage listingsPage = navigateToJobListingsWithFilters();

        int count = listingsPage.getJobListingCount();
        assertTrue(count > 0, "Expected at least one job listing, but found none");

        for (int i = 0; i < count; i++) {
            String title = listingsPage.getJobTitleAt(i);
            String dept = listingsPage.getDepartmentAt(i);
            String loc = listingsPage.getLocationAt(i);

            assertTrue(dept.toLowerCase().contains("quality assurance"),
                    String.format("Listing[%d] department '%s' should contain 'Quality Assurance'", i, dept));
            assertTrue(loc.toLowerCase().contains("istanbul"),
                    String.format("Listing[%d] location '%s' should contain 'Istanbul'", i, loc));
        }
    }

    @Test
    @Story("Job Application")
    @DisplayName("Clicking 'View Role' on first listing should open Lever application form")
    @Description("Verifies that 'View Role' button redirects to Lever job application page with form visible")
    void viewRole_shouldOpenLeverApplicationForm() {
        JobListingsPage listingsPage = navigateToJobListingsWithFilters();

        assertTrue(listingsPage.getJobListingCount() > 0,
                "There must be at least one listing to click View Role");

        LeverApplicationPage leverPage = listingsPage.clickViewRoleAt(0);

        assertTrue(leverPage.isOnLeverPage(),
                "After clicking 'View Role', URL should contain 'lever.co'");
        assertTrue(leverPage.isApplicationFormDisplayed(),
                "Lever application form or Apply button should be visible");
    }

    // --- Helper ---

    private JobListingsPage navigateToJobListingsWithFilters() {
        CareersQAPage careersPage = new CareersQAPage(DriverManager.getDriver());
        careersPage.open();

        JobListingsPage listingsPage = careersPage.clickSeeAllQAJobs();
        listingsPage.selectLocationFilter(LOCATION);
        listingsPage.selectDepartmentFilter(DEPARTMENT);
        listingsPage.waitForListingsToLoad();

        return listingsPage;
    }
}
