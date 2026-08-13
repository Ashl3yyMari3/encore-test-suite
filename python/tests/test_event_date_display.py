# Opens a fixed event's Event Details screen and captures the rendered
# date/time text so it can be compared across devices.
#
# Run via the BrowserStack Python SDK (browserstack-sdk), which reads
# ../browserstack.yml for credentials, the app, and device details, and
# transparently redirects this local Appium session to BrowserStack. Do not
# set capabilities here — they live in browserstack.yml.

from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

WAIT_SECONDS = 20
EVENT_TEST_ID = "event-card-evt-01"  # "Neon Skyline" — fixed, known event


def by_resource_id(resource_id):
    return (AppiumBy.ANDROID_UIAUTOMATOR, f'new UiSelector().resourceId("{resource_id}")')


def test_event_date_display():
    options = UiAutomator2Options()
    driver = webdriver.Remote("http://localhost:4723/wd/hub", options=options)

    try:
        wait = WebDriverWait(driver, WAIT_SECONDS)

        # Use accessibility id (content-desc) — this app's resource-ids have no
        # package prefix so AppiumBy.ID fails; content-desc is stable and
        # confirmed in page source.
        wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "Continue as Guest"))).click()

        # Wait for the event list screen to load, then scroll to evt-01 and click it.
        wait.until(EC.presence_of_element_located(by_resource_id("event-list-screen")))
        wait.until(
            EC.presence_of_element_located(
                (
                    AppiumBy.ANDROID_UIAUTOMATOR,
                    'new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true))'
                    f'.scrollIntoView(new UiSelector().resourceId("{EVENT_TEST_ID}"))',
                )
            )
        ).click()

        date_element = wait.until(EC.presence_of_element_located(by_resource_id("event-details-date")))
        rendered_date = date_element.text

        assert rendered_date, "Expected the event date/time element to render some text"
        print(f"[test_event_date_display] rendered date/time: {rendered_date!r}")
    finally:
        driver.quit()
