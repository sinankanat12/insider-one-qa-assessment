# Insider One UI Automation Framework

This project contains a robust, scalable, and maintainable end-to-end UI automation framework for the Insider One platform. It is built using **Java**, **Selenium WebDriver**, and **JUnit 5**, strictly following the **Page Object Model (POM)** design pattern.

## 🚀 Key Features

- **Page Object Model (POM):** Clean separation between page elements, actions, and test logic.
- **Flow-based Abstraction:** Complex multi-page user journeys are encapsulated in "Flow" classes (e.g., `JobSearchFlow`).
- **BasePage Implementation:** Centralized wait strategies, scrolling, and common element interactions to ensure DRY (Don't Repeat Yourself) code.
- **Automated Screenshots:** Leverages a custom JUnit 5 extension (`ScreenshotExtension`) to automatically capture and save screenshots on test failure.
- **Reporting:** Integrated with **Allure Report** for detailed execution insights.
- **Configuration Management:** Centralized configuration via `config.properties`.
- **Driver Management:** Uses `ThreadLocal` for safe driver handling and future parallel execution support.

## 🛠 Tech Stack

- **Language:** Java 17
- **Test Framework:** JUnit 5
- **Automation Tool:** Selenium WebDriver
- **Build Tool:** Maven
- **Reporting:** Allure
- **Design Pattern:** POM & Flow-based Navigation

## 📋 Prerequisites

- **Java JDK 17+**
- **Maven** installed and in your PATH.
- **Chrome browser** installed (the framework defaults to Chrome).
- **Allure Commandline** (optional, for viewing reports).

## 🏃 Running Tests

### Run all tests
```bash
mvn clean test
```

### Run specific test class
```bash
mvn test -Dtest=InsiderOneUITest
```

### Run with a specific browser
```bash
mvn test -Dbrowser=firefox
```

## 📊 Reporting

### Generating Allure Reports
After running the tests, Allure results are generated in `target/allure-results`. To view the report:

```bash
allure serve target/allure-results
```

### Screenshots on Failure
If a test fails, a screenshot is automatically captured and saved in:
`ui-automation/target/screenshots/{ClassName}/{MethodName}_{Timestamp}.png`

Screenshots are also automatically attached to the Allure report.

## 📁 Project Structure

```text
ui-automation/
├── src/main/java/com/insiderone/qa/
│   ├── config/      # Configuration reader and properties
│   ├── driver/      # WebDriver factory and manager
│   ├── flows/       # Business flows (multi-page actions)
│   └── pages/       # Page Object classes
├── src/test/java/com/insiderone/qa/
│   ├── extensions/  # JUnit 5 extensions (e.g., Screenshots)
│   └── tests/       # UI Test classes
├── src/main/resources/
│   └── config.properties  # App configuration
├── pom.xml          # Project dependencies
└── README.md
```

## 🧪 Demonstration
The project includes an **`InsiderFailingUITest`** class which deliberately fails to demonstrate the automatic screenshot capture and reporting mechanism.
