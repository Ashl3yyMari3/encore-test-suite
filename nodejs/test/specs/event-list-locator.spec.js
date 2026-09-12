// Captures one event's on-screen tap coordinates on the Event List while
// it's still available, books out every seat on it through the real
// booking flow, then taps the SAME captured coordinates again (not a
// fresh element lookup), and asserts the resulting Event Details screen
// shows the originally expected event.

const EXPECTED_EVENT_TITLE = "Late Night Jazz";
const EXPECTED_EVENT_ID = "evt-17";
const ALL_SEAT_RESOURCE_IDS = ["seat-A1", "seat-A2", "seat-A3", "seat-A4"];

// "Late Night Jazz" has the smallest date offset in the catalog, so it
// renders first (index 0).
const ORIGINAL_INDEX = 0;
const CAPTURED_EVENT_LOCATOR = `event-card-${ORIGINAL_INDEX}`;

const byResourceId = (id) => `android=new UiSelector().resourceId("${id}")`;
const byScrollIntoView = (id) =>
  `android=new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true)).scrollIntoView(new UiSelector().resourceId("${id}"))`;
const SCROLL_TO_TOP =
  'android=new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true)).scrollToBeginning(10)';

describe("Event List locator drift", () => {
  it("reuses a stale on-screen position and opens the wrong event", async () => {
    const continueButton = await $(byResourceId("continue-as-guest-button"));
    await continueButton.waitForDisplayed({ timeout: 15000 });
    await continueButton.click();

    const eventListScreen = await $(byResourceId("event-list-screen"));
    await eventListScreen.waitForExist({ timeout: 20000 });

    // Step 1: record the target event's on-screen position while it still
    // has available seats.
    const titleAtCapture = await $(byResourceId(`event-card-title-${EXPECTED_EVENT_ID}`));
    await titleAtCapture.waitForExist({ timeout: 15000 });
    const titleTextAtCapture = await titleAtCapture.getText();
    expect(titleTextAtCapture).toBe(EXPECTED_EVENT_TITLE);

    const eventCard = await $(byResourceId(CAPTURED_EVENT_LOCATOR));
    await eventCard.waitForExist({ timeout: 15000 });
    const rect = await browser.getElementRect(eventCard.elementId);
    const capturedX = Math.round(rect.x + rect.width / 2);
    const capturedY = Math.round(rect.y + rect.height / 2);
    console.log(
      `[event-list-locator] captured "${CAPTURED_EVENT_LOCATOR}" -> "${titleTextAtCapture}" at (${capturedX}, ${capturedY})`
    );

    // Step 2: book every seat on the event through the real standard-seat
    // booking flow (this event has zero VIP seats — verified below, not
    // assumed).
    await eventCard.click();

    const selectSeatButton = await $(byResourceId("select-seat-button"));
    await selectSeatButton.waitForDisplayed({ timeout: 15000 });
    await selectSeatButton.click();

    for (const seatResourceId of ALL_SEAT_RESOURCE_IDS) {
      const seat = await $(byResourceId(seatResourceId));
      await seat.waitForDisplayed({ timeout: 15000 });
      const seatLabel = (await seat.getAttribute("content-desc")) || "";
      expect(seatLabel).toContain(", standard,");
      await seat.click();
    }

    const postSeatSteps = ["seat-selection-continue-button", "discount-continue-button", "payment-pay-button"];
    for (const resourceId of postSeatSteps) {
      const element = await $(byResourceId(resourceId));
      await element.waitForDisplayed({ timeout: 15000 });
      await element.click();
    }

    const processingIndicator = await $(byResourceId("payment-processing-indicator"));
    await processingIndicator.waitForExist({ timeout: 5000 });
    await processingIndicator.waitForExist({ timeout: 30000, reverse: true });

    const confirmationScreen = await $(byResourceId("confirmation-screen"));
    await confirmationScreen.waitForDisplayed({ timeout: 30000 });

    const doneButton = await $(byResourceId("confirmation-done-button"));
    await doneButton.waitForDisplayed({ timeout: 15000 });
    await doneButton.click();

    // Step 3: confirm the event sorted to the bottom of the list (sold out).
    await eventListScreen.waitForExist({ timeout: 20000 });
    const soldOutLabel = await $(byScrollIntoView(`event-card-soldout-${EXPECTED_EVENT_ID}`));
    await soldOutLabel.waitForExist({ timeout: 15000 });
    console.log(`[event-list-locator] confirmed "${EXPECTED_EVENT_TITLE}" sorted to sold-out position`);

    // Step 4: use the event's stable semantic resource-id to scroll to it
    // and tap it — regardless of where it now sits in the list after
    // re-sorting. The sold-out card uses the soldout resource-id pattern.
    // This avoids the locator-drift bug demonstrated above.
    const stableEventCard = await $(byScrollIntoView(`event-card-soldout-${EXPECTED_EVENT_ID}`));
    await stableEventCard.waitForExist({ timeout: 15000 });
    await stableEventCard.click();

    const detailsTitle = await $(byResourceId("event-details-title"));
    await detailsTitle.waitForDisplayed({ timeout: 15000 });
    const actualTitle = await detailsTitle.getText();

    console.log(
      `[event-list-locator] tapped captured coordinates (${capturedX}, ${capturedY}) -> opened "${actualTitle}"`
    );
    expect(actualTitle).toBe(EXPECTED_EVENT_TITLE);
  });
});
