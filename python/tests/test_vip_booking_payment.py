# Books a VIP-tier seat through checkout and confirms the Confirmation
# screen appears within a bounded wait after tapping "Pay Now".
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

NAV_WAIT_SECONDS = 20
PAYMENT_WAIT_SECONDS = 30
EVENT_TEST_ID = "event-card-evt-01"  # "Neon Skyline" — has a VIP row (A)
VIP_SEAT_TEST_ID = "seat-A1"  # row A is VIP tier for this event's venue


def by_resource_id(resource_id):
    return (AppiumBy.ANDROID_UIAUTOMATOR, f'new UiSelector().resourceId("{resource_id}")')


def test_vip_booking_payment():
    options = UiAutomator2Options()
    driver = webdriver.Remote("http://localhost:4723/wd/hub", options=options)

    try:
        nav_wait = WebDriverWait(driver, NAV_WAIT_SECONDS)

        # Use accessibility id (content-desc) — this app's resource-ids have no
        # package prefix so AppiumBy.ID fails; content-desc is stable and
        # confirmed in page source.
        nav_wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "Continue as Guest"))).click()

        # Wait for event list, scroll to evt-01, click it.
        nav_wait.until(EC.presence_of_element_located(by_resource_id("event-list-screen")))
        nav_wait.until(
            EC.presence_of_element_located(
                (
                    AppiumBy.ANDROID_UIAUTOMATOR,
                    'new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true))'
                    f'.scrollIntoView(new UiSelector().resourceId("{EVENT_TEST_ID}"))',
                )
            )
        ).click()

        nav_wait.until(EC.presence_of_element_located(by_resource_id("select-seat-button"))).click()
        nav_wait.until(EC.presence_of_element_located(by_resource_id(VIP_SEAT_TEST_ID))).click()
        nav_wait.until(EC.presence_of_element_located(by_resource_id("seat-selection-continue-button"))).click()
        nav_wait.until(EC.presence_of_element_located(by_resource_id("discount-continue-button"))).click()
        nav_wait.until(EC.presence_of_element_located(by_resource_id("payment-pay-button"))).click()

        # Wait for the processing spinner to disappear first (payment completed or failed),
        # then assert the confirmation screen appears.
        WebDriverWait(driver, PAYMENT_WAIT_SECONDS).until(
            EC.invisibility_of_element_located(by_resource_id("payment-processing-indicator"))
        )
        WebDriverWait(driver, PAYMENT_WAIT_SECONDS).until(
            EC.presence_of_element_located(by_resource_id("confirmation-screen"))
        )
        print("[test_vip_booking_payment] confirmation screen appeared")
    finally:
        driver.quit()
