# Minimal smoke test: launch the app, confirm one screen loads.
# Intentionally shallow — the goal right now is to prove the BrowserStack
# App Automate pipeline works end to end, not to test real functionality.
#
# Run via the BrowserStack Python SDK (browserstack-sdk), which reads
# ../browserstack.yml for credentials, the app, and device details, and
# transparently redirects this local Appium session to BrowserStack.
# Do not set capabilities here — they live in browserstack.yml.

from appium import webdriver
from appium.options.android import UiAutomator2Options


def test_smoke():
    options = UiAutomator2Options()
    driver = webdriver.Remote("http://localhost:4723/wd/hub", options=options)

    try:
        # Placeholder assertion until real locators exist for the frozen app
        # (e.g. the event list screen). For now, a live session + activity
        # is enough to prove the pipeline works.
        assert driver.current_activity is not None
    finally:
        driver.quit()
