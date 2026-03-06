package com.insiderone.qa.extensions;

import com.insiderone.qa.driver.DriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotExtension implements AfterTestExecutionCallback {

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // If the test failed, capture the screenshot BEFORE the driver is closed in
        // @AfterEach
        if (context.getExecutionException().isPresent()) {
            System.out.println("ScreenshotExtension: Test failed: " + context.getDisplayName());
            WebDriver driver = DriverManager.getDriver();

            if (driver == null) {
                System.err.println("ScreenshotExtension ERROR: DriverManager.getDriver() returned null");
                return;
            }

            if (!(driver instanceof TakesScreenshot)) {
                System.err.println("ScreenshotExtension ERROR: Driver does not support taking screenshots");
                return;
            }

            try {
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

                // Attach to Allure report
                Allure.addAttachment(
                        "Screenshot on Failure",
                        "image/png",
                        new ByteArrayInputStream(screenshotBytes),
                        "png");

                // Save to file system
                System.out.println("ScreenshotExtension: Saving screenshot to file...");
                saveScreenshotToFile(context, screenshotBytes);
            } catch (Exception e) {
                System.err.println("ScreenshotExtension: Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    private void saveScreenshotToFile(ExtensionContext context, byte[] bytes) {
        String className = context.getTestClass()
                .map(Class::getSimpleName)
                .orElse("Unknown");
        String methodName = context.getTestMethod()
                .map(m -> m.getName())
                .orElse("unknown");
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);

        Path dir = Path.of("target", "screenshots", className);
        try {
            Files.createDirectories(dir);
            Path file = dir.resolve(methodName + "_" + timestamp + ".png");
            Files.write(file, bytes);
            System.out.println("Screenshot saved to: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}
