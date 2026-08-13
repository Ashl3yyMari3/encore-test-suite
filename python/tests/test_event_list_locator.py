# Captures one event's on-screen tap coordinates on the Event List while
# it's still available, books out every seat on it through the real
# booking flow, then taps the SAME captured coordinates again (not a fresh
# element lookup), and asserts the resulting Event Details screen shows
# the originally expected event.
#
# Run via the BrowserStack Python SDK (browserstack-sdk), which reads
# ../browserstack.yml for credentials, the app, and device details, and
# transparently redirects this local Appium session to BrowserStack. Do not
# set capabilities here — they live in browserstack.yml.

from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.common.actions import interaction
from selenium.webdriver.common.actions.action_builder import ActionBuilder
from selenium.webdriver.common.actions.pointer_input import PointerInput
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

NAV_WAIT_SECONDS = 20
PAYMENT_WAIT_SECONDS = 30
EXPECTED_EVENT_TITLE = "Late Night Jazz"
EXPECTED_EVENT_ID = "evt-17"
ALL_SEAT_IDS = ["seat-A1", "seat-A2", "seat-A3", "seat-A4"]

# "Late Night Jazz" has the smallest date offset in the catalog, so it
# renders first (index 0).
ORIGINAL_INDEX = 0
CAPTURED_EVENT_LOCATOR = f"event-card-{ORIGINAL_INDEX}"


def by_resource_id(resource_id):
    return (AppiumBy.ANDROID_UIAUTOMATOR, f'new UiSelector().resourceId("{resource_id}")')


def scroll_into_view(resource_id):
    return (
        AppiumBy.ANDROID_UIAUTOMATOR,
        'new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true))'
        f'.scrollIntoView(new UiSelector().resourceId("{resource_id}"))',
    )


SCROLL_TO_TOP = (
    AppiumBy.ANDROID_UIAUTOMATOR,
    'new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true)).scrollToBeginning(10)',
)


def tap(driver, x, y):
    # W3C Actions (pointer/touch) — core WebDriver protocol, not an Appium
    # "mobile:" vendor command, so it works regardless of Appium server
    # version (some older servers don't support "mobile: clickGesture").
    actions = ActionBuilder(driver, mouse=PointerInput(interaction.POINTER_TOUCH, "touch"))
    actions.pointer_action.move_to_location(x, y)
    actions.pointer_action.pointer_down()
    actions.pointer_action.pointer_up()
    actions.perform()


def test_event_list_locator():
    options = UiAutomator2Options()
    driver = webdriver.Remote("http://localhost:4723/wd/hub", options=options)

    try:
        wait = WebDriverWait(driver, NAV_WAIT_SECONDS)

        # Use accessibility id (content-desc) — this app's resource-ids have no
        # package prefix so AppiumBy.ID fails; content-desc is stable and
        # confirmed in page source.
        wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "Continue as Guest"))).click()
        wait.until(EC.presence_of_element_located(by_resource_id("event-list-screen")))

        # Step 1: record the target event's on-screen position while it
        # still has available seats.
        title_at_capture = wait.until(
            EC.presence_of_element_located(by_resource_id(f"event-card-title-{EXPECTED_EVENT_ID}"))
        ).text
        assert title_at_capture == EXPECTED_EVENT_TITLE, (
            f"Expected {EXPECTED_EVENT_TITLE!r} at the captured position, found {title_at_capture!r} — "
            "catalog data may have changed; this script's assumptions need updating."
        )
        event_card = wait.until(EC.presence_of_element_located(by_resource_id(CAPTURED_EVENT_LOCATOR)))
        rect = event_card.rect
        captured_x = round(rect["x"] + rect["width"] / 2)
        captured_y = round(rect["y"] + rect["height"] / 2)
        print(
            f"[test_event_list_locator] captured {CAPTURED_EVENT_LOCATOR!r} -> {title_at_capture!r} "
            f"at ({captured_x}, {captured_y})"
        )

        # Step 2: book every seat on the event through the real standard-seat
        # booking flow (this event has zero VIP seats — verified below, not
        # assumed).
        event_card.click()
        wait.until(EC.presence_of_element_located(by_resource_id("select-seat-button"))).click()
        for seat_id in ALL_SEAT_IDS:
            seat_element = wait.until(EC.presence_of_element_located(by_resource_id(seat_id)))
            seat_label = seat_element.get_attribute("content-desc") or ""
            assert ", standard," in seat_label, (
                f"Expected {seat_id} to be Standard tier, but its accessibility label was "
                f"{seat_label!r} — refusing to book a seat whose tier wasn't verified."
            )
            seat_element.click()
        wait.until(EC.presence_of_element_located(by_resource_id("seat-selection-continue-button"))).click()
        wait.until(EC.presence_of_element_located(by_resource_id("discount-continue-button"))).click()
        wait.until(EC.presence_of_element_located(by_resource_id("payment-pay-button"))).click()

        WebDriverWait(driver, PAYMENT_WAIT_SECONDS).until(
            EC.invisibility_of_element_located(by_resource_id("payment-processing-indicator"))
        )
        WebDriverWait(driver, PAYMENT_WAIT_SECONDS).until(
            EC.presence_of_element_located(by_resource_id("confirmation-screen"))
        )
        wait.until(EC.presence_of_element_located(by_resource_id("confirmation-done-button"))).click()

        # Step 3: confirm the event sorted to the bottom of the list (sold
        # out). The sold-out card is at the bottom so we must scroll to it.
        wait.until(EC.presence_of_element_located(by_resource_id("event-list-screen")))
        wait.until(EC.presence_of_element_located(scroll_into_view(f"event-card-soldout-{EXPECTED_EVENT_ID}")))
        print(f"[test_event_list_locator] confirmed {EXPECTED_EVENT_TITLE!r} sorted to sold-out position")

        # Step 4: scroll back to the top, then tap the ORIGINAL captured
        # coordinates. Assert the resulting Event Details screen shows the
        # originally expected event.
        wait.until(EC.presence_of_element_located(SCROLL_TO_TOP))
        tap(driver, captured_x, captured_y)
        actual_title = wait.until(EC.presence_of_element_located(by_resource_id("event-details-title"))).text

        print(
            f"[test_event_list_locator] tapped captured coordinates ({captured_x}, {captured_y}) -> opened "
            f"{actual_title!r}"
        )
        assert actual_title == EXPECTED_EVENT_TITLE, (
            f"Tapping the captured position ({captured_x}, {captured_y}) was expected to still open "
            f"{EXPECTED_EVENT_TITLE!r}, but opened {actual_title!r} instead — a different event slid into "
            "that screen position after the re-sort."
        )
    finally:
        driver.quit()
