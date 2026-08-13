package com.encore.smoke;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Books a VIP-tier seat through checkout and confirms the Confirmation
 * screen appears within a bounded wait after tapping "Pay Now".
 *
 * Run via the BrowserStack Java SDK javaagent (see the "sample-test" Maven
 * profile in pom.xml), which reads browserstack.yml for credentials, the
 * app, and device details. Do not set capabilities here.
 */
public class VipBookingPaymentTest {

  private static final String LOCAL_APPIUM_URL = "http://localhost:4723/wd/hub";
  private static final String EVENT_TEST_ID = "event-card-evt-01"; // "Neon Skyline" — has a VIP row (A)
  private static final String VIP_SEAT_TEST_ID = "seat-A1"; // row A is VIP tier for this event's venue

  private AndroidDriver driver;
  private WebDriverWait navWait;

  @BeforeMethod
  public void setUp() throws MalformedURLException {
    UiAutomator2Options options = new UiAutomator2Options();
    driver = new AndroidDriver(new URL(LOCAL_APPIUM_URL), options);
    navWait = new WebDriverWait(driver, Duration.ofSeconds(15));
  }

  @Test
  public void reachesConfirmationAfterPayingForVipSeat() {
    // AppiumBy.id requires full resource-id; this app uses bare ids without package prefix,
    // so use accessibilityId (content-desc) for the landing button and UiAutomator for the rest.
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.accessibilityId("Continue as Guest"))).click();
    // Wait for event list, scroll to evt-01, click it
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"event-list-screen\")")));
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().resourceId(\"event-list\").scrollable(true))" +
            ".scrollIntoView(new UiSelector().resourceId(\"" + EVENT_TEST_ID + "\"))"
        )
    )).click();
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"select-seat-button\")"))).click();
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"" + VIP_SEAT_TEST_ID + "\")"))).click();
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"seat-selection-continue-button\")"))).click();
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"discount-continue-button\")"))).click();
    navWait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"payment-pay-button\")"))).click();

    // Wait for the processing spinner to disappear first (payment completed or failed),
    // then assert the confirmation screen appears.
    new WebDriverWait(driver, Duration.ofSeconds(30))
        .until(ExpectedConditions.invisibilityOfElementLocated(
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"payment-processing-indicator\")")));
    new WebDriverWait(driver, Duration.ofSeconds(30))
        .until(ExpectedConditions.presenceOfElementLocated(
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"confirmation-screen\")")));
  }

  @AfterMethod
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
