exports.config = {
  hostname: "hub-cloud.browserstack.com",
  path: "/wd/hub",
  user: "YOUR_USERNAME",
  key: "YOUR_ACCESS_KEY",

  specs: ["./test/specs/**/*.js"],
  maxInstances: 1,

  capabilities: [
    {
      platformName: "Android",
      "appium:app": "YOUR_APP_ID",
      "appium:deviceName": "YOUR_DEVICE_NAME",
      "appium:platformVersion": "YOUR_PLATFORM_VERSION",
      "bstack:options": {
        projectName: "Encore Hackathon",
        buildName: "Encore Hackathon",
        sessionName: "Smoke test",
      },
    },
  ],

  logLevel: "info",
  framework: "mocha",
  reporters: ["spec"],
  mochaOpts: { ui: "bdd", timeout: 60000 },
};
