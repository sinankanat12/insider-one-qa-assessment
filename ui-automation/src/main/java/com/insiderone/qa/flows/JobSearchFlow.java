package com.insiderone.qa.flows;

import com.insiderone.qa.pages.CareersQAPage;
import com.insiderone.qa.pages.JobListingsPage;
import org.openqa.selenium.WebDriver;

public class JobSearchFlow {

    private final WebDriver driver;

    public JobSearchFlow(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Navigates to the QA Careers page, clicks 'See All Teams', selects 'Quality
     * Assurance',
     * and filters by a specific location.
     *
     * @param location The city/location to filter by (e.g., "Istanbul")
     * @return A JobListingsPage with the results loaded
     */
    public JobListingsPage navigateToQAJobsWithLocation(String location) {
        CareersQAPage careersPage = new CareersQAPage(driver);
        careersPage.open();

        JobListingsPage listingsPage = careersPage.clickSeeAllQAJobs();

        // Apply location filter and wait for listings
        listingsPage.selectLocationFilter(location);
        listingsPage.waitForListingsToLoad();

        return listingsPage;
    }
}
