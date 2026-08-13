package com.encore.smoke;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Opens a fixed event's Event Details screen and captures the rendered
 * date/time text so it can be compared across devices.
 *
 * Run via the BrowserStack Java SDK javaagent (see the "sample-test" Maven
 * profile in pom.xml), which reads browserstack.yml for credentials, the
 * app, and device details. Do not set capabilities here.
 */
public class EventDateDisplayTest {

  private static final String LOCAL_APPIUM_URL = "http://localhost:4723/wd/hub";
  private static final String EVENT_TEST_ID = "event-card-evt-01"; // "Neon Skyline" — fixed, known event

  private AndroidDriver driver;
  private WebDriverWait wait;

  @BeforeMethod
  public void setUp() throws MalformedURLException {
    UiAutomator2Options options = new UiAutomator2Options();
    driver = new AndroidDriver(new URL(LOCAL_APPIUM_URL), options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
  }

  @Test
  public void capturesRenderedEventDateTime() {
    // Use accessibility id (content-desc) — the resource-ids on this app have no package prefix
    // so AppiumBy.id fails; content-desc is stable and confirmed in page source.
    wait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.accessibilityId("Continue as Guest"))).click();
    // Wait for the event list screen to load, then scroll to evt-01 and click it.
    wait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"event-list-screen\")")));
    wait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().resourceId(\"event-list\").scrollable(true))" +
            ".scrollIntoView(new UiSelector().resourceId(\"" + EVENT_TEST_ID + "\"))"
        )
    )).click();

    WebElement dateElement =
        wait.until(ExpectedConditions.presenceOfElementLocated(
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"event-details-date\")")));
    String renderedDate = dateElement.getText();

    Assert.assertFalse(
        renderedDate == null || renderedDate.isEmpty(),
        "Expected the event date/time element to render some text");
    System.out.println("[EventDateDisplayTest] rendered date/time: \"" + renderedDate + "\"");
  }

  @AfterMethod
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
