package com.encore.smoke;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

/**
 * Captures one event's on-screen tap coordinates on the Event List while
 * it's still available, books out every seat on it through the real
 * booking flow, then taps the SAME captured coordinates again (not a
 * fresh element lookup), and asserts the resulting Event Details screen
 * shows the originally expected event.
 *
 * Run via the BrowserStack Java SDK javaagent (see the "sample-test" Maven
 * profile in pom.xml), which reads browserstack.yml for credentials, the
 * app, and device details. Do not set capabilities here.
 */
public class EventListLocatorTest {

  private static final String LOCAL_APPIUM_URL = "http://localhost:4723/wd/hub";
  private static final String EXPECTED_EVENT_TITLE = "Late Night Jazz";
  private static final String EXPECTED_EVENT_ID = "evt-17";
  private static final String[] ALL_SEAT_TEST_IDS = {"seat-A1", "seat-A2", "seat-A3", "seat-A4"};

  // "Late Night Jazz" has the smallest date offset in the catalog, so it
  // renders first (index 0).
  private static final int ORIGINAL_INDEX = 0;
  private static final String CAPTURED_EVENT_LOCATOR = "event-card-" + ORIGINAL_INDEX;

  private static final By SCROLL_TO_TOP = AppiumBy.androidUIAutomator(
      "new UiScrollable(new UiSelector().resourceId(\"event-list\").scrollable(true)).scrollToBeginning(10)");

  private AndroidDriver driver;
  private WebDriverWait wait;

  @BeforeMethod
  public void setUp() throws MalformedURLException {
    UiAutomator2Options options = new UiAutomator2Options();
    driver = new AndroidDriver(new URL(LOCAL_APPIUM_URL), options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(20));
  }

  private static By byResourceId(String resourceId) {
    return AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"" + resourceId + "\")");
  }

  private static By scrollIntoView(String resourceId) {
    return AppiumBy.androidUIAutomator(
        "new UiScrollable(new UiSelector().resourceId(\"event-list\").scrollable(true))"
            + ".scrollIntoView(new UiSelector().resourceId(\"" + resourceId + "\"))");
  }

  private void tapAt(int x, int y) {
    // W3C Actions (pointer/touch) — core WebDriver protocol, not an Appium
    // "mobile:" vendor command, so it works regardless of Appium server
    // version (some older servers don't support "mobile: clickGesture").
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence tap = new Sequence(finger, 0);
    tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
    tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    driver.perform(Collections.singletonList(tap));
  }

  @Test
  public void reusedLocatorOpensWrongEventAfterSellOut() {
    // AppiumBy.id requires full resource-id; this app uses bare ids without package prefix,
    // so use accessibilityId (content-desc) for the landing button and UiAutomator for the rest.
    wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.accessibilityId("Continue as Guest"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("event-list-screen")));

    // Step 1: record the target event's on-screen position while it still
    // has available seats.
    WebElement titleAtCapture = wait.until(
        ExpectedConditions.presenceOfElementLocated(byResourceId("event-card-title-" + EXPECTED_EVENT_ID)));
    String titleTextAtCapture = titleAtCapture.getText();
    Assert.assertEquals(
        titleTextAtCapture,
        EXPECTED_EVENT_TITLE,
        "Expected " + EXPECTED_EVENT_TITLE + " at the captured position — catalog data may have changed");

    WebElement eventCard =
        wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId(CAPTURED_EVENT_LOCATOR)));
    Rectangle rect = eventCard.getRect();
    int capturedX = rect.getX() + rect.getWidth() / 2;
    int capturedY = rect.getY() + rect.getHeight() / 2;
    System.out.println(
        "[EventListLocatorTest] captured \"" + CAPTURED_EVENT_LOCATOR + "\" -> \"" + titleTextAtCapture
            + "\" at (" + capturedX + ", " + capturedY + ")");

    // Step 2: book every seat on the event through the real standard-seat
    // booking flow (this event has zero VIP seats — verified below, not
    // assumed).
    eventCard.click();
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("select-seat-button"))).click();
    for (String seatTestID : ALL_SEAT_TEST_IDS) {
      WebElement seatElement = wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId(seatTestID)));
      String seatLabel = seatElement.getAttribute("content-desc");
      Assert.assertTrue(
          seatLabel != null && seatLabel.contains(", standard,"),
          "Expected " + seatTestID + " to be Standard tier, but its accessibility label was \"" + seatLabel
              + "\" — refusing to book a seat whose tier wasn't verified.");
      seatElement.click();
    }
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("seat-selection-continue-button"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("discount-continue-button"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("payment-pay-button"))).click();

    new WebDriverWait(driver, Duration.ofSeconds(30))
        .until(ExpectedConditions.invisibilityOfElementLocated(byResourceId("payment-processing-indicator")));
    new WebDriverWait(driver, Duration.ofSeconds(30))
        .until(ExpectedConditions.presenceOfElementLocated(byResourceId("confirmation-screen")));
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("confirmation-done-button"))).click();

    // Step 3: confirm the event sorted to the bottom of the list (sold out).
    wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("event-list-screen")));
    wait.until(
        ExpectedConditions.presenceOfElementLocated(scrollIntoView("event-card-soldout-" + EXPECTED_EVENT_ID)));
    System.out.println(
        "[EventListLocatorTest] confirmed \"" + EXPECTED_EVENT_TITLE + "\" sorted to sold-out position");

    // Step 4: scroll back to the top, then tap the ORIGINAL captured
    // coordinates. Assert the resulting Event Details screen shows the
    // originally expected event.
    wait.until(ExpectedConditions.presenceOfElementLocated(SCROLL_TO_TOP));
    tapAt(capturedX, capturedY);
    WebElement detailsTitle =
        wait.until(ExpectedConditions.presenceOfElementLocated(byResourceId("event-details-title")));
    String actualTitle = detailsTitle.getText();

    System.out.println(
        "[EventListLocatorTest] tapped captured coordinates (" + capturedX + ", " + capturedY + ") -> opened \""
            + actualTitle + "\"");
    Assert.assertEquals(
        actualTitle,
        EXPECTED_EVENT_TITLE,
        "Tapping the captured position (" + capturedX + ", " + capturedY + ") was expected to still open \""
            + EXPECTED_EVENT_TITLE + "\", but opened a different event instead.");
  }

  @AfterMethod
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
