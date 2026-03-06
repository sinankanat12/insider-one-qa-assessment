package com.insiderone.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final int timeout;

    protected BasePage(WebDriver driver, int explicitWaitSeconds) {
        this.driver = driver;
        this.timeout = explicitWaitSeconds;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds));
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected String getText(By locator) {
        return waitForElement(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        return isDisplayed(locator, timeout);
    }

    protected boolean isDisplayed(By locator, int timeoutSeconds) {
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(
                    driver, Duration.ofSeconds(timeoutSeconds));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void scrollIntoView(By locator) {
        WebElement element = waitForElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    protected void waitForUrlToContain(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    protected void waitForPresenceOfElement(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected void waitForNumberOfElementsToBeMoreThan(By locator, int count) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, count));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void acceptCookies() {
        try {
            By cookieBtn = By.cssSelector("#cookie-law-info-bar #wt-cli-accept-all-btn");
            if (driver.findElements(cookieBtn).size() > 0 && driver.findElement(cookieBtn).isDisplayed()) {
                WebElement btn = driver.findElement(cookieBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(cookieBtn));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public void waitForPageToLoad() {
        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                    .equals("complete"));
        } catch (Exception e) {
            System.err.println("Page load wait interrupted: " + e.getMessage());
        }
    }
}
