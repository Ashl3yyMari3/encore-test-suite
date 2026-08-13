# app

**[`encore-release.apk`](encore-release.apk)** — the frozen Encore build used
for this hackathon. Read [`../PRD.md`](../PRD.md) first for what the app is
supposed to do.

## Using it

- **Via Test Companion (in chat)** — connect this APK in the IDE and ask Test
  Companion to run any of the test scripts in [`python/`](../python/README.md),
  [`nodejs/`](../nodejs/README.md), or [`java/`](../java/README.md) against
  it. This is the easiest path.
- **Manually via BrowserStack App Automate** — upload the APK yourself
  (`curl -u "USER:KEY" -X POST "https://api-cloud.browserstack.com/app-automate/upload" -F "file=@app/encore-release.apk"`)
  and use the returned `app_url` (a `bs://...` id) as the `app` value in a
  config file in place of `YOUR_APP_ID`.
- **Sideloading** — install directly on a device/emulator for manual
  exploration: `adb install app/encore-release.apk`.

Nothing to build here — this is a pre-built binary, not source.
