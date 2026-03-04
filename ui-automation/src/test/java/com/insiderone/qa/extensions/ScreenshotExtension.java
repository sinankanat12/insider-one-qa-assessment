package com.insiderone.qa.extensions;

import com.insiderone.qa.driver.DriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ScreenshotExtension implements TestWatcher {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        WebDriver driver = DriverManager.getDriver();
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }

        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Attach to Allure report
        Allure.addAttachment(
                "Screenshot on Failure",
                "image/png",
                new ByteArrayInputStream(screenshotBytes),
                "png"
        );

        // Save to file system
        saveScreenshotToFile(context, screenshotBytes);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        // No-op: screenshots only on failure
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        // No-op
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // No-op
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
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}
