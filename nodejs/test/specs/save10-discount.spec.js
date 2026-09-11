// SAVE10 discount rule: 10% off Standard seats only, VIP seats at full price.
// Seats: C1 (Standard $65) + A1 (VIP $120)
// Expected total: ($65 × 0.90) + $120.00 = $58.50 + $120.00 = $178.50
//
// BUG (confirmed): App incorrectly applies SAVE10 to VIP seats.
// VIP seat A1 shows $108.00 instead of $120.00; total shows $166.50 instead of $178.50.
// This test will fail until the discount engine is fixed to restrict SAVE10 to Standard seats only.

const EVENT_RESOURCE_ID = "event-card-evt-01"; // "Neon Skyline" — has Standard (rows C–E) and VIP (rows A–B)
const STANDARD_SEAT_ID = "seat-C1";            // Standard, $65
const VIP_SEAT_ID = "seat-A1";                 // VIP, $120

const STANDARD_BASE_PRICE = 65.0;
const VIP_BASE_PRICE = 120.0;
const DISCOUNT_RATE = 0.10;

const byId = (id) => `android=new UiSelector().resourceId("${id}")`;

describe("SAVE10 discount rule", () => {
  it("applies 10% only to Standard seat and leaves VIP at full price", async () => {
    // 1. Continue as Guest
    const guestBtn = await $(byId("continue-as-guest-button"));
    await guestBtn.waitForDisplayed({ timeout: 15000 });
    await guestBtn.click();

    // 2. Wait for event list, scroll to Neon Skyline (evt-01) and open it
    const eventList = await $(byId("event-list"));
    await eventList.waitForExist({ timeout: 20000 });

    const eventCard = await $(
      `android=new UiScrollable(new UiSelector().resourceId("event-list").scrollable(true))` +
      `.scrollIntoView(new UiSelector().resourceId("${EVENT_RESOURCE_ID}"))`
    );
    await eventCard.waitForExist({ timeout: 20000 });
    await eventCard.click();

    // 3. Open seat selection
    const selectSeatBtn = await $(byId("select-seat-button"));
    await selectSeatBtn.waitForDisplayed({ timeout: 15000 });
    await selectSeatBtn.click();

    // 4. Select one Standard seat (C1) and one VIP seat (A1)
    // Wait for seat map to load by waiting for the first seat button
    const standardSeat = await $(byId(STANDARD_SEAT_ID));
    await standardSeat.waitForDisplayed({ timeout: 10000 });
    await standardSeat.click();

    const vipSeat = await $(byId(VIP_SEAT_ID));
    await vipSeat.waitForDisplayed({ timeout: 10000 });
    await vipSeat.click();

    // 5. Continue to discount screen
    const continueToDiscount = await $(byId("seat-selection-continue-button"));
    await continueToDiscount.waitForDisplayed({ timeout: 10000 });
    await continueToDiscount.click();

    // 6. Enter SAVE10 and apply
    // Wait for discount screen by waiting for the input field
    const discountInput = await $(byId("discount-input"));
    await discountInput.waitForDisplayed({ timeout: 10000 });
    await discountInput.setValue("SAVE10");

    const applyBtn = await $(byId("discount-apply-button"));
    await applyBtn.waitForDisplayed({ timeout: 5000 });
    await applyBtn.click();

    // 7. Verify discount-applied label appears (confirms SAVE10 was accepted)
    const appliedLabel = await $(byId("discount-applied-label"));
    await appliedLabel.waitForDisplayed({ timeout: 10000 });

    // 8. Verify Remove button is shown (discount is active) and Apply button is gone
    const removeBtn = await $(byId("discount-remove-button"));
    await expect(removeBtn).toBeDisplayed();

    // 9. Continue to Payment
    const continueToPayment = await $(byId("discount-continue-button"));
    await continueToPayment.waitForDisplayed({ timeout: 10000 });
    await continueToPayment.click();

    // 10. Wait for Payment screen — wait for the subtotal element which is always present
    const paymentSubtotal = await $(byId("payment-subtotal"));
    await paymentSubtotal.waitForDisplayed({ timeout: 15000 });

    // 11. Read actual prices from the payment breakdown
    const standardPriceEl = await $(byId("payment-line-price-C1"));
    await standardPriceEl.waitForDisplayed({ timeout: 10000 });
    const standardPriceText = await standardPriceEl.getText();

    const vipPriceEl = await $(byId("payment-line-price-A1"));
    await vipPriceEl.waitForDisplayed({ timeout: 10000 });
    const vipPriceText = await vipPriceEl.getText();

    const totalEl = await $(byId("payment-total"));
    await totalEl.waitForDisplayed({ timeout: 10000 });
    const totalText = await totalEl.getText();

    // Parse dollar amounts — strip leading "$", "USD " or whitespace
    const parseAmount = (text) => parseFloat(text.replace(/[^0-9.]/g, ""));

    const actualStandardPrice = parseAmount(standardPriceText);
    const actualVipPrice = parseAmount(vipPriceText);
    const actualTotal = parseAmount(totalText);

    // 12. Assert Standard seat has 10% discount applied
    const expectedStandardPrice = parseFloat((STANDARD_BASE_PRICE * (1 - DISCOUNT_RATE)).toFixed(2));
    expect(actualStandardPrice).toBe(
      expectedStandardPrice,
      `Standard seat C1 should be $${expectedStandardPrice} (10% off $${STANDARD_BASE_PRICE}), got $${actualStandardPrice}`
    );

    // 13. Assert VIP seat remains at full price (SAVE10 must NOT discount VIP)
    expect(actualVipPrice).toBe(
      VIP_BASE_PRICE,
      `VIP seat A1 should remain at full price $${VIP_BASE_PRICE}, got $${actualVipPrice}`
    );

    // 14. Assert final total = (Standard × 0.90) + VIP full price
    const expectedTotal = parseFloat((expectedStandardPrice + VIP_BASE_PRICE).toFixed(2));
    expect(actualTotal).toBe(
      expectedTotal,
      `Total should be $${expectedTotal} (discounted Standard + full VIP), got $${actualTotal}`
    );
  });
});
