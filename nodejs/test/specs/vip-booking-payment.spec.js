// Books a VIP-tier seat through checkout and confirms the Confirmation
// screen appears within a bounded wait after tapping "Pay Now".

const EVENT_RESOURCE_ID = "event-card-evt-01"; // "Neon Skyline" — has a VIP row (A)
const VIP_SEAT_RESOURCE_ID = "seat-A1"; // row A is VIP tier for this event's venue

const byResourceId = (id) => `android=new UiSelector().resourceId("${id}")`;

describe("VIP booking payment", () => {
  it("reaches the confirmation screen after paying for a VIP seat", async () => {
    // Steps before the event card — click directly
    const preEventSteps = ["continue-as-guest-button"];
    for (const resourceId of preEventSteps) {
      const element = await $(byResourceId(resourceId));
      await element.waitForDisplayed({ timeout: 15000 });
      await element.click();
    }

    // Wait for event list, then scroll to evt-01 and click
    const eventListScreen = await $(byResourceId("event-list-screen"));
    await eventListScreen.waitForExist({ timeout: 20000 });
    const eventCard = await $(`android=new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true)).scrollIntoView(new UiSelector().resourceId("${EVENT_RESOURCE_ID}"))`);
    await eventCard.waitForExist({ timeout: 20000 });
    await eventCard.click();

    // Remaining steps after event selection
    const postEventSteps = [
      "select-seat-button",
      VIP_SEAT_RESOURCE_ID,
      "seat-selection-continue-button",
      "discount-continue-button",
      "payment-pay-button",
    ];
    for (const resourceId of postEventSteps) {
      const element = await $(byResourceId(resourceId));
      await element.waitForDisplayed({ timeout: 15000 });
      await element.click();
    }

    // Wait for processing spinner to disappear, then assert confirmation screen
    const processingIndicator = await $(byResourceId("payment-processing-indicator"));
    await processingIndicator.waitForExist({ timeout: 5000 });
    await processingIndicator.waitForExist({ timeout: 30000, reverse: true });
    const confirmationScreen = await $(byResourceId("confirmation-screen"));
    await confirmationScreen.waitForDisplayed({ timeout: 30000 });
  });
});
