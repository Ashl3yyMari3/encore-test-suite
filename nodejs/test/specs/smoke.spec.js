// Minimal smoke test: launch the app, confirm one screen loads.
// Intentionally shallow — the goal right now is to prove the BrowserStack
// App Automate pipeline works end to end, not to test real functionality.

describe("Encore app smoke test", () => {
  it("launches and loads a first screen", async () => {
    // Placeholder assertion until real locators exist for the frozen app
    // (e.g. the event list screen). For now, a live session with a
    // current activity is enough to prove the pipeline works.
    const activity = await driver.getCurrentActivity();
    expect(activity).not.toBeNull();
  });
});
