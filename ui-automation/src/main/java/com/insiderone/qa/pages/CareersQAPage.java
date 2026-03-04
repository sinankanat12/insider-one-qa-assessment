package com.insiderone.qa.pages;

import com.insiderone.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CareersQAPage extends BasePage {

    private static final String QA_PAGE_URL = ConfigReader.getBaseUrl() + "/careers/quality-assurance/";

    // "See all QA jobs" anchor link
    private static final By SEE_ALL_QA_JOBS_LINK = By.linkText("See all QA jobs");

    public CareersQAPage(WebDriver driver) {
        super(driver, ConfigReader.getExplicitWaitSeconds());
    }

    public void open() {
        driver.get(QA_PAGE_URL);
    }

    public JobListingsPage clickSeeAllQAJobs() {
        scrollIntoView(SEE_ALL_QA_JOBS_LINK);
        click(SEE_ALL_QA_JOBS_LINK);
        return new JobListingsPage(driver);
    }
}
