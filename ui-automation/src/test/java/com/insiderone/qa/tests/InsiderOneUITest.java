package com.insiderone.qa.tests;

import com.insiderone.qa.config.ConfigReader;
import com.insiderone.qa.driver.DriverFactory;
import com.insiderone.qa.driver.DriverManager;
import com.insiderone.qa.extensions.ScreenshotExtension;
import com.insiderone.qa.pages.HomePage;
import com.insiderone.qa.pages.JobListingsPage;
import com.insiderone.qa.pages.LeverApplicationPage;
import com.insiderone.qa.flows.JobSearchFlow;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

@Epic("InsiderOne UI Tests")
@Feature("Careers & Job Listings")
@ExtendWith(ScreenshotExtension.class)
class InsiderOneUITest {

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
        @Description("Verifies that insiderone.com homepage loads correctly with navigation, hero, content, and footer blocks")
        void homePage_shouldLoadWithAllMainBlocks() {
                HomePage homePage = new HomePage(DriverManager.getDriver());
                homePage.open();

                // TEMPORARY: Sleep to see the session in Grid UI
                try {
                        Thread.sleep(20000);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

                // Wait for page to fully load
                homePage.waitForPageToLoad();

                assertTrue(homePage.isNavbarDisplayed(),
                                "Navigation bar should be visible");
                assertTrue(homePage.isHeroSectionDisplayed(),
                                "Hero/banner section should be visible");
                assertTrue(homePage.isContentSectionDisplayed(),
                                "At least one content section should be visible");

                // Scroll to the bottom to verify footer
                assertTrue(homePage.isFooterDisplayed(),
                                "Footer should be visible");

        }

        @Test
        @Story("Job Listings")
        @Description("Navigates to careers page, clicks 'See all teams', 'Quality Assurance', apply location filter, verifies list")
        void careersPage_filterApplied_shouldShowJobListings() {
                JobSearchFlow searchFlow = new JobSearchFlow(DriverManager.getDriver());
                JobListingsPage listingsPage = searchFlow.navigateToQAJobsWithLocation("Istanbul");

                assertTrue(listingsPage.isListingDisplayed(),
                                "At least one job listing should be displayed after applying filters");
                assertTrue(listingsPage.getJobListingCount() > 0,
                                "Job listing count should be greater than zero");
        }

        @Test
        @Story("Job Listings")
        @Description("Verifies every job listing matches the selected department and location filters")
        void allJobListings_shouldMatchQADepartmentAndIstanbulLocation() {
                JobSearchFlow searchFlow = new JobSearchFlow(DriverManager.getDriver());
                JobListingsPage listingsPage = searchFlow.navigateToQAJobsWithLocation("Istanbul");

                boolean allMatch = listingsPage.verifyJobsMatchCriteria("Quality Assurance", "Istanbul",
                                "Turkey", "qa");
                assertTrue(allMatch,
                                "Not all jobs matched 'Quality Assurance' department and 'Istanbul' or 'Turkey' location criteria.");
        }

        @Test
        @Story("Job Application")
        @Description("Verifies that 'View Role/Apply' button redirects to Lever job application page with form visible")
        void viewRole_shouldOpenLeverApplicationForm() {
                JobSearchFlow searchFlow = new JobSearchFlow(DriverManager.getDriver());
                JobListingsPage listingsPage = searchFlow.navigateToQAJobsWithLocation("Istanbul");

                assertTrue(listingsPage.getJobListingCount() > 0,
                                "There must be at least one listing to click View Role/Apply");

                LeverApplicationPage leverPage = listingsPage.clickViewRoleAt(0);

                assertTrue(leverPage.isOnLeverPage(),
                                "After clicking 'Apply', URL should contain 'lever.co'");
                assertTrue(leverPage.isApplicationFormDisplayed(),
                                "Lever application form or Apply button should be visible");
        }

}
