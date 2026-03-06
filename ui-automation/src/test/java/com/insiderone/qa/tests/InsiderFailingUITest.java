package com.insiderone.qa.tests;

import com.insiderone.qa.config.ConfigReader;
import com.insiderone.qa.driver.DriverFactory;
import com.insiderone.qa.driver.DriverManager;
import com.insiderone.qa.extensions.ScreenshotExtension;
import com.insiderone.qa.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Failing Tests Demo")
@Feature("Screenshot Mechanism")
@ExtendWith(ScreenshotExtension.class)
class InsiderFailingUITest {

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
    @Story("Failing Scenario")
    @Description("This test is designed to fail to demonstrate the automatic screenshot capture feature.")
    void failingTest_shouldCaptureScreenshot() {
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.open();

        // intentional failure: checking for a title that doesn't exist
        String actualTitle = DriverManager.getDriver().getTitle();
        assertEquals("Wrong Title To Trigger Failure", actualTitle,
                "Title should not match, triggering a failure for screenshot.");
    }
}
