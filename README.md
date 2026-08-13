# Encore Test Suite — Hackathon Starter

Starter repo for the Encore testing hackathon. Each language folder
(`python/`, `nodejs/`, `java/`) is a standalone Appium test project that runs
against BrowserStack App Automate — either via **Test Companion** (in chat,
no local setup needed beyond the language toolchain) or from the command
line.

📄 **[Read the PRD first](PRD.md)** — the product spec to test against.
📱 **[Get the app](app/encore-release.apk)** — the frozen build for this
hackathon (see [`app/README.md`](app/README.md) for how to connect it).

This README is the one-stop setup guide: installing Git, installing (and
updating) each language's toolchain on macOS/Windows/Linux, and
troubleshooting the most common problems. Each language folder also has its
own README with the specific install/run steps and a table of what each test
script does.

## Contents

- [PRD](PRD.md) · [App (APK)](app/encore-release.apk)
- [Repository structure](#repository-structure)
- [1. Git](#1-git)
  - [Check whether you already have Git](#check-whether-you-already-have-git)
  - [Installing Git](#installing-git)
  - [Updating Git](#updating-git)
  - [Cloning this repo](#cloning-this-repo)
- [2. Installing a language toolchain](#2-installing-a-language-toolchain)
  - [Python (3.10+)](#python-310)
  - [Node.js (18+)](#nodejs-18)
  - [Java (JDK 17+) and Maven](#java-jdk-17-and-maven)
- [3. Running the tests](#3-running-the-tests)
- [Troubleshooting](#troubleshooting)

---

## Repository structure

```
encore-test-suite/
├── PRD.md                       # product spec — read this first
├── app/
│   └── encore-release.apk        # the frozen app build
├── python/                        # pytest + Appium-Python-Client
├── nodejs/                         # WebdriverIO + Mocha
└── java/                            # TestNG + Appium Java client
```

- [`PRD.md`](PRD.md)
- [`app/encore-release.apk`](app/encore-release.apk) · [`app/README.md`](app/README.md)
- [`python/README.md`](python/README.md)
- [`nodejs/README.md`](nodejs/README.md)
- [`java/README.md`](java/README.md)

You don't need all three language toolchains — just install the one(s) for
the language you're working in.

**[⬆ back to top](#contents)**

---

## 1. Git

You need Git to get a copy of this repo onto your machine and to pull future
updates. If you already have it, skip to [Cloning this repo](#cloning-this-repo).

### Check whether you already have Git

Open a terminal (macOS/Linux) or PowerShell/Command Prompt (Windows) and run:

```bash
git --version
```

If you see something like `git version 2.43.0`, you're set. If you get a
"command not found" / "not recognized" error, install it below.

### Installing Git

**macOS**

The simplest route is via Xcode's Command Line Tools — running `git` for the
first time on a fresh Mac will usually prompt you to install it automatically:

```bash
git --version
```

If that doesn't trigger a prompt, or you'd rather install directly:

```bash
xcode-select --install
```

Alternatively, if you use [Homebrew](https://brew.sh):

```bash
brew install git
```

**Windows**

Download and run the installer from [git-scm.com/download/win](https://git-scm.com/download/win).
Accept the defaults unless you have a reason not to — the default install
also gives you **Git Bash**, a Unix-like terminal that makes the `bash`
command blocks in this README and the sub-READMEs work as-is on Windows too.

Alternatively, with [winget](https://learn.microsoft.com/en-us/windows/package-manager/winget/):

```powershell
winget install --id Git.Git -e --source winget
```

**Linux**

```bash
# Debian / Ubuntu
sudo apt update && sudo apt install git

# Fedora / RHEL / CentOS
sudo dnf install git

# Arch
sudo pacman -S git
```

Full install docs for every platform: [git-scm.com/downloads](https://git-scm.com/downloads).

### Updating Git

```bash
# macOS (Homebrew)
brew upgrade git

# Windows (winget)
winget upgrade --id Git.Git -e --source winget

# Debian / Ubuntu
sudo apt update && sudo apt upgrade git

# Fedora / RHEL
sudo dnf upgrade git
```

Latest release notes: [github.com/git/git/releases](https://github.com/git/git/releases).

### Cloning this repo

```bash
git clone <the repo URL your team shared with you>
cd encore-test-suite
```

If you were handed this repo as a folder directly (e.g. a zip file) rather
than a git URL, you can skip cloning — just `cd` into wherever you extracted
it and continue below.

**[⬆ back to top](#contents)**

---

## 2. Installing a language toolchain

Jump to whichever language(s) you're working in.

### Python (3.10+)

Used by [`python/`](python/README.md). Needs Python and `pip`.
Downloads: [python.org/downloads](https://www.python.org/downloads/) ·
Release schedule: [python.org/downloads (by version)](https://www.python.org/downloads/) ·
What's new each release: [docs.python.org/3/whatsnew](https://docs.python.org/3/whatsnew/).

**macOS**

```bash
brew install python@3.12
python3 --version
```

(macOS ships an old system Python — always use `python3`, not `python`.)

**Windows**

Download the installer from [python.org/downloads](https://www.python.org/downloads/).
**Check "Add python.exe to PATH"** on the first install screen — this is the
single most common setup mistake on Windows. Then verify in a new terminal:

```powershell
python --version
```

**Linux**

Most distros ship Python 3, but confirm the version and install `venv`/`pip`
if missing:

```bash
# Debian / Ubuntu
sudo apt update && sudo apt install python3 python3-venv python3-pip

# Fedora / RHEL
sudo dnf install python3 python3-pip
```

Once Python is installed, follow [`python/README.md`](python/README.md) to
create a virtual environment and install dependencies.

**Updating Python**

The most reliable way to switch/update Python versions on any OS is a
version manager rather than reinstalling from scratch:

- **[pyenv](https://github.com/pyenv/pyenv)** (macOS/Linux) — install a new
  version alongside your existing ones without disturbing anything:
  ```bash
  brew install pyenv        # macOS; see pyenv's README for Linux install
  pyenv install 3.12.7
  pyenv global 3.12.7
  ```
- **[pyenv-win](https://github.com/pyenv-win/pyenv-win)** — the Windows
  equivalent.
- Without a version manager: **macOS** — `brew upgrade python@3.12`;
  **Windows** — download the latest installer from
  [python.org/downloads](https://www.python.org/downloads/) and run it (it
  upgrades in place); **Linux** — `sudo apt upgrade python3` (Debian/Ubuntu)
  or `sudo dnf upgrade python3` (Fedora/RHEL).

### Node.js (18+)

Used by [`nodejs/`](nodejs/README.md). Needs Node.js and `npm` (bundled with
Node). Downloads: [nodejs.org](https://nodejs.org) ·
Release schedule: [nodejs.org/en/about/previous-releases](https://nodejs.org/en/about/previous-releases) ·
Changelogs: [github.com/nodejs/node/releases](https://github.com/nodejs/node/releases).

**macOS**

```bash
brew install node@20
node --version
```

**Windows**

Download the LTS installer from [nodejs.org](https://nodejs.org) and run it
(defaults are fine). Verify in a new terminal:

```powershell
node --version
npm --version
```

**Linux**

Distro package managers often carry outdated Node versions — using
[nvm](https://github.com/nvm-sh/nvm) is more reliable across all three OSes,
including Linux:

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
# restart your terminal, then:
nvm install 20
nvm use 20
```

Once Node is installed, follow [`nodejs/README.md`](nodejs/README.md) to run
`npm install`.

**Updating Node.js**

- **[nvm](https://github.com/nvm-sh/nvm)** (macOS/Linux):
  ```bash
  nvm install --lts
  nvm use --lts
  nvm alias default node   # make it the default for new terminals
  ```
- **[nvm-windows](https://github.com/coreybutler/nvm-windows)** — the
  Windows equivalent (regular `nvm` doesn't run natively on Windows):
  ```powershell
  nvm install lts
  nvm use <version installed above>
  ```
- Without a version manager: **macOS** — `brew upgrade node@20`;
  **Windows** — download the latest LTS installer from
  [nodejs.org](https://nodejs.org) and run it; **Linux** — reinstall via
  nvm above rather than the distro package manager, to avoid version drift.

### Java (JDK 17+) and Maven

Used by [`java/`](java/README.md). Needs a JDK and Maven.
JDK downloads: [Eclipse Temurin](https://adoptium.net/temurin/releases/) ·
Maven downloads: [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) ·
Maven release notes: [maven.apache.org/docs/history.html](https://maven.apache.org/docs/history.html).

**macOS**

```bash
brew install openjdk@17 maven
# Homebrew installs openjdk unlinked by default — link it so `java` resolves:
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
java -version
mvn -version
```

**Windows**

Install a JDK — e.g. [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17)
— then [Maven](https://maven.apache.org/download.cgi) (download the binary
zip, extract it, and add its `bin` folder to your `PATH`). Verify in a new
terminal:

```powershell
java -version
mvn -version
```

If `JAVA_HOME` isn't set, Maven will fail with a `JAVA_HOME not found`-style
error — see [Troubleshooting](#troubleshooting) below.

**Linux**

```bash
# Debian / Ubuntu
sudo apt update && sudo apt install openjdk-17-jdk maven

# Fedora / RHEL
sudo dnf install java-17-openjdk-devel maven
```

Once installed, follow [`java/README.md`](java/README.md) to build the
project.

**Updating Java / Maven**

- **[SDKMAN!](https://sdkman.io)** (macOS/Linux) — the easiest way to manage
  and switch between multiple JDK and Maven versions:
  ```bash
  curl -s "https://get.sdkman.io" | bash
  sdk install java 17.0.13-tem   # or any version listed by `sdk list java`
  sdk install maven
  sdk use java 17.0.13-tem
  ```
- **[jabba](https://github.com/shyiko/jabba)** — a cross-platform (including
  Windows) JDK version manager if you'd rather not use SDKMAN!'s WSL/Git
  Bash requirement on Windows.
- Without a version manager: **macOS** — `brew upgrade openjdk@17 maven`;
  **Windows** — download a newer installer from
  [adoptium.net](https://adoptium.net/temurin/releases/) and/or a newer
  Maven zip from [maven.apache.org](https://maven.apache.org/download.cgi);
  **Linux** — `sudo apt upgrade openjdk-17-jdk maven` (Debian/Ubuntu) or
  `sudo dnf upgrade java-17-openjdk-devel maven` (Fedora/RHEL).

**[⬆ back to top](#contents)**

---

## 3. Running the tests

Each test can be run two ways:

1. **Via Test Companion, in chat** — ask it to fill in your credentials, app
   ID, and device details in that language's config file
   (`browserstack.yml` for Python/Java, `wdio.conf.js` for Node), then run a
   specific test file by name. This is the easiest path and doesn't require
   you to manage BrowserStack credentials yourself.
2. **From the command line** — see each language's README for the exact
   command. You'll need `browserstack.yml`/`wdio.conf.js` filled in with real
   values first (either by Test Companion or by hand) for this to actually
   connect to BrowserStack.

The config files ship with placeholder values (`YOUR_USERNAME`,
`YOUR_ACCESS_KEY`, `YOUR_APP_ID`, `YOUR_DEVICE_NAME`, `YOUR_PLATFORM_VERSION`)
— this is intentional, not a bug. Don't hardcode real credentials into these
files if you're going to share or commit the repo further; let Test
Companion (or your own local, uncommitted overrides) supply them.

**[⬆ back to top](#contents)**

---

## Troubleshooting

**`git`, `python`, `node`, or `java`/`mvn` — "command not found" / "not
recognized"**
The tool isn't on your `PATH`. On Windows this is almost always the Python
installer's "Add to PATH" checkbox being missed, or a new terminal window
not having been opened after install (PATH changes only apply to new
terminal sessions). Close and reopen your terminal after any install before
concluding something is broken.

**macOS: `python` works but `python3` doesn't, or vice versa**
Use `python3` — macOS ships an old, separate system Python 2/3 alongside
whatever you install via Homebrew, and `python` may point at either
depending on your shell setup. All commands in this repo use `python3`
deliberately.

**Windows: `venv/bin/activate` doesn't exist**
On Windows the virtual environment's activate script lives at
`.venv\Scripts\activate` (Command Prompt) or `.venv\Scripts\Activate.ps1`
(PowerShell), not `.venv/bin/activate`. If you're using **Git Bash**, the
Unix-style path (`source .venv/Scripts/activate`) works.

**Windows PowerShell: "running scripts is disabled on this system"**
PowerShell's default execution policy blocks activation scripts. Either use
Command Prompt / Git Bash instead, or run once (per session, not
permanently changing security settings):
```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

**Java: `mvn` fails with something about `JAVA_HOME`**
Maven needs `JAVA_HOME` pointed at your JDK install, not just `java` on your
`PATH`.
```bash
# macOS/Linux — check where your JDK actually lives, then:
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
# or, on Linux, wherever your package manager installed it, e.g.:
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```
```powershell
:: Windows — set JAVA_HOME to your JDK's install folder (System Properties
:: → Environment Variables), then reopen your terminal.
```

**Java: dependency version conflicts / `cannot access org.openqa.selenium.X`
during `mvn test-compile`**
This class of error means two dependencies pulled in incompatible Selenium
versions — Maven silently mediates to one version, and if it's newer than
what `appium-java-client` was built against, classes it expects can be
missing. `java/pom.xml` already pins this correctly (`selenium-bom` in
`dependencyManagement`, forcing one consistent Selenium version everywhere);
if you bump `appium.java.client.version` or `selenium.version` yourself,
re-run `mvn dependency:tree -Dincludes=org.seleniumhq.selenium` afterward and
confirm every Selenium artifact resolves to the *same* version.

**Python: `pip install -r requirements.txt` fails with a dependency
conflict**
Usually means `Appium-Python-Client` and `selenium` are pinned to
incompatible versions relative to each other (Appium-Python-Client declares
a minimum compatible Selenium version). Check the specific version
constraint in the error message and bump `selenium` in `requirements.txt` to
satisfy it.

**BrowserStack session fails immediately with `401 Invalid username or
password` / `Authorization Required`**
The config file (`browserstack.yml` or `wdio.conf.js`) still has placeholder
credentials. Either have Test Companion fill them in, or replace
`YOUR_USERNAME` / `YOUR_ACCESS_KEY` with real values from your BrowserStack
account settings.

**A test hangs or times out waiting on an element that should be there**
Some BrowserStack devices run older Appium server versions with partial
support for newer Appium/Selenium conveniences (e.g. `mobile: clickGesture`
isn't supported everywhere, and W3C Actions payloads sometimes need an
explicit `origin` and small pauses between `pointerDown`/`pointerUp` to
register as a real tap on some devices). If a gesture-based step is silently
not working, prefer the plain W3C Actions pointer/touch sequence over
higher-level convenience wrappers, and check the BrowserStack session's
video/logs (linked in the terminal output) to see what actually happened on
screen.

**Still stuck?**
Check the specific language folder's README for that project's exact
pinned versions and run commands, and check the BrowserStack dashboard link
printed at the end of a run for a full session video, device logs, and
network logs.

**[⬆ back to top](#contents)**
