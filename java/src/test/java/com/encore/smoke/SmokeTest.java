package com.encore.smoke;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Minimal smoke test: launch the app, confirm one screen loads.
 * Intentionally shallow — the goal right now is to prove the BrowserStack
 * App Automate pipeline works end to end, not to test real functionality.
 *
 * Run via the BrowserStack Java SDK javaagent (see the "sample-test" Maven
 * profile in pom.xml), which reads browserstack.yml for credentials, the
 * app, and device details, and transparently redirects this local Appium
 * session to BrowserStack. Do not set capabilities here — they live in
 * browserstack.yml.
 */
public class SmokeTest {

  private static final String LOCAL_APPIUM_URL = "http://localhost:4723/wd/hub";

  private AndroidDriver driver;

  @BeforeMethod
  public void setUp() throws MalformedURLException {
    UiAutomator2Options options = new UiAutomator2Options();
    driver = new AndroidDriver(new URL(LOCAL_APPIUM_URL), options);
  }

  @Test
  public void appLaunchesAndFirstScreenLoads() {
    // Placeholder assertion until real locators exist for the frozen app
    // (e.g. the event list screen). For now, a live session with a
    // current activity is enough to prove the pipeline works.
    Assert.assertNotNull(driver.currentActivity());
  }

  @AfterMethod
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
