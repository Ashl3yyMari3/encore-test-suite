# java

📄 **[Read the PRD first](../PRD.md)** — the product spec to test against.
📱 **[Get the app](../app/encore-release.apk)** — see [`../app/README.md`](../app/README.md)
for how to connect it.

TestNG + Appium Java client smoke test, run against BrowserStack App Automate via
the BrowserStack Java SDK, configured through `browserstack.yml`.

## Install

Requires JDK 17+ and Maven.

```bash
cd java
mvn install -DskipTests
```

## Test scripts

| File | What it does |
|---|---|
| `SmokeTest.java` | Launches the app and confirms a first screen loads. |
| `EventDateDisplayTest.java` | Opens a fixed event and captures the rendered date/time text for cross-device comparison. |
| `VipBookingPaymentTest.java` | Books a VIP-tier seat through checkout and verifies the confirmation screen appears within a bounded wait. |
| `EventListLocatorTest.java` | Books out an event, then re-opens it from a captured Event List position and confirms the expected event is what actually opens. |

All four are wired into `testng.xml` as separate `<test>` entries.

## Run via Test Companion (in chat)

In Test Companion's chat, ask it to fill in the credentials, app ID, and device
details in browserstack.yml using the app you've connected and your
BrowserStack account, then run one of the files above (e.g. "run
VipBookingPaymentTest.java").

## Run from the command line

```bash
cd java
mvn test -P sample-test
```

This runs the whole suite (every `<test>` in `testng.xml`). To run just one
class, edit `testng.xml` to comment out the others, or ask Test Companion to
run that one file.

(Credentials aren't set up manually here — Test Companion pulls the BrowserStack
Username and Access Key automatically from Chat Settings once you're signed in,
and fills them into `browserstack.yml`.)

## Note on the `app` capability

The `app` value in `browserstack.yml` is deliberately left as the placeholder
`YOUR_APP_ID` (along with `YOUR_USERNAME`, `YOUR_ACCESS_KEY`, `YOUR_DEVICE_NAME`,
and `YOUR_PLATFORM_VERSION`) rather than real values. This is intentional: this
phase is specifically testing whether Test Companion resolves these placeholders
to whatever app is currently connected in the IDE and your BrowserStack account,
or whether real values need to be supplied manually.
