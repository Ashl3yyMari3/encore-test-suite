# nodejs

📄 **[Read the PRD first](../PRD.md)** — the product spec to test against.
📱 **[Get the app](../app/encore-release.apk)** — see [`../app/README.md`](../app/README.md)
for how to connect it.

WebdriverIO + Mocha smoke test, run against BrowserStack App Automate, configured
through `wdio.conf.js`.

## Install

Requires Node.js 18+ and npm.

```bash
cd nodejs
npm install
```

## Test scripts

| File | What it does |
|---|---|
| `test/specs/smoke.spec.js` | Launches the app and confirms a first screen loads. |
| `test/specs/event-date-display.spec.js` | Opens a fixed event and captures the rendered date/time text for cross-device comparison. |
| `test/specs/vip-booking-payment.spec.js` | Books a VIP-tier seat through checkout and verifies the confirmation screen appears within a bounded wait. |
| `test/specs/event-list-locator.spec.js` | Books out an event, then re-opens it from a captured Event List position and confirms the expected event is what actually opens. |

## Run via Test Companion (in chat)

In Test Companion's chat, ask it to fill in the credentials, app ID, and device
details in wdio.conf.js using the app you've connected and your BrowserStack
account, then run one of the files above (e.g. "run
vip-booking-payment.spec.js").

## Run from the command line

```bash
cd nodejs
npx wdio run wdio.conf.js
```

This runs every spec matching `wdio.conf.js`'s `specs` glob. To run just one:

```bash
npx wdio run wdio.conf.js --spec test/specs/vip-booking-payment.spec.js
```

(Credentials aren't set up manually here — Test Companion pulls the BrowserStack
Username and Access Key automatically from Chat Settings once you're signed in,
and fills them into `wdio.conf.js`.)

## Note on the `app` capability

The `appium:app` capability in `wdio.conf.js` is deliberately left as the
placeholder `YOUR_APP_ID` (along with `YOUR_USERNAME`, `YOUR_ACCESS_KEY`,
`YOUR_DEVICE_NAME`, and `YOUR_PLATFORM_VERSION`) rather than real values. This
is intentional: this phase is specifically testing whether Test Companion
resolves these placeholders to whatever app is currently connected in the IDE
and your BrowserStack account, or whether real values need to be supplied
manually.
