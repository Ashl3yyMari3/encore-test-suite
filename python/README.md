# python

📄 **[Read the PRD first](../PRD.md)** — the product spec to test against.
📱 **[Get the app](../app/encore-release.apk)** — see [`../app/README.md`](../app/README.md)
for how to connect it.

Appium-Python-Client smoke test, run against BrowserStack App Automate via the
BrowserStack Python SDK (`browserstack-sdk`), configured through `browserstack.yml`.

## Install

Requires Python 3.10+ and pip.

```bash
cd python
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Test scripts

| File | What it does |
|---|---|
| `tests/test_smoke.py` | Launches the app and confirms a first screen loads. |
| `tests/test_event_date_display.py` | Opens a fixed event and captures the rendered date/time text for cross-device comparison. |
| `tests/test_vip_booking_payment.py` | Books a VIP-tier seat through checkout and verifies the confirmation screen appears within a bounded wait. |
| `tests/test_event_list_locator.py` | Books out an event, then re-opens it from a captured Event List position and confirms the expected event is what actually opens. |

## Run via Test Companion (in chat)

In Test Companion's chat, ask it to fill in the credentials, app ID, and device
details in browserstack.yml using the app you've connected and your
BrowserStack account, then run one of the files above (e.g. "run
tests/test_vip_booking_payment.py").

## Run from the command line

```bash
cd python
source .venv/bin/activate
browserstack-sdk pytest tests/test_smoke.py
```

Swap in any of the other file paths from the table above to run that script instead.

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
