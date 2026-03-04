package com.insiderone.qa.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverFactory {

    private DriverFactory() {}

    public static WebDriver createDriver(String browser, String gridUrl) {
        WebDriver driver;

        if (gridUrl != null && !gridUrl.isBlank()) {
            driver = createRemoteDriver(browser, gridUrl);
        } else {
            driver = createLocalDriver(browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createRemoteDriver(String browser, String gridUrl) {
        try {
            if ("firefox".equalsIgnoreCase(browser)) {
                FirefoxOptions options = buildFirefoxOptions();
                return new RemoteWebDriver(new URL(gridUrl), options);
            } else {
                ChromeOptions options = buildChromeOptions();
                return new RemoteWebDriver(new URL(gridUrl), options);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Grid URL: " + gridUrl, e);
        }
    }

    private static WebDriver createLocalDriver(String browser) {
        if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver(buildFirefoxOptions());
        } else {
            WebDriverManager.chromedriver().setup();
            return new ChromeDriver(buildChromeOptions());
        }
    }

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        if (isCI()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
        }
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-width=1920");
        options.addArguments("-height=1080");

        if (isCI()) {
            options.addArguments("--headless");
        }
        return options;
    }

    private static boolean isCI() {
        String ci = System.getenv("CI");
        return "true".equalsIgnoreCase(ci);
    }
}
